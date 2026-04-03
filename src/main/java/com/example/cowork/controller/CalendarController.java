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
import com.example.cowork.entity.Team;
import com.example.cowork.service.CalendarService;
import com.example.cowork.service.PagingService;
import com.example.cowork.service.TeamMemberService;
import com.example.cowork.type.MemberRoleType;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/teams")
@Controller
public class CalendarController {

    private final CalendarService calendarService;
    private final PagingService pagingService;
    private final TeamMemberService teamMemberService;

    private boolean checkIsLeader(Long teamId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return false;
        try {
            return teamMemberService.getMemberInfo(teamId, userId).getRole() == MemberRoleType.LEADER;
        } catch (Exception e) {
            return false;
        }
    }

    private void setCommonAttributes(Long tid, HttpSession session, ModelMap map) {
        Team team = calendarService.getTeam(tid);
        map.addAttribute("teamName", team.getName());
        map.addAttribute("teamDescription",
                team.getDescription() != null ? team.getDescription() : "프로젝트 자료 및 일정 관리");
        map.addAttribute("teamId", tid);
        map.addAttribute("isLeader", checkIsLeader(tid, session));
        map.addAttribute("currentUser", session.getAttribute("userNickname"));
    }

    // 캘린더 목록 (등록/수정 모달 포함)
    @GetMapping("/{team_id}/calendar")
    public String getCalendars(@PathVariable("team_id") Long tid,
                               @PageableDefault(size = 5) Pageable pageable,
                               HttpSession session,
                               ModelMap map) {

        if (session.getAttribute("userId") == null) return "redirect:/api/user/login";

        Page<TaskResponseDto> calendars = calendarService.getCalendarWithPage(tid, pageable);
        List<Integer> pagingNumbers = pagingService.getPagingNumbers(
                pageable.getPageNumber() + 1, calendars.getTotalPages());

        setCommonAttributes(tid, session, map);
        map.addAttribute("calendarAll", calendars);
        map.addAttribute("pagingNumbers", pagingNumbers);
        return "calendar/calendar";
    }

    // 일정 등록 처리 (모달 form POST)
    @PostMapping("/{team_id}/calendar")
    public String registerCalendar(@PathVariable("team_id") Long tid,
                                   TaskRequestDto taskRequestDto,
                                   HttpSession session,
                                   ModelMap map) {
        try {
            calendarService.registerCalendar(tid, taskRequestDto);
        } catch (IllegalArgumentException e) {
            // 에러 시 목록 페이지로 돌아가면서 메시지 표시
            Page<TaskResponseDto> calendars = calendarService.getCalendarWithPage(tid, Pageable.ofSize(5));
            List<Integer> pagingNumbers = pagingService.getPagingNumbers(1, calendars.getTotalPages());
            setCommonAttributes(tid, session, map);
            map.addAttribute("calendarAll", calendars);
            map.addAttribute("pagingNumbers", pagingNumbers);
            map.addAttribute("errorMessage", e.getMessage());
            return "calendar/calendar";
        }
        return "redirect:/teams/{team_id}/calendar";
    }

    // 일정 수정 처리 (모달 form POST)
    @PostMapping("/{team_id}/calendar/{task_id}/edit")
    public String updateCalendar(@PathVariable("team_id") Long tid,
                                 @PathVariable("task_id") Long taskId,
                                 TaskRequestDto taskRequestDto,
                                 HttpSession session,
                                 ModelMap map) {
        try {
            calendarService.updateCalendar(tid, taskId, taskRequestDto);
        } catch (IllegalArgumentException e) {
            Page<TaskResponseDto> calendars = calendarService.getCalendarWithPage(tid, Pageable.ofSize(5));
            List<Integer> pagingNumbers = pagingService.getPagingNumbers(1, calendars.getTotalPages());
            setCommonAttributes(tid, session, map);
            map.addAttribute("calendarAll", calendars);
            map.addAttribute("pagingNumbers", pagingNumbers);
            map.addAttribute("errorMessage", e.getMessage());
            return "calendar/calendar";
        }
        return "redirect:/teams/{team_id}/calendar";
    }

    // 일정 삭제
    @PostMapping("/{team_id}/calendar/{task_id}/delete")
    public String deleteCalendar(@PathVariable("team_id") Long tid,
                                 @PathVariable("task_id") Long taskId) {
        calendarService.deleteCalendar(tid, taskId);
        return "redirect:/teams/{team_id}/calendar";
    }
}