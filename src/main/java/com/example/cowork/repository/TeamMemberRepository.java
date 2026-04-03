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

    // 특정 팀에 특정 유저가 있는지 조회
    Optional<TeamMember> findByTeamIdAndUserId(Long teamId, Long userId);

    // 초대 수락 대기 중인 데이터 조회
    Optional<TeamMember> findByTeamIdAndUserIdAndStatus(Long teamId, Long userId, MemberStatus status);

    // 특정 팀의 팀원 목록 조회
    List<TeamMember> findAllByTeamIdAndStatus(Long teamId, MemberStatus status);

    // 특정 유저의 모든 팀 참여 내역 조회
    List<TeamMember> findAllByUserId(Long userId);
}