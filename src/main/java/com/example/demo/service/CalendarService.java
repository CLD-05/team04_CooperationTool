package com.example.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dto.calendar.TaskRequestDto;
import com.example.demo.dto.calendar.TaskResponseDto;
import com.example.demo.entity.Task;
import com.example.demo.entity.Team;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.TeamRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CalendarService {
	
	private final TaskRepository taskRepository;
	private final TeamRepository teamRepository;
	
	// 모든 캘린더 조회
	@Transactional
	public List<TaskResponseDto> getCalendars(Long tid) {
		return taskRepository.findByTeamId(tid)
							.stream()
							.map(TaskResponseDto::from)
							.toList();
	}
	
	// 해당 캘린더 가져오기 => 수정할 때 필요
	@Transactional
	public TaskResponseDto getCalendar(Long tid, Long taskId) {
		Task task = taskRepository.findByTeamIdAndId(tid, taskId).orElseThrow(() -> new IllegalArgumentException("존재하는 일정이 없습니다."));
		return TaskResponseDto.from(task);
	}
	
	public Team getTeam(Long tid) {
		return teamRepository.findById(tid).orElseThrow(() -> new IllegalArgumentException("팀이 존재하지 않습니다."));
	}
	
	// 캘린더 등록
	@Transactional
	public void registerCalendar(Long tid, TaskRequestDto taskRequestDto) {
		if (taskRequestDto.getStartAt().isAfter(taskRequestDto.getEndAt())) {
	        throw new IllegalArgumentException("시작 시간은 종료 시간보다 늦을 수 없습니다.");
	    }
		
		// teamId 가져오기
		Team team = teamRepository.findById(tid)
									.orElseThrow(() -> new IllegalArgumentException("팀이 존재하지 않습니다."));
		
		// Dto -> entity 변환
		Task task = taskRequestDto.toEntity(team);
		
		// DB에 저장
		taskRepository.save(task);
	}
	
	// 캘린더 수정
	@Transactional
	public void updateCalendar(Long tid, Long taskId, TaskRequestDto taskRequestDto) {
		if (taskRequestDto.getStartAt().isAfter(taskRequestDto.getEndAt())) {
	        throw new IllegalArgumentException("시작 시간은 종료 시간보다 늦을 수 없습니다.");
	    }
		
		Task task = taskRepository.findByTeamIdAndId(tid, taskId).orElseThrow(() -> new IllegalArgumentException("존재하는 일정이 없습니다."));
		task.updateTitleAndContentAndTaskStatusAndStartAtAndEndAt(taskRequestDto.getTitle(),
																  taskRequestDto.getContent(),
																  taskRequestDto.getTaskStatus(),
																  taskRequestDto.getStartAt(),
																  taskRequestDto.getEndAt());
	}
	
	// 캘린더 삭제
	@Transactional
	public void deleteCalendar(Long tid, Long taskId) {
		taskRepository.deleteByTeamIdAndId(tid, taskId);
	}

	public Page<TaskResponseDto> getCalendarWithPage(Long tid, Pageable pageable) {
		return taskRepository.findByTeamId(tid, pageable).map(TaskResponseDto::from);
	}
}
