package com.example.fastfood.data.model;

public class LoginRequest {
    private String phone;
    private String password;
    private boolean rememberMe;

    public LoginRequest(String phone, String password) {
        this.phone = phone;
        this.password = password;
        this.rememberMe = true;
    }
}