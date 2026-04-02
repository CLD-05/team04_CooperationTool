package com.example.cowork.repository;

import com.example.cowork.entity.TeamMember;
import com.example.cowork.type.MemberStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    // 1. 특정 팀에 특정 유저가 있는지 조회
    Optional<TeamMember> findByTeamIdAndUserId(Long teamId, Long userId);

    // 2. 초대 수락 대기 중인 데이터 조회 (Status로 통일)
    Optional<TeamMember> findByTeamIdAndUserIdAndStatus(Long teamId, Long userId, MemberStatus status);

    // 3. 팀원 목록 조회 (Status로 통일)
    List<TeamMember> findAllByTeamIdAndStatus(Long teamId, MemberStatus status);

    // 4. 특정 유저의 모든 팀 참여 내역 조회
    List<TeamMember> findAllByUserId(Long userId);
}
