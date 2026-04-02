package com.example.teamProject1.dto;

import com.example.teamProject1.entity.Team;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamDto {

    private Integer id;
    private String name;
    private String description;
    private Integer leaderId;
    private String leaderUsername;

    public static TeamDto fromEntity(Team team) {
        return new TeamDto(
                team.getId(),
                team.getName(),
                team.getDescription(),
                team.getLeader().getId(),
                team.getLeader().getUsername()
        );
    }
}