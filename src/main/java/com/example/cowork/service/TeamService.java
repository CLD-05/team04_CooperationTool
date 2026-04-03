package com.example.cowork.service;

import com.example.cowork.dto.team.TeamForm;
import com.example.cowork.entity.Team;
import com.example.cowork.entity.TeamMember;
import com.example.cowork.entity.User;
import com.example.cowork.repository.TeamMemberRepository;
import com.example.cowork.repository.TeamRepository;
import com.example.cowork.repository.UserRepository;
import com.example.cowork.type.MemberRoleType;
import com.example.cowork.type.MemberStatus;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

    public TeamService(TeamRepository teamRepository,
                       TeamMemberRepository teamMemberRepository,
                       UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userRepository = userRepository;
    }

    public Team createTeam(TeamForm form, User loginUser) {
        if (form.getName() == null || form.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("팀 이름을 입력해주세요.");
        }

        Team team = new Team(form.getName().trim(), form.getDescription(), loginUser);
        Team savedTeam = teamRepository.save(team);

        // 팀장은 바로 ACCEPTED
        TeamMember leaderMember = new TeamMember();
        leaderMember.setTeam(savedTeam);
        leaderMember.setUser(loginUser);
        leaderMember.setRole(MemberRoleType.LEADER);
        leaderMember.setStatus(MemberStatus.ACCEPTED);
        teamMemberRepository.save(leaderMember);

        // 초대한 멤버는 INVITED — 수락해야 참여됨
        if (form.getMemberEmails() != null && !form.getMemberEmails().trim().isEmpty()) {
            String[] memberArray = form.getMemberEmails().split(",");

            for (String memberEmail : memberArray) {
                String trimmed = memberEmail.trim();
                if (trimmed.isEmpty()) continue;

                User member = userRepository.findByEmail(trimmed)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀원 이메일입니다: " + trimmed));

                if (!member.getId().equals(loginUser.getId())) {
                    boolean alreadyExists = teamMemberRepository.existsByTeamAndUser(savedTeam, member);
                    if (!alreadyExists) {
                        TeamMember memberEntity = new TeamMember();
                        memberEntity.setTeam(savedTeam);
                        memberEntity.setUser(member);
                        memberEntity.setRole(MemberRoleType.MEMBER);
                        memberEntity.setStatus(MemberStatus.INVITED); // ACCEPTED → INVITED
                        teamMemberRepository.save(memberEntity);
                    }
                }
            }
        }

        return savedTeam;
    }

    public void deleteTeam(Long teamId, User loginUser) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀이 존재하지 않습니다."));

        if (!team.getLeader().getId().equals(loginUser.getId())) {
            throw new IllegalArgumentException("팀 리더만 팀을 삭제할 수 있습니다.");
        }

        List<TeamMember> members = teamMemberRepository.findByTeam(team);
        teamMemberRepository.deleteAll(members);
        teamRepository.delete(team);
    }

    public void leaveTeam(Long teamId, User loginUser) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀이 존재하지 않습니다."));

        if (team.getLeader().getId().equals(loginUser.getId())) {
            throw new IllegalArgumentException("팀 리더는 탈퇴할 수 없습니다. 팀을 삭제해주세요.");
        }

        TeamMember teamMember = teamMemberRepository.findByTeamAndUser(team, loginUser)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자는 이 팀의 팀원이 아닙니다."));

        teamMemberRepository.delete(teamMember);
    }

    public Team getTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀이 존재하지 않습니다."));
    }

    public String getMemberEmails(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀이 존재하지 않습니다."));

        return teamMemberRepository.findByTeam(team).stream()
                .map(tm -> tm.getUser().getEmail())
                .collect(Collectors.joining(", "));
    }
}