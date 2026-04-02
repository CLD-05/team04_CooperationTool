package com.example.cowork.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "leader_id")
    private User leader;

    // 서비스에서 new Team(name, description, leader) 쓰기 위해 추가
    public Team(String name, String description, User leader) {
        this.name = name;
        this.description = description;
        this.leader = leader;
    }
}