package com.example.cowork.dto.calendar;

import java.time.LocalDateTime;

import com.example.cowork.entity.Task;
import com.example.cowork.type.TaskStatus;

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
    private String author;       // 작성자
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private TaskStatus taskStatus;
    private Long teamId;

    // Entity -> DTO 변환
    public static TaskResponseDto from(Task task) {
        return new TaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getContent(),
                task.getAuthor(),
                task.getStartAt(),
                task.getEndAt(),
                task.getTaskStatus(),
                task.getTeam() != null ? task.getTeam().getId() : null
        );
    }

    // 조회용 (author 포함)
    public static TaskResponseDto of(Long id, String title, String content, String author,
                                     LocalDateTime startAt, LocalDateTime endAt,
                                     TaskStatus taskStatus, Long teamId) {
        return new TaskResponseDto(id, title, content, author, startAt, endAt, taskStatus, teamId);
    }
}