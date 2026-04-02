package com.example.teamProject1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity 
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 작업용 (로그인 하지 않아도 모든 페이지 접근 허용)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() 
            )
            .formLogin(login -> login.disable()); 
            
        return http.build();
    }
}


/* 작업 마무리하고 변경(로그인 해야만 다른 페이지 접근 가능)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // 테스트 편의를 위해 해제
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/user/signup", "/api/user/login", "/css/**", "/js/**").permitAll() 
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/api/user/login") // 우리 로그인 페이지 주소
                .defaultSuccessUrl("/api/user/dashboard", true)
            )
            .logout(logout -> logout
                .logoutUrl("/api/user/logout")
                .logoutSuccessUrl("/api/user/login")
                .invalidateHttpSession(true) // 세션 즉시 삭제
            );
        return http.build();
    }
}

*/