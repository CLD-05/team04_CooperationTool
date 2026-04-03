package com.example.cowork.dto.dashboard;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DashboardResponseDto {

    private String userName;
    private List<TeamCardDto> teams;
    private int totalPages;
    private long totalElements;
    private List<InviteCardDto> invites;  // 초대 대기 목록

    // 기존 생성자 호환용 (invites 없는 경우)
    public DashboardResponseDto(String userName, List<TeamCardDto> teams,
                                int totalPages, long totalElements) {
        this.userName = userName;
        this.teams = teams;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.invites = List.of();
    }
}