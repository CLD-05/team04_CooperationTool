package com.example.cowork.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.cowork.dto.team.MemberInviteRequest;
import com.example.cowork.dto.team.TeamMemberResponse;
import com.example.cowork.service.TeamMemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/teams/{teamId}/members")
@RequiredArgsConstructor
public class TeamMemberController {
    private final TeamMemberService teamMemberService;
    
    @PostMapping("/invite")
    public ResponseEntity<String> inviteMember(
            @PathVariable Long teamId,
            @RequestHeader("x-leader-id") Long leaderId,
            @RequestBody MemberInviteRequest request) {
        teamMemberService.inviteMember(teamId, leaderId, request);
        return ResponseEntity.ok("팀원 초대가 성공적으로 완료되었습니다.");
    }
    
    @PostMapping("/accept")
    public ResponseEntity<String> acceptInvitation(
            @PathVariable Long teamId,
            @RequestHeader("x-user-id") Long user_id) {
        teamMemberService.acceptInvitation(teamId, user_id);
        return ResponseEntity.ok("초대를 수락하여 팀에 합류했습니다.");
    }
    
    @PostMapping("/remove/{target_user_id}") // 경로에 변수 추가
    public ResponseEntity<String> removeMember(
            @PathVariable Long teamId,
            @PathVariable Long target_user_id,
            @RequestHeader("x-user-id") Long request_id) {
        // 서비스 파라미터 순서에 맞춰 수정 (teamId, requesterId, targetUserId)
        teamMemberService.removeMember(teamId, request_id, target_user_id);
        return ResponseEntity.ok("팀 멤버 삭제/탈퇴 처리가 완료되었습니다.");
    }
    
}
