package com.example.cowork.repository;

import com.example.cowork.entity.Team;
import com.example.cowork.entity.TeamMember;
import com.example.cowork.entity.User;
import com.example.cowork.type.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    void deleteAllByTeam(Team team);

    boolean existsByTeamAndUser(Team team, User user);

    void deleteByTeamAndUser(Team team, User user);

    List<TeamMember> findByTeam(Team team);

    Optional<TeamMember> findByTeamAndUser(Team team, User user);

    Optional<TeamMember> findByTeamIdAndUserId(Long teamId, Long userId);

    Optional<TeamMember> findByTeamIdAndUserIdAndStatus(Long teamId, Long userId, MemberStatus status);

    List<TeamMember> findAllByTeamIdAndStatus(Long teamId, MemberStatus status);

    List<TeamMember> findAllByUserId(Long userId);

    // 특정 유저의 특정 상태 팀 목록 (초대 목록 조회용)
    List<TeamMember> findAllByUserIdAndStatus(Long userId, MemberStatus status);
}