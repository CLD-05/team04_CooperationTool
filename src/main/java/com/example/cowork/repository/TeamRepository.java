package com.example.cowork.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cowork.entity.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long>{

	List<Team> findAllByOrderByCreatedAtDesc();
	
	List<Team> findByNameContaining(String keyword);
}