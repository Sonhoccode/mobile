package com.example.fastfood.data.model;

import com.example.fastfood.data.local.CartItem;
import java.util.List;

// Lớp này đại diện cho toàn bộ yêu cầu đặt hàng
public class OrderRequest {
    int userId;
    CustomerInfo customerInfo;
    List<CartItem> items;
    double totalPrice;
    String paymentMethod;

    public OrderRequest(int userId, CustomerInfo info, List<CartItem> items, double total, String method) {
        this.userId = userId;
        this.customerInfo = info;
        this.items = items;
        this.totalPrice = total;
        this.paymentMethod = method;
    }
}