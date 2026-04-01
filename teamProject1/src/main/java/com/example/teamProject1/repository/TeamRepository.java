package com.example.teamProject1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.teamProject1.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Integer> {
}