package com.example.cowork.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cowork.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long>{

}
