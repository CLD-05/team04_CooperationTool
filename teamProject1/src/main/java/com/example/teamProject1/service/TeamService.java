package com.example.teamProject1.service;

import com.example.teamProject1.dto.TeamForm;
import com.example.teamProject1.entity.Team;
import com.example.teamProject1.entity.TeamMember;
import com.example.teamProject1.entity.User;
import com.example.teamProject1.repository.TeamMemberRepository;
import com.example.teamProject1.repository.TeamRepository;
import com.example.teamProject1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

    public Team createTeam(TeamForm form) {
        if (form.getName() == null || form.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("팀 이름을 입력해주세요.");
        }

        if (form.getLeaderUsername() == null || form.getLeaderUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("리더 아이디를 입력해주세요.");
        }

        User leader = userRepository.findByUsername(form.getLeaderUsername().trim())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리더 아이디입니다."));

        Team team = new Team(form.getName().trim(), form.getDescription(), leader);
        Team savedTeam = teamRepository.save(team);

        TeamMember leaderMember = new TeamMember(savedTeam, leader);
        teamMemberRepository.save(leaderMember);

        if (form.getMemberUsernames() != null && !form.getMemberUsernames().trim().isEmpty()) {
            String[] memberArray = form.getMemberUsernames().split(",");

            for (String memberUsername : memberArray) {
                String trimmed = memberUsername.trim();

                if (trimmed.isEmpty()) {
                    continue;
                }

                User member = userRepository.findByUsername(trimmed)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀원 아이디입니다: " + trimmed));

                if (!member.getId().equals(leader.getId())) {
                    boolean alreadyExists = teamMemberRepository.existsByTeamAndUser(savedTeam, member);
                    if (!alreadyExists) {
                        TeamMember memberEntity = new TeamMember(savedTeam, member);
                        teamMemberRepository.save(memberEntity);
                    }
                }
            }
        }

        return savedTeam;
    }

    public void deleteTeam(Integer teamId, User loginUser) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀이 존재하지 않습니다."));

        // 리더만 삭제 가능
        if (!team.getLeader().getId().equals(loginUser.getId())) {
            throw new IllegalArgumentException("팀 리더만 팀을 삭제할 수 있습니다.");
        }

        // 팀원 관계 먼저 삭제
        teamMemberRepository.deleteAllByTeam(team);

        // 팀 삭제
        teamRepository.delete(team);
    }

    public void leaveTeam(Integer teamId, User loginUser) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀 없음"));

        if (team.getLeader().getId().equals(loginUser.getId())) {
            throw new IllegalArgumentException("팀장은 탈퇴 불가");
        }

        if (!teamMemberRepository.existsByTeamAndUser(team, loginUser)) {
            throw new IllegalArgumentException("팀원이 아님");
        }

        teamMemberRepository.deleteByTeamAndUser(team, loginUser);
    }
}