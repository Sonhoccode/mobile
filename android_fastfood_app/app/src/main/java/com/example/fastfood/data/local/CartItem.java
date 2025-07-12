package com.example.fastfood.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cart_items") // Tên của bảng trong database
public class CartItem {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String foodId;
    public String name;
    public double price;
    public String imageUrl;
    public int quantity;
    public String notes;

    // có thể thêm các constructor, getter, setter
}