package com.example.cowork.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.cowork.dto.auth.LoginRequestDto;
import com.example.cowork.dto.auth.SignupRequestDto;
import com.example.cowork.entity.User;
import com.example.cowork.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/signup")
    public String signupPage() {
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute SignupRequestDto dto, RedirectAttributes redirectAttributes) {
        try {
            userService.signup(dto);
            redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다!");
            return "redirect:/api/user/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/api/user/signup";
        }
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequestDto dto, HttpSession session, Model model) {
        try {
            User user = userService.login(dto);

            // 세션에 userId와 userNickname 함께 저장
            session.setAttribute("userId", user.getId());
            session.setAttribute("userNickname", user.getNickname());

            return "redirect:/dashboard";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/api/user/login";
    }
}