package com.example.cowork.controller;

import com.example.cowork.dto.team.MemberInviteRequest;
import com.example.cowork.dto.team.TeamMemberResponse;
import com.example.cowork.service.TeamMemberService;
import com.example.cowork.type.MemberRoleType;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/view/teams")
@RequiredArgsConstructor
public class TeamViewController {

    private final TeamMemberService teamMemberService;

    // 1. 팀 관리 페이지 (팀장만 접근 가능)
    @GetMapping("/{teamId}/members")
    public String teamMembersPage(@PathVariable("teamId") Long teamId,
                                  HttpSession session,
                                  Model model) {

        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            return "redirect:/api/user/login";
        }

        try {
            TeamMemberResponse myInfo = teamMemberService.getMemberInfo(teamId, currentUserId);
            if (myInfo.getRole() != MemberRoleType.LEADER) {
                return "redirect:/dashboard?error=no-permission";
            }
            model.addAttribute("teamId", teamId);
            model.addAttribute("isLeader", true);
            model.addAttribute("members", teamMemberService.getTeamMembers(teamId));
            model.addAttribute("invitedMembers", teamMemberService.getInviteMembers(teamId));

            return "team/team-members";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/dashboard?error=not-found";
        }
    }

    // 2. 팀원 초대
    @PostMapping("/{teamId}/members/invite")
    public String inviteMember(@PathVariable("teamId") Long teamId,
                               @RequestParam("email") String email,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            return "redirect:/api/user/login";
        }

        try {
            teamMemberService.inviteMember(teamId, currentUserId, new MemberInviteRequest(email));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/view/teams/" + teamId + "/members";
    }

    // 3. 팀원 삭제/초대 취소
    @PostMapping("/{teamId}/members/remove")
    public String removeMember(@PathVariable("teamId") Long teamId,
                               @RequestParam("targetUserId") Long targetUserId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        Long currentUserId = (Long) session.getAttribute("userId");
        if (currentUserId == null) {
            return "redirect:/api/user/login";
        }

        try {
            teamMemberService.removeMember(teamId, currentUserId, targetUserId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/view/teams/" + teamId + "/members";
    }
}