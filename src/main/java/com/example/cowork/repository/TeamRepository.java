package com.example.cowork.repository;

import com.example.cowork.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cowork.entity.User;


public interface TeamRepository extends JpaRepository<Team, Long> {
}

