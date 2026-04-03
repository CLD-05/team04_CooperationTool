package com.example.cowork.service;

import com.example.cowork.dto.team.MemberInviteRequest;
import com.example.cowork.dto.team.TeamMemberResponse;
import com.example.cowork.entity.Team;
import com.example.cowork.entity.TeamMember;
import com.example.cowork.entity.User;
import com.example.cowork.repository.TeamMemberRepository;
import com.example.cowork.repository.TeamRepository;
import com.example.cowork.repository.UserRepository;
import com.example.cowork.type.MemberRoleType;
import com.example.cowork.type.MemberStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    // 특정 유저의 팀 내 역할 조회
    @Transactional(readOnly = true)
    public TeamMemberResponse getMemberInfo(Long teamId, Long userId) {
        TeamMember member = teamMemberRepository.findByTeamIdAndUserId(teamId, userId)
                .orElseThrow(() -> new RuntimeException("해당 팀의 멤버가 아닙니다."));
        return convertToResponse(member);
    }

    // 팀장 ID 조회 (초대 거절 시 사용)
    @Transactional(readOnly = true)
    public Long getLeaderId(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."))
                .getLeader().getId();
    }

    // 1. 팀원 초대
    public void inviteMember(Long teamId, Long leaderId, MemberInviteRequest request) {
        TeamMember requester = teamMemberRepository.findByTeamIdAndUserId(teamId, leaderId)
                .orElseThrow(() -> new RuntimeException("권한 정보가 없습니다."));

        if (requester.getRole() != MemberRoleType.LEADER) {
            throw new RuntimeException("팀장만 초대할 수 있습니다.");
        }

        User findUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("해당 이메일의 유저가 없습니다."));

        teamMemberRepository.findByTeamIdAndUserId(teamId, findUser.getId())
                .ifPresent(m -> { throw new RuntimeException("이미 팀원이거나 초대 중입니다."); });

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));

        teamMemberRepository.save(TeamMember.builder()
                .team(team).user(findUser)
                .role(MemberRoleType.MEMBER).status(MemberStatus.INVITED).build());
    }

    // 2. 초대 수락 → JOINED로 변경
    public void acceptInvitation(Long teamId, Long currentUserId) {
        TeamMember invitation = teamMemberRepository
                .findByTeamIdAndUserIdAndStatus(teamId, currentUserId, MemberStatus.INVITED)
                .orElseThrow(() -> new RuntimeException("수락할 초대 내역이 없습니다."));
        invitation.setStatus(MemberStatus.JOINED);
    }

    // 초대 거절 — 본인이 직접 INVITED 레코드 삭제
    public void declineInvitation(Long teamId, Long currentUserId) {
        TeamMember invitation = teamMemberRepository
                .findByTeamIdAndUserIdAndStatus(teamId, currentUserId, MemberStatus.INVITED)
                .orElseThrow(() -> new RuntimeException("거절할 초대 내역이 없습니다."));
        teamMemberRepository.delete(invitation);
    }

    // 3. 팀원 삭제 / 초대 취소 / 초대 거절 통합
    public void removeMember(Long teamId, Long requesterId, Long targetUserId) {
        TeamMember requester = teamMemberRepository.findByTeamIdAndUserId(teamId, requesterId)
                .orElseThrow(() -> new RuntimeException("요청자 정보를 찾을 수 없습니다."));

        if (requester.getRole() != MemberRoleType.LEADER) {
            throw new RuntimeException("팀 관리 권한이 없습니다.");
        }

        if (requesterId.equals(targetUserId)) {
            throw new RuntimeException("팀장 본인은 삭제할 수 없습니다.");
        }

        TeamMember targetMember = teamMemberRepository.findByTeamIdAndUserId(teamId, targetUserId)
                .orElseThrow(() -> new RuntimeException("대상 멤버를 찾을 수 없습니다."));

        teamMemberRepository.delete(targetMember);
    }

    @Transactional(readOnly = true)
    public List<TeamMemberResponse> getTeamMembers(Long teamId) {
        return teamMemberRepository.findAllByTeamIdAndStatus(teamId, MemberStatus.JOINED).stream()
                .map(this::convertToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TeamMemberResponse> getInviteMembers(Long teamId) {
        return teamMemberRepository.findAllByTeamIdAndStatus(teamId, MemberStatus.INVITED).stream()
                .map(this::convertToResponse).collect(Collectors.toList());
    }

    private TeamMemberResponse convertToResponse(TeamMember m) {
        return TeamMemberResponse.builder()
                .userId(m.getUser().getId())
                .userName(m.getUser().getNickname())
                .userEmail(m.getUser().getEmail())
                .role(m.getRole())
                .status(m.getStatus()).build();
    }
}