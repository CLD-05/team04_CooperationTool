package com.example.cowork.controller;

import com.example.cowork.dto.TeamForm;
import com.example.cowork.entity.Team;
import com.example.cowork.entity.User;
import com.example.cowork.repository.UserRepository;
import com.example.cowork.service.TeamService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;
    private final UserRepository userRepository;

    public TeamController(TeamService teamService, UserRepository userRepository) {
        this.teamService = teamService;
        this.userRepository = userRepository;
    }

    @GetMapping("/new")
    public String newTeamForm(Model model) {
        model.addAttribute("teamForm", new TeamForm());
        return "team/team_form";
    }

    @PostMapping("/new")
    public String createTeam(@ModelAttribute("teamForm") TeamForm teamForm, Model model) {
        try {
            if (teamForm.getLoginEmail() == null || teamForm.getLoginEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("로그인 이메일을 입력해주세요.");
            }

            User loginUser = userRepository.findByEmail(teamForm.getLoginEmail().trim())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

            Team savedTeam = teamService.createTeam(teamForm, loginUser);

            model.addAttribute("teamId", savedTeam.getId());
            model.addAttribute("teamName", savedTeam.getName());
            model.addAttribute("leaderEmail", savedTeam.getLeader().getEmail());
            model.addAttribute("memberEmails", teamService.getMemberEmails(savedTeam.getId()));
            model.addAttribute("description", savedTeam.getDescription());
            model.addAttribute("loginEmail", loginUser.getEmail());

            return "team/team_complete";
        } catch (Exception e) {
            model.addAttribute("teamForm", teamForm);
            model.addAttribute("errorMessage", e.getMessage());
            return "team/team_form";
        }
    }

    @GetMapping("/{teamId}")
    public String teamDetail(@PathVariable Long teamId,
                             @RequestParam(required = false) String loginEmail,
                             Model model) {
        Team team = teamService.getTeamById(teamId);

        model.addAttribute("teamId", team.getId());
        model.addAttribute("teamName", team.getName());
        model.addAttribute("leaderEmail", team.getLeader().getEmail());
        model.addAttribute("description", team.getDescription());
        model.addAttribute("memberEmails", teamService.getMemberEmails(teamId));
        model.addAttribute("loginEmail", loginEmail);

        return "team/team_complete";
    }

    @PostMapping("/{teamId}/delete")
    public String deleteTeam(@PathVariable Long teamId,
                             @RequestParam String loginEmail,
                             Model model) {
        try {
            User loginUser = userRepository.findByEmail(loginEmail.trim())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

            teamService.deleteTeam(teamId, loginUser);

            model.addAttribute("message", "팀이 성공적으로 삭제되었습니다.");
            return "team/team_deleted";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "team/team_error";
        }
    }

    @PostMapping("/{teamId}/leave")
    public String leaveTeam(@PathVariable Long teamId,
                            @RequestParam String loginEmail,
                            Model model) {
        try {
            User loginUser = userRepository.findByEmail(loginEmail.trim())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

            teamService.leaveTeam(teamId, loginUser);

            model.addAttribute("message", "팀에서 성공적으로 탈퇴했습니다.");
            return "team/team_deleted";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "team/team_error";
        }
    }
}