package com.example.cowork.controller;

import com.example.cowork.dto.MemberInviteRequest;
import com.example.cowork.dto.TeamMemberResponse;
import com.example.cowork.service.TeamMemberService;
import com.example.cowork.type.MemberRoleType;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/view/teams")
@RequiredArgsConstructor
public class TeamViewController {

    private final TeamMemberService teamMemberService;

    // 1. 팀 관리 페이지 로드 (DB 최신 데이터 반영)
    @GetMapping("/{teamId}/members")
    public String teamMembersPage(@PathVariable("teamId") Long teamId, Model model) {
        Long currentUserId = 1L; // 임시 팀장 ID

        try {
            TeamMemberResponse myInfo = teamMemberService.getMemberInfo(teamId, currentUserId);
            if (myInfo.getRole() != MemberRoleType.LEADER) {
                return "redirect:/view/teams/" + teamId + "?error=no-permission"; 
            }
            model.addAttribute("teamId", teamId);
            model.addAttribute("members", teamMemberService.getTeamMembers(teamId));
            model.addAttribute("invitedMembers", teamMemberService.getInviteMembers(teamId));
            
            return "team/team-members";
        } catch (Exception e) {
        	e.printStackTrace();
            return "redirect:/view/teams?error=not-found";
        }
    }

    // 2. 팀원 초대 처리 후 '관리 페이지'로 리다이렉트 (변경사항 즉시 확인)
    @PostMapping("/{teamId}/members/invite")
    public String inviteMember(@PathVariable("teamId") Long teamId, 
                               @RequestParam("email") String email) {
        teamMemberService.inviteMember(teamId, 1L, new MemberInviteRequest(email));
        return "redirect:/view/teams/" + teamId + "/members"; // [수정] 관리 페이지 유지
    }

    // 3. 팀원 삭제/취소 처리 후 '관리 페이지'로 리다이렉트 (변경사항 즉시 확인)
    @PostMapping("/{teamId}/members/remove")
    public String removeMember(@PathVariable("teamId") Long teamId, 
                               @RequestParam("targetUserId") Long targetUserId) {
        teamMemberService.removeMember(teamId, 1L, targetUserId);
        return "redirect:/view/teams/" + teamId + "/members"; // [수정] 관리 페이지 유지
    }
}