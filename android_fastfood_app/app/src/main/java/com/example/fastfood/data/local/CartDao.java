package com.example.fastfood.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CartDao {

    @Insert
    void insert(CartItem cartItem);

    @Update
    void update(CartItem cartItem);
    @Delete
    void delete(CartItem cartItem);


    @Query("SELECT * FROM cart_items WHERE foodId = :foodId LIMIT 1")
    CartItem findItemById(String foodId);

    @Query("SELECT * FROM cart_items")
    LiveData<List<CartItem>> getAllCartItems();

    @Query("DELETE FROM cart_items")
    void deleteAll();
}