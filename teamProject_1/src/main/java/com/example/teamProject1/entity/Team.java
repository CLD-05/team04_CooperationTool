package com.example.teamProject1.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "Team")
public class Team {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name = "id")
	private Long id;
	
	@Column(name = "name", nullable = false)
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "description", length = 500)
    private String description;
	

}
