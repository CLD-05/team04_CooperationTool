package com.example.teamProject1.repository;

import com.example.teamProject1.entity.Team;
import com.example.teamProject1.entity.TeamMember;
import com.example.teamProject1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Integer> {

    void deleteAllByTeam(Team team);

    boolean existsByTeamAndUser(Team team, User user);

    void deleteByTeamAndUser(Team team, User user);
}