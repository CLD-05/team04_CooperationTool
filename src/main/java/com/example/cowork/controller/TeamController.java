package com.example.cowork.controller;

import com.example.cowork.dto.team.TeamForm;
import com.example.cowork.entity.User;
import com.example.cowork.service.TeamService;
import com.example.cowork.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final UserService userService;

    // 팀 생성 폼
    @GetMapping("/new")
    public String newTeamForm(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) return "redirect:/api/user/login";
        model.addAttribute("teamForm", new TeamForm());
        return "team/team_form";
    }

    // 팀 생성 처리
    @PostMapping("/new")
    public String createTeam(@ModelAttribute("teamForm") TeamForm teamForm,
                             HttpSession session,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/api/user/login";

        try {
            User loginUser = userService.findById(userId);
            teamService.createTeam(teamForm, loginUser);
            redirectAttributes.addFlashAttribute("message", "팀이 성공적으로 생성되었습니다!");
            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("teamForm", teamForm);
            model.addAttribute("errorMessage", e.getMessage());
            return "team/team_form";
        }
    }

    // 팀 삭제 (팀장 전용)
    @PostMapping("/{teamId}/delete")
    public String deleteTeam(@PathVariable Long teamId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/api/user/login";

        try {
            User loginUser = userService.findById(userId);
            teamService.deleteTeam(teamId, loginUser);
            redirectAttributes.addFlashAttribute("message", "팀이 성공적으로 삭제되었습니다.");
            return "redirect:/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/view/teams/" + teamId + "/members";
        }
    }

    // 팀 탈퇴 (일반 멤버 전용)
    @PostMapping("/{teamId}/leave")
    public String leaveTeam(@PathVariable Long teamId,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/api/user/login";

        try {
            User loginUser = userService.findById(userId);
            teamService.leaveTeam(teamId, loginUser);
            redirectAttributes.addFlashAttribute("message", "팀에서 탈퇴했습니다.");
            return "redirect:/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/teams/" + teamId + "/files";
        }
    }
}