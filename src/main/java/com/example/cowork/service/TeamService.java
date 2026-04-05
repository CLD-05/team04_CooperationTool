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

        // 팀장은 바로 ACCEPTED, builder 패턴으로 생성
        teamMemberRepository.save(TeamMember.builder()
                .team(savedTeam)
                .user(loginUser)
                .role(MemberRoleType.LEADER)
                .status(MemberStatus.ACCEPTED)
                .build());

        // 초대한 멤버는 INVITED
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
                        teamMemberRepository.save(TeamMember.builder()
                                .team(savedTeam)
                                .user(member)
                                .role(MemberRoleType.MEMBER)
                                .status(MemberStatus.INVITED)
                                .build());
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

        // findByTeam + deleteAll 대신 deleteAllByTeam으로 변경
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

    @Transactional(readOnly = true)
    public Team getTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("팀이 존재하지 않습니다."));
    }
}