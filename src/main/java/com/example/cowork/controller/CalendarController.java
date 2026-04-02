package com.example.cowork.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.cowork.dto.calendar.TaskRequestDto;
import com.example.cowork.dto.calendar.TaskResponseDto;
import com.example.cowork.service.CalendarService;
import com.example.cowork.service.PagingService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/teams")
@Controller
public class CalendarController {
	
	private final CalendarService calendarService;
	private final PagingService pagingService;
	
	// 모든 캘린더 조회
	@GetMapping("/{team_id}/calendar")
	public String getCalendars(@PathVariable("team_id") Long tid, 
							   @PageableDefault(size = 5) Pageable pageable,
								ModelMap map) {
		// 페이지 처리
		Page<TaskResponseDto> calendars = calendarService.getCalendarWithPage(tid, pageable);
										 
		// 일정 조회
		List<Integer> pagingNumbers = pagingService.getPagingNumbers(pageable.getPageNumber() + 1, calendars.getTotalPages());
		
		map.addAttribute("pagingNumbers", pagingNumbers);
		map.addAttribute("teamName", calendarService.getTeam(tid).getName());
		map.addAttribute("calendarAll", calendars);
		map.addAttribute("teamId", tid);
		return "teams/calendar";
	}
	
	// 캘린더 등록 폼 
	@GetMapping("/{team_id}/calendar/new")
	public String postCalendarPage(@PathVariable("team_id") Long tid,
									ModelMap map) {
		map.addAttribute("teamId", tid);
		map.addAttribute("teamName", calendarService.getTeam(tid).getName());
		return "teams/calendar_form";
	}
	
	// 캘린더 등록 처리
	@PostMapping("/{team_id}/calendar")
	public String registerCalendar(@PathVariable("team_id") Long tid,
								TaskRequestDto taskRequestDto,
								ModelMap map) {
		try {
			calendarService.registerCalendar(tid, taskRequestDto);
		} catch (IllegalArgumentException e) {
			map.addAttribute("errorMessage", e.getMessage());
			map.addAttribute("teamId", tid);
			return "teams/calendar_form";
		}
		
		return "redirect:/teams/{team_id}/calendar";
	}
	
	// 캘린더 수정 폼
	@GetMapping("/{team_id}/calendar/{task_id}/edit")
	public String updateCalendarFormpage(@PathVariable("team_id") Long tid,
										 @PathVariable("task_id") Long taskId,
										 ModelMap map) {
		TaskResponseDto task =  calendarService.getCalendar(tid, taskId);
		map.addAttribute("teamName", calendarService.getTeam(tid).getName());
		map.addAttribute("calendarOne", task);
		map.addAttribute("teamId", tid);
		return "teams/calendar_form";
	}
	
	@PostMapping("/{team_id}/calendar/{task_id}/edit")
	public String updateCalendar(@PathVariable("team_id") Long tid,
							 @PathVariable("task_id") Long taskId,
							 TaskRequestDto taskRequestDto, ModelMap map) {
		try {
			calendarService.updateCalendar(tid, taskId, taskRequestDto);
		} catch (IllegalArgumentException e) {
			map.addAttribute("errorMessage", e.getMessage());
			map.addAttribute("teamId", tid);
			return "teams/calendar_form";
		}
		
		return "redirect:/teams/{team_id}/calendar";
	}
	
	@PostMapping("/{team_id}/calendar/{task_id}/delete")
	public String deleteCalendar(@PathVariable("team_id") Long tid,
								 @PathVariable("task_id") Long taskId) {
		calendarService.deleteCalendar(tid, taskId);
		return "redirect:/teams/{team_id}/calendar";
	}
}
