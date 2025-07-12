package com.example.fastfood.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastfood.R;
import com.example.fastfood.adapter.CartAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import com.example.fastfood.data.local.AppDatabase;
import com.example.fastfood.data.local.CartItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartItemListener {

    private RecyclerView rvCartMain;
    private CartAdapter cartAdapter;
    private MaterialToolbar toolbar;
    private AppDatabase database;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Khởi tạo các thành phần
        database = AppDatabase.getDatabase(this);
        toolbar = findViewById(R.id.toolbar_cart);
        rvCartMain = findViewById(R.id.rv_cart_main);

        // Thiết lập các chức năng
        setupToolbar();
        setupRecyclerView();
        observeCartData();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Giỏ hàng");
        }
    }

    private void setupRecyclerView() {
        rvCartMain.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, new ArrayList<>(), this);
        rvCartMain.setAdapter(cartAdapter);
    }
    private void observeCartData() {
        database.cartDao().getAllCartItems().observe(this, new Observer<List<CartItem>>() {
            @Override
            public void onChanged(List<CartItem> cartItems) {
                List<Object> displayList = new ArrayList<>();

                displayList.add("HEADER");

                if (cartItems != null && !cartItems.isEmpty()) {
                    displayList.addAll(cartItems);
                }
                displayList.add("SUMMARY");

                cartAdapter.updateItems(displayList);
            }
        });
    }

    @Override
    public void onQuantityIncrease(CartItem item) {
        databaseExecutor.execute(() -> {
            item.quantity++;
            database.cartDao().update(item);
        });
    }

    @Override
    public void onQuantityDecrease(CartItem item) {
        databaseExecutor.execute(() -> {
            if (item.quantity > 1) {
                item.quantity--;
                database.cartDao().update(item);
            } else {
                database.cartDao().delete(item);
                runOnUiThread(() -> Toast.makeText(CartActivity.this, "Đã xóa " + item.name, Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public void onItemDelete(CartItem item) {
        databaseExecutor.execute(() -> {
            database.cartDao().delete(item);
            runOnUiThread(() -> Toast.makeText(CartActivity.this, "Đã xóa " + item.name, Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}