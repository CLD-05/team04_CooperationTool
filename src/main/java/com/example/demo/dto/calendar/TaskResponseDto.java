package com.example.demo.dto.calendar;

import java.time.LocalDateTime;

import com.example.demo.entity.Task;
import com.example.demo.type.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class TaskResponseDto {
	
	private Long id;
	
	private String title;
	private String content;
	
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	
	private TaskStatus taskStatus;
	
	private Long teamId;
	
	public static TaskResponseDto of(String title, String content, LocalDateTime startAt, LocalDateTime endAt, TaskStatus taskStatus) {
		return TaskResponseDto.of(null, title, content, startAt, endAt, taskStatus, null);
	}
	
	// 조회용 DTO
	public static TaskResponseDto of(Long id, String title, String content, LocalDateTime startAt, LocalDateTime endAt, TaskStatus taskStatus, Long teamId) {
		return new TaskResponseDto(id, title, content, startAt, endAt, taskStatus, teamId);
	}
	
	// Entity -> DTO 변환
	public static TaskResponseDto from(Task task) {
		return new TaskResponseDto(
									task.getId(),
									task.getTitle(),
									task.getContent(),
									task.getStartAt(),
									task.getEndAt(),
									task.getTaskStatus(),
									task.getTeam() !=null ? task.getTeam().getId() : null);
	}
}
