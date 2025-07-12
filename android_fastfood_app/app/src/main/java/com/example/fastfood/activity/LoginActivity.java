package com.example.fastfood.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fastfood.R;
import com.example.fastfood.data.api.AuthAPI;
import com.example.fastfood.data.api.RetrofitClient;
import com.example.fastfood.data.api.SessionManager;
import com.example.fastfood.data.model.LoginRequest;
import com.example.fastfood.data.model.LoginResponse;
import com.example.fastfood.data.model.User; // **Quan trọng: Cần import lớp User**

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText edtPhone, edtPassword;
    ImageView ivBack;
    LinearLayout llGoToRegister;
    View btnLogin;
    CheckBox cbRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Kiểm tra xem người dùng đã đăng nhập từ trước chưa
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Ánh xạ các view
        cbRememberMe = findViewById(R.id.cb_remember_me);
        edtPhone = findViewById(R.id.edt_phone);
        edtPassword = findViewById(R.id.edt_password);
        ivBack = findViewById(R.id.iv_back);
        llGoToRegister = findViewById(R.id.ll_go_to_register);
        btnLogin = findViewById(R.id.btn_login_submit);
        TextView tvForgotPassword = findViewById(R.id.tv_forgot_password);

        // Cài đặt các sự kiện click
        tvForgotPassword.setOnClickListener(v -> {
            // (Bạn cần tạo ForgotPasswordActivity)
             Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
             startActivity(intent);
        });

        ivBack.setOnClickListener(v -> finish());

        llGoToRegister.setOnClickListener(v -> {
            // (Bạn cần tạo RegisterActivity)
             Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
             startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            String phone = edtPhone.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi API đăng nhập
            AuthAPI api = RetrofitClient.getAuthApi();
            LoginRequest loginRequest = new LoginRequest(phone, password);

            api.login(loginRequest).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                        LoginResponse loginResponse = response.body();
                        String token = loginResponse.getToken();

                        // Lưu token nếu người dùng chọn "Ghi nhớ đăng nhập"
                        if (cbRememberMe.isChecked()) {
                            SessionManager sessionManager = new SessionManager(getApplicationContext());
                            sessionManager.createLoginSession(token);
                        }

                        // **[PHẦN QUAN TRỌNG] Lưu thông tin người dùng vào SharedPreferences**
                        // Giả sử LoginResponse của bạn có chứa một đối tượng User
                        User loggedInUser = loginResponse.getUser();

                        SharedPreferences prefs = getSharedPreferences("USER_PREFS", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        if (loggedInUser != null) {
                            // THÊM DÒNG NÀY ĐỂ KIỂM TRA
                            android.util.Log.d("LOGIN_DEBUG", "User ID lấy được: " + loggedInUser.getId());

                            editor.putString("userId", loggedInUser.getId());
                            editor.putString("userName", loggedInUser.getName());
                            editor.putString("userPhone", loggedInUser.getPhone());
                        } else {
                            // THÊM DÒNG NÀY ĐỂ KIỂM TRA
                            android.util.Log.d("LOGIN_DEBUG", "Đối tượng loggedInUser là null!");
                        }

                        editor.commit();

                        // Chuyển sang màn hình chính
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Sai số điện thoại hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}