package com.example.fastfood.data.model;

public class CreatePaymentRequest {
    private long amount;
    // Thêm các trường khác nếu cần
    public CreatePaymentRequest(long amount) {
        this.amount = amount;
    }
}