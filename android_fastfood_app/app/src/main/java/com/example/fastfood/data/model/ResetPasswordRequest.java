package com.example.fastfood.data.model;
public class ResetPasswordRequest {
    private String phone;
    private String newPassword;
    private String confirmPassword;

    public ResetPasswordRequest(String phone, String newPassword, String confirmPassword) {
        this.phone = phone;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getPhone() {
        return phone;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
