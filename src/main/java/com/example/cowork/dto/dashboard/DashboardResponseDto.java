package com.example.cowork.dto.dashboard;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DashboardResponseDto {

    private String userName;
    private List<TeamCardDto> teams;
    private List<InviteCardDto> invites;
}