package com.example.cowork.dto.team;

public class TeamForm {

    private String name;
    private String memberEmails;
    private String description;
    private String loginEmail;

    public TeamForm() {
    }

    public String getName() {
        return name;
    }

    public String getMemberEmails() {
        return memberEmails;
    }

    public String getDescription() {
        return description;
    }

    public String getLoginEmail() {
        return loginEmail;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMemberEmails(String memberEmails) {
        this.memberEmails = memberEmails;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLoginEmail(String loginEmail) {
        this.loginEmail = loginEmail;
    }
}