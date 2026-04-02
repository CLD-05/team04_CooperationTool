package com.example.cowork.dto;

public class TeamCreateDto {

    private String name;
    private String description;
    private String leaderUsername;
    private String memberUsername;

    public TeamCreateDto() {
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLeaderUsername() {
        return leaderUsername;
    }

    public String getMemberUsername() {
        return memberUsername;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLeaderUsername(String leaderUsername) {
        this.leaderUsername = leaderUsername;
    }

    public void setMemberUsername(String memberUsername) {
        this.memberUsername = memberUsername;
    }
}