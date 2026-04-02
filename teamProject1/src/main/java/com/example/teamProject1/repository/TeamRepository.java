package com.example.teamProject1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.teamProject1.entity.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long>{

	List<Team> findAllByOrderByCreatedAtDesc();
	
	List<Team> findByNameContaining(String keyword);
}