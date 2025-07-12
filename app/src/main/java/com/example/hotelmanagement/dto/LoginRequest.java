package com.example.hotelmanagement.dto;

public class LoginRequest {
    private String login;
    private String password;

    public LoginRequest(String username, String password) {
        this.login = username;
        this.password = password;
    }
}
