package com.example.fastfood.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastfood.R;
import com.example.fastfood.adapter.CartAdapter;
import com.example.fastfood.data.api.FoodAPI;
import com.example.fastfood.data.api.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;

import com.example.fastfood.data.local.AppDatabase;
import com.example.fastfood.data.local.CartItem;
import com.example.fastfood.data.model.ApiResponse;
import com.example.fastfood.data.model.CreatePaymentRequest;
import com.example.fastfood.data.model.CreatePaymentResponse;
import com.example.fastfood.data.model.CustomerInfo;
import com.example.fastfood.data.model.OrderRequest;
import com.example.fastfood.data.model.PaymentAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartItemListener {

    private static final int VNPAY_REQUEST_CODE = 100;
    private RecyclerView rvCartMain;
    private CartAdapter cartAdapter;
    private MaterialToolbar toolbar;
    private AppDatabase database;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();
    private LiveData<List<CartItem>> cartItemsLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        database = AppDatabase.getDatabase(this);
        toolbar = findViewById(R.id.toolbar_cart);
        rvCartMain = findViewById(R.id.rv_cart_main);

        setupToolbar();
        setupRecyclerView();
        observeCartData();
        loadVisaCards();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thanh toán");
        }
    }

    private void setupRecyclerView() {
        rvCartMain.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, new ArrayList<>(), this);
        rvCartMain.setAdapter(cartAdapter);
    }

    private void observeCartData() {
        cartItemsLiveData = database.cartDao().getAllCartItems();
        cartItemsLiveData.observe(this, cartItems -> {
            List<Object> displayList = new ArrayList<>();
            displayList.add("HEADER");
            if (cartItems != null && !cartItems.isEmpty()) {
                displayList.addAll(cartItems);
            }
            displayList.add("SUMMARY");
            cartAdapter.updateItems(displayList);
        });
    }

    private void loadVisaCards() {
        SharedPreferences prefs = getSharedPreferences("USER_PREFS", MODE_PRIVATE);
        String userPhone = prefs.getString("userPhone", null);
        if (userPhone == null) return;

        RetrofitClient.getApi().getPaymentAccounts(userPhone).enqueue(new Callback<List<PaymentAccount>>() {
            @Override
            public void onResponse(Call<List<PaymentAccount>> call, Response<List<PaymentAccount>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartAdapter.setVisaCards(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<PaymentAccount>> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Lỗi tải danh sách thẻ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCheckout(int paymentMethodId, int visaCardPosition, double totalAmount) {
        if (findViewById(paymentMethodId) == null) {
            Toast.makeText(this, "Vui lòng chọn phương thức thanh toán!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (paymentMethodId == R.id.rbCash) {
            createOrder("Tiền mặt");
        } else if (paymentMethodId == R.id.rbVnPay) {
            processVnPayPayment(totalAmount);
        } else if (paymentMethodId == R.id.rbVisa) {
            List<PaymentAccount> cards = cartAdapter.getVisaCards();
            if (cards != null && !cards.isEmpty() && visaCardPosition >= 0) {
                createOrder("Thẻ Visa");
            } else {
                Toast.makeText(this, "Vui lòng chọn một thẻ để thanh toán!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createOrder(String paymentMethod) {
        List<CartItem> cartItems = cartItemsLiveData.getValue();
        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống! Không thể đặt hàng.", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("USER_PREFS", MODE_PRIVATE);
        String userId = prefs.getString("userId", null);
        String userName = prefs.getString("userName", "Khách vãng lai");
        String userPhone = prefs.getString("userPhone", "");
        String address = "123 Đường ABC, Quận 1, TP. HCM"; // Giả định, bạn nên có màn hình nhập địa chỉ

        if (userId == null) {
            Toast.makeText(this, "Lỗi xác thực người dùng. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        CustomerInfo customerInfo = new CustomerInfo(userName, address, userPhone);
        double totalPrice = calculateTotalAmount();

        OrderRequest request = new OrderRequest(Integer.parseInt(userId), customerInfo, cartItems, totalPrice, paymentMethod);

        RetrofitClient.getApi().createOrder(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CartActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();

                    // Xóa giỏ hàng sau khi đặt thành công
                    databaseExecutor.execute(() -> database.cartDao().deleteAll());

                    // Chuyển về MainActivity và xóa các màn hình trung gian
                    Intent intent = new Intent(CartActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(CartActivity.this, "Đặt hàng thất bại, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processVnPayPayment(double amount) {
        if (amount <= 15000) {
            Toast.makeText(this, "Giỏ hàng của bạn đang trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        CreatePaymentRequest request = new CreatePaymentRequest((long) amount);
        RetrofitClient.getApi().createVnPayPayment(request).enqueue(new Callback<CreatePaymentResponse>() {
            @Override
            public void onResponse(Call<CreatePaymentResponse> call, Response<CreatePaymentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String paymentUrl = response.body().getPaymentUrl();
                    Intent intent = new Intent(CartActivity.this, VnPayActivity.class);
                    intent.putExtra("paymentUrl", paymentUrl);
                    startActivityForResult(intent, VNPAY_REQUEST_CODE);
                } else {
                    Toast.makeText(CartActivity.this, "Không thể tạo yêu cầu thanh toán VNPay.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreatePaymentResponse> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VNPAY_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null && data.getBooleanExtra("isSuccess", false)) {
                // Thanh toán thành công, tiến hành tạo đơn hàng
                createOrder("VNPay (Đã thanh toán)");
            } else {
                Toast.makeText(this, "Thanh toán VNPay thất bại hoặc đã bị hủy.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private double calculateTotalAmount() {
        List<CartItem> cartItems = cartItemsLiveData.getValue();
        double subtotal = 0;
        if (cartItems != null) {
            for (CartItem item : cartItems) {
                subtotal += item.price * item.quantity;
            }
        }
        return subtotal + 15000; // Cộng phí ship
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
