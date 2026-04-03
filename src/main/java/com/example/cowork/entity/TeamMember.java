package com.example.cowork.entity;

import com.example.cowork.type.MemberRoleType;
import com.example.cowork.type.MemberStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "team_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 여러 TeamMember가 하나의 Team에 속함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    // 여러 TeamMember가 하나의 User에 연결됨
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRoleType role;   // LEADER, MEMBER

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;   // INVITED, JOINED

    // 서비스에서 간단히 생성할 때 사용
    public TeamMember(Team team, User user) {
        this.team = team;
        this.user = user;
    }

    // 역할/상태까지 지정해서 생성할 때 사용
    public TeamMember(Team team, User user, MemberRoleType role, MemberStatus status) {
        this.team = team;
        this.user = user;
        this.role = role;
        this.status = status;
    }
}