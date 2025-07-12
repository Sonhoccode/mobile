package com.example.fastfood.data.model;

public class ForgotPasswordRequest {
    private String phone;

    public ForgotPasswordRequest(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
