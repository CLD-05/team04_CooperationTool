package com.example.cowork.dto.dashboard;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DashboardResponseDto {
	
	private String userName;
	private List<TeamCardDto> teams;
	
	private int totalPages;
	private long totalElements;

}
