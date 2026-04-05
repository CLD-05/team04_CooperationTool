package com.example.cowork.dto.team;

import com.example.cowork.type.MemberRoleType;
import com.example.cowork.type.MemberStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeamMemberResponse {
    private Long userId;
    private String userName;
    private String userEmail;
    private MemberRoleType role;
    private MemberStatus status;
}