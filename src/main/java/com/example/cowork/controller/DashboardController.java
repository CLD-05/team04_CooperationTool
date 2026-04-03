package com.example.cowork.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.cowork.dto.dashboard.DashboardResponseDto;
import com.example.cowork.service.DashboardService;
import com.example.cowork.service.TeamMemberService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final TeamMemberService teamMemberService;

    @GetMapping("/dashboard")
    public String getDashboard(Model model, HttpSession session,
                               @RequestParam(defaultValue = "0") int page) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/api/user/login";

        DashboardResponseDto dashboardData = dashboardService.getDashboardData(userId);
        model.addAttribute("dashboard", dashboardData);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", dashboardData.getTotalPages());
        return "user/dashboard";
    }

    // 초대 수락
    @PostMapping("/dashboard/invites/{teamId}/accept")
    public String acceptInvite(@PathVariable Long teamId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/api/user/login";

        try {
            teamMemberService.acceptInvitation(teamId, userId);
            redirectAttributes.addFlashAttribute("inviteMessage", "팀에 참여했습니다!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("inviteError", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    // 초대 거절 (본인이 직접 INVITED 레코드 삭제)
    @PostMapping("/dashboard/invites/{teamId}/decline")
    public String declineInvite(@PathVariable Long teamId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/api/user/login";

        try {
            teamMemberService.declineInvitation(teamId, userId);
            redirectAttributes.addFlashAttribute("inviteMessage", "초대를 거절했습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("inviteError", e.getMessage());
        }
        return "redirect:/dashboard";
    }
}