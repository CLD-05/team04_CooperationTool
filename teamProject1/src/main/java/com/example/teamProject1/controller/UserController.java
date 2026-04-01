package com.example.teamProject1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.teamProject1.dto.auth.LoginRequestDto;
import com.example.teamProject1.dto.auth.SignupRequestDto;
import com.example.teamProject1.entity.User;
import com.example.teamProject1.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/signup")
    public String signupPage() {
        return "auth/signup"; // signup.html 반환
    }
    
    @PostMapping("/signup")
    public String signup(@ModelAttribute SignupRequestDto dto, RedirectAttributes redirectAttributes) {
        try {
            userService.signup(dto); // 서비스 호출 (여기서 중복 이메일 등 예외 발생 가능)
            // 성공 시 메시지 담기
            redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다!");
            return "redirect:/api/user/login"; 
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/api/user/signup"; 
        }
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login"; // login.html 반환
    }
    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequestDto dto, HttpSession session, Model model) {
        try {
            User user = userService.login(dto);
            session.setAttribute("userNickname", user.getNickname());
            return "redirect:/dashboard"; // 로그인 성공 시 대시보드로 이동
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/login"; // 실패 시 에러 메시지와 함께 로그인 페이지 유지
        }
    }

    
    
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        // 1. 세션의 모든 데이터를 지우고 무효화합니다.
        if (session != null) {
            session.invalidate();
        }
        
        // 2. 로그아웃 후에는 다시 로그인 페이지로 리다이렉트 시킵니다.
        return "redirect:api/user/login";
    }
}