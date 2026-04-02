package com.example.cowork.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.cowork.type.TaskStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "team")
@Entity
public class Task {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "team_id", nullable = false)
	private Team team;
	
	@Setter
	private String title;
	
	@Setter
	private String content;
	
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "task_status", columnDefinition = "VARCHAR(50)")
	private TaskStatus taskStatus;
	
	@Setter
	@Column(name = "start_at")
	private LocalDateTime startAt;
	
	@Setter
	@Column(name = "end_at")
	private LocalDateTime endAt;
	
	@CreationTimestamp
	@Column(name = "created_at")
	private LocalDateTime createdAt;

	public void updateTitleAndContentAndTaskStatusAndStartAtAndEndAt(String title, String content,
			TaskStatus taskStatus, LocalDateTime startAt, LocalDateTime endAt) {
		this.title = title;
		this.content = content;
		this.taskStatus = taskStatus;
		this.startAt = startAt;
		this.endAt = endAt;
	}

	
}
