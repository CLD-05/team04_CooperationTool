package com.example.teamProject1.repository;


import com.example.teamProject1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 1. 로그인 시: 이메일로 사용자 정보 가져오기
    Optional<User> findByEmail(String email);

    // 2. 회원가입 시: 이메일 중복 확인 (true면 이미 있는 이메일)
    boolean existsByEmail(String email);

    // 3. 회원가입 시: 닉네임 중복 확인
    boolean existsByNickname(String nickname);
}