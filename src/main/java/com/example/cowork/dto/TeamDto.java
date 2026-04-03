package com.example.cowork.dto;

import com.example.cowork.entity.Team;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamDto {

    private Long id;
    private String name;
    private String description;
    private Long leaderId;
    //private String leaderUsername;

    public static TeamDto fromEntity(Team team) {
        return new TeamDto(
                team.getId(),
                team.getName(),
                team.getDescription(),
                team.getLeader().getId()
              
        );
    }
}