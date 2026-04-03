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
    private String author;       // 작성자
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private TaskStatus taskStatus;

    public Task toEntity(Team team) {
        // title 입력란 제거 → content 앞 20자를 title로 자동 설정
        String autoTitle = (content != null && !content.isBlank())
                ? (content.length() > 20 ? content.substring(0, 20) + "..." : content)
                : "(내용 없음)";
        return new Task(
                null,
                team,
                autoTitle,
                content,
                author,
                taskStatus,
                startAt,
                endAt,
                null
        );
    }
}