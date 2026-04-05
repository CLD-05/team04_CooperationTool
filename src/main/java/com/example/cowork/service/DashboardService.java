package com.example.cowork.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cowork.dto.dashboard.DashboardResponseDto;
import com.example.cowork.dto.dashboard.InviteCardDto;
import com.example.cowork.dto.dashboard.TeamCardDto;
import com.example.cowork.entity.TeamMember;
import com.example.cowork.repository.TeamMemberRepository;
import com.example.cowork.repository.UserRepository;
import com.example.cowork.type.MemberStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public DashboardResponseDto getDashboardData(Long userId) {

        String nickname = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."))
                .getNickname();

        // 참여 중인 팀 (JOINED 또는 ACCEPTED 상태)
        List<TeamMember> myTeamMembers = teamMemberRepository.findAllByUserId(userId)
                .stream()
                .filter(tm -> tm.getStatus() == MemberStatus.JOINED
                           || tm.getStatus() == MemberStatus.ACCEPTED)
                .collect(Collectors.toList());

        List<TeamCardDto> teamCardList = myTeamMembers.stream()
                .map(tm -> new TeamCardDto(
                        tm.getTeam().getId(),
                        tm.getTeam().getName(),
                        tm.getTeam().getDescription(),
                        tm.getRole() == com.example.cowork.type.MemberRoleType.LEADER
                ))
                .collect(Collectors.toList());

        // 초대 대기 목록 (INVITED 상태)
        List<InviteCardDto> inviteList = teamMemberRepository
                .findAllByUserIdAndStatus(userId, MemberStatus.INVITED)
                .stream()
                .map(tm -> new InviteCardDto(
                        tm.getTeam().getId(),
                        tm.getTeam().getName(),
                        tm.getTeam().getLeader().getNickname()
                ))
                .collect(Collectors.toList());

        return new DashboardResponseDto(nickname + "님", teamCardList, inviteList);
    }
}