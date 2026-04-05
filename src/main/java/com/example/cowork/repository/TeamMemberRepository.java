package com.example.cowork.repository;

import com.example.cowork.entity.Team;
import com.example.cowork.entity.TeamMember;
import com.example.cowork.entity.User;
import com.example.cowork.type.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    boolean existsByTeamAndUser(Team team, User user);

    List<TeamMember> findByTeam(Team team);

    Optional<TeamMember> findByTeamAndUser(Team team, User user);

    Optional<TeamMember> findByTeamIdAndUserId(Long teamId, Long userId);

    Optional<TeamMember> findByTeamIdAndUserIdAndStatus(Long teamId, Long userId, MemberStatus status);

    List<TeamMember> findAllByTeamIdAndStatus(Long teamId, MemberStatus status);

    // 팀장(ACCEPTED) + 일반 멤버(JOINED) 모두 조회
    List<TeamMember> findAllByTeamIdAndStatusIn(Long teamId, List<MemberStatus> statuses);

    List<TeamMember> findAllByUserId(Long userId);

    List<TeamMember> findAllByUserIdAndStatus(Long userId, MemberStatus status);
}