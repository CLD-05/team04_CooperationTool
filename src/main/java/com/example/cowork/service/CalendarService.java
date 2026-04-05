package com.example.cowork.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cowork.dto.calendar.TaskRequestDto;
import com.example.cowork.dto.calendar.TaskResponseDto;
import com.example.cowork.entity.Task;
import com.example.cowork.entity.Team;
import com.example.cowork.repository.TaskRepository;
import com.example.cowork.repository.TeamRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CalendarService {

    private final TaskRepository taskRepository;
    private final TeamRepository teamRepository;

    public Team getTeam(Long tid) {
        return teamRepository.findById(tid)
                .orElseThrow(() -> new IllegalArgumentException("팀이 존재하지 않습니다."));
    }

    // 캘린더 등록
    @Transactional
    public void registerCalendar(Long tid, TaskRequestDto dto) {
        if (dto.getStartAt() == null || dto.getEndAt() == null) {
            throw new IllegalArgumentException("시작/종료 시간을 입력해주세요.");
        }
        if (dto.getStartAt().isAfter(dto.getEndAt())) {
            throw new IllegalArgumentException("시작 시간은 종료 시간보다 늦을 수 없습니다.");
        }
        Team team = teamRepository.findById(tid)
                .orElseThrow(() -> new IllegalArgumentException("팀이 존재하지 않습니다."));
        taskRepository.save(dto.toEntity(team));
    }

    // 캘린더 수정
    @Transactional
    public void updateCalendar(Long tid, Long taskId, TaskRequestDto dto) {
        if (dto.getStartAt() == null || dto.getEndAt() == null) {
            throw new IllegalArgumentException("시작/종료 시간을 입력해주세요.");
        }
        if (dto.getStartAt().isAfter(dto.getEndAt())) {
            throw new IllegalArgumentException("시작 시간은 종료 시간보다 늦을 수 없습니다.");
        }

        Task task = taskRepository.findByTeamIdAndId(tid, taskId)
                .orElseThrow(() -> new IllegalArgumentException("존재하는 일정이 없습니다."));

        String newContent = (dto.getContent() != null && !dto.getContent().isBlank())
                ? dto.getContent()
                : task.getContent();

        String autoTitle = (newContent != null && !newContent.isBlank())
                ? (newContent.length() > 20 ? newContent.substring(0, 20) + "..." : newContent)
                : "(내용 없음)";

        String newAuthor = (dto.getAuthor() != null && !dto.getAuthor().isBlank())
                ? dto.getAuthor()
                : task.getAuthor();

        task.updateTitleAndContentAndAuthorAndTaskStatusAndStartAtAndEndAt(
                autoTitle,
                newContent,
                newAuthor,
                dto.getTaskStatus() != null ? dto.getTaskStatus() : task.getTaskStatus(),
                dto.getStartAt(),
                dto.getEndAt()
        );
    }

    // 캘린더 삭제
    @Transactional
    public void deleteCalendar(Long tid, Long taskId) {
        taskRepository.deleteByTeamIdAndId(tid, taskId);
    }

    // 페이징 조회
    @Transactional(readOnly = true)
    public Page<TaskResponseDto> getCalendarWithPage(Long tid, Pageable pageable) {
        return taskRepository.findByTeamId(tid, pageable).map(TaskResponseDto::from);
    }
}