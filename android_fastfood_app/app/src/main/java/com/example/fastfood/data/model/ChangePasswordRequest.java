package com.example.fastfood.data.model;

public class ChangePasswordRequest {
    private String phone;
    private String oldPassword;
    private String newPassword;

    public ChangePasswordRequest(String phone, String oldPassword, String newPassword) {
        this.phone = phone;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}