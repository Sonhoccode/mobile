package com.example.fastfood.data.model;

public class ForgotPasswordResponse {
    private String message;
    private String resetToken;

    public String getMessage() {
        return message;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }
}
