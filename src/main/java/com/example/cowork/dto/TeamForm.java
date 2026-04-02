package com.example.cowork.dto;

public class TeamForm {

    private String name;              // 팀 이름
    private String leaderUsername;    // 리더 username
    private String memberUsernames;   // 팀원 username들 (쉼표 구분)
    private String description;       // 팀 소개

    public TeamForm() {
    }

    public String getName() {
        return name;
    }

    public String getLeaderUsername() {
        return leaderUsername;
    }

    public String getMemberUsernames() {
        return memberUsernames;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLeaderUsername(String leaderUsername) {
        this.leaderUsername = leaderUsername;
    }

    public void setMemberUsernames(String memberUsernames) {
        this.memberUsernames = memberUsernames;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}