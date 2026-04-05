package com.example.cowork.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InviteCardDto {
    private Long teamId;
    private String teamName;
    private String leaderName;  // 초대한 팀장 닉네임
}