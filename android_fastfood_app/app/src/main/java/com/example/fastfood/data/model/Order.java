// File: data/model/Order.java
package com.example.fastfood.data.model;

import java.util.List;

public class Order {
    private int id;
    private String customerName;
    private double totalPrice;
    private String status;
    private String paymentMethod;
    private String createdAt;
    private List<OrderItem> orderItems; // Quan trọng

    // Thêm getters cho tất cả các trường

    public int getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
}
