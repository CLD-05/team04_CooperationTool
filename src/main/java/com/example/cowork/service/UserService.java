package com.example.cowork.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cowork.dto.auth.LoginRequestDto;
import com.example.cowork.dto.auth.SignupRequestDto;
import com.example.cowork.entity.User;
import com.example.cowork.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder; // 비밀번호 암호화 도구

    // 회원가입 로직
    public void signup(SignupRequestDto dto) {
    	
    	// 비밀번호 일치 확인 
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }
    	
        // 1. 중복 이메일 검증
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        
        // 2. 중복 닉네임 검증 
        if (userRepository.existsByNickname(dto.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        

        // 3. 비밀번호 암호화 및 저장
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        User user = User.builder()
                .email(dto.getEmail())
                .password(encodedPassword)
                .nickname(dto.getNickname())
                .build();

        userRepository.save(user);
    }

    // 로그인 검증 로직 (성공 시 유저 정보 반환)
    public User login(LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }
}