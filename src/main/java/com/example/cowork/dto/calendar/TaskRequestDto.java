package com.example.cowork.dto.calendar;

import java.time.LocalDateTime;

import com.example.cowork.entity.Task;
import com.example.cowork.entity.Team;
import com.example.cowork.type.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class TaskRequestDto {
	
	private Long id;
	private String title;
	private String content;
	private LocalDateTime startAt;
	private LocalDateTime endAt;
	private TaskStatus taskStatus;
	
	public Task toEntity(Team team) {
		return new Task(
						null,
						team,
						title,
						content,
						taskStatus,
						startAt,
						endAt,
						null
				);
	}
	
}
