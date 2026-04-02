package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.dto.calendar.TaskResponseDto;
import com.example.demo.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long>{

	List<Task> findByTeamId(Long teamId);

	Optional<Task> findByTeamIdAndId(Long tid, Long taskId);

	void deleteByTeamIdAndId(Long tid, Long taskId);

	Page<Task> findByTeamId(Long tid, Pageable pageable);
}
