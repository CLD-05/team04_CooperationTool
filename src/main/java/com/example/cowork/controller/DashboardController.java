package com.example.cowork.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.cowork.dto.dashboard.DashboardResponseDto;
import com.example.cowork.service.DashboardService;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequiredArgsConstructor
public class DashboardController {
	
	private final DashboardService dashboardService;
	
	@GetMapping("/dashboard")
	public String getDashboard(
			@PageableDefault(size = 6, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
			Model model, HttpSession Session) {

		
		String userNickname = (String) Session.getAttribute("userNickname");
		DashboardResponseDto dashboardData = dashboardService.getDashboardData(userNickname, pageable);
		
		// Model 객체에 데이터를 담고 HTML에 전달 -> HTML에서는 "dashboard"라는 이름으로 접근
		model.addAttribute("dashboard", dashboardData);
		
		model.addAttribute("currentPage", pageable.getPageNumber());
        model.addAttribute("totalPages", dashboardData.getTotalPages()); // DTO에 이 필드가 있어야 함
        
		// HTML파일 이름
		return "dashboard"; 
	}  
	
	
	
}

