
// File: data/model/OrderItem.java
package com.example.fastfood.data.model;

public class OrderItem {
    private String foodName;
    private int quantity;
    private double price;
    private String imageUrl;

    // Thêm getters cho tất cả các trường

    public String getImageUrl() {
        return imageUrl;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getFoodName() {
        return foodName;
    }
}