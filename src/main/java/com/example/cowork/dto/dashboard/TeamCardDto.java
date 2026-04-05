package com.example.cowork.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamCardDto {
    private Long id;
    private String name;
    private String description;
    private boolean leader;  // 본인이 팀장인지 여부
}