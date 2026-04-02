package com.example.teamProject1.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.teamProject1.dto.dashboard.DashboardResponseDto;
import com.example.teamProject1.dto.dashboard.TeamCardDto;
import com.example.teamProject1.entity.Team;
import com.example.teamProject1.repository.TeamRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {
	
	private final TeamRepository teamRepository;
	
	@Transactional(readOnly = true)
	public DashboardResponseDto getDashboardData(String userId, Pageable pageable) {

		Page<Team> teamPage = teamRepository.findAll(pageable);
		
		List<TeamCardDto> teamCardList = teamPage.getContent().stream()
                .map(team -> new TeamCardDto(
                        team.getId(),
                        team.getName(),
                        team.getDescription()
                ))
                .collect(Collectors.toList());
		
		return new DashboardResponseDto(
			    userId + "님", 
			    teamCardList, 
			    teamPage.getTotalPages(),   
			    teamPage.getTotalElements() 
			);
	}

}
