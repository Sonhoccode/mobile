package com.example.fastfood.data.model;
public class VerifyOtpRequest {
    private String phone;
    private String otp;

    public VerifyOtpRequest(String phone, String otp) {
        this.phone = phone;
        this.otp = otp;
    }

    public String getPhone() {
        return phone;
    }

    public String getOtp() {
        return otp;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
