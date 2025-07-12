package com.example.fastfood.data.api;

import com.example.fastfood.data.model.ApiResponse;
import com.example.fastfood.data.model.ForgotPasswordRequest;
import com.example.fastfood.data.model.ForgotPasswordResponse;
import com.example.fastfood.data.model.LoginRequest;
import com.example.fastfood.data.model.LoginResponse;
import com.example.fastfood.data.model.RegisterRequest;
import com.example.fastfood.data.model.RegisterResponse;
import com.example.fastfood.data.model.ResetPasswordRequest;
import com.example.fastfood.data.model.VerifyOtpRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthAPI {
    @POST("/auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    @POST("/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("/auth/forgot-password")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("/auth/verify-otp")
    Call<ApiResponse> verifyOtp(@Body VerifyOtpRequest request);

    @POST("/auth/reset-password")
    Call<ApiResponse> resetPassword(@Body ResetPasswordRequest request);
}