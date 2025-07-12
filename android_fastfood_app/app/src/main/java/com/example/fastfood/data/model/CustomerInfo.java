package com.example.fastfood.data.model;

// Lớp này để chứa thông tin khách hàng
public class CustomerInfo {
    String name, address, phone;

    public CustomerInfo(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
    }
}