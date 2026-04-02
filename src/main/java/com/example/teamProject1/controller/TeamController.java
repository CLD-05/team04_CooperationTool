package com.example.teamProject1.controller;

import com.example.teamProject1.dto.TeamForm;
import com.example.teamProject1.entity.Team;
import com.example.teamProject1.entity.User;
import com.example.teamProject1.repository.UserRepository;
import com.example.teamProject1.service.TeamService;
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
            Team savedTeam = teamService.createTeam(teamForm);

            model.addAttribute("teamId", savedTeam.getId());
            model.addAttribute("teamName", savedTeam.getName());
            model.addAttribute("leaderUsername", teamForm.getLeaderUsername());
            model.addAttribute("memberUsernames", teamForm.getMemberUsernames());
            model.addAttribute("description", savedTeam.getDescription());

            return "team/team_complete";
        } catch (Exception e) {
            model.addAttribute("teamForm", teamForm);
            model.addAttribute("errorMessage", e.getMessage());
            return "team/team_form";
        }
    }

    @PostMapping("/{teamId}/delete")
    public String deleteTeam(@PathVariable Integer teamId,
                             @RequestParam String loginUsername,
                             Model model) {
        try {
            User loginUser = userRepository.findByUsername(loginUsername)
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
    public String leaveTeam(@PathVariable Integer teamId,
                            @RequestParam String loginUsername,
                            Model model) {
        try {
            User loginUser = userRepository.findByUsername(loginUsername)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

            teamService.leaveTeam(teamId, loginUser);

            return "redirect:/teams/" + teamId;

        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "team/team_error";
        }
    }
    
    @GetMapping("/{teamId}")
    public String teamDetail(@PathVariable Integer teamId, Model model) {
        Team team = teamService.getTeamById(teamId);

        model.addAttribute("teamId", team.getId());
        model.addAttribute("teamName", team.getName());
        model.addAttribute("leaderUsername", team.getLeader().getUsername());
        model.addAttribute("description", team.getDescription());
        model.addAttribute("memberUsernames", teamService.getMemberUsernames(teamId));

        return "team/team_complete";
    }
}