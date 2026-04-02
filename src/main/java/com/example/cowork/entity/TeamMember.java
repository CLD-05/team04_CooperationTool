package com.example.cowork.entity;

import com.example.cowork.type.MemberRoleType;
import com.example.cowork.type.MemberStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "team_members")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // N:1 관계 - 여러 멤버십 데이터가 하나의 팀에 속함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    // N:1 관계 - 여러 멤버십 데이터가 하나의 유저와 연결됨
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRoleType role;   // LEADER, MEMBER

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status; // INVITED, JOINED


}