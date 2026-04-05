package com.example.cowork.repository;


import com.example.cowork.entity.Team;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.example.cowork.entity.User;



@Repository
public interface TeamRepository extends JpaRepository<Team, Long>{


}

