package com.example.fastfood.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import com.example.fastfood.data.local.AppDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.fastfood.fragment.InfoFragment;

import com.example.fastfood.R;
import com.example.fastfood.data.api.FoodAPI;
import com.example.fastfood.data.api.RetrofitClient;
import com.example.fastfood.data.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingFragment extends Fragment {
    private TextView tvName, tvPhone;
    private FoodAPI foodAPI;
    private String userId;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_setting, container, false);

        // Lấy userId đã lưu từ SharedPreferences khi Fragment được tạo
        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE);
            userId = prefs.getString("userId", null);

            android.util.Log.d("SETTING_DEBUG", "userId đọc từ SharedPreferences: " + userId);
        }

        // Ánh xạ các view
        tvName = view.findViewById(R.id.tvName);
        tvPhone = view.findViewById(R.id.tvPhone);
        LinearLayout btnInfo = view.findViewById(R.id.btnInfo);
        LinearLayout btnPayment = view.findViewById(R.id.btnPayment);
        LinearLayout btnSupport = view.findViewById(R.id.btnSupport);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        foodAPI = RetrofitClient.getApi();

        // Cài đặt các sự kiện click
        btnInfo.setOnClickListener(v -> {
            if (userId != null) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, InfoFragment.newInstance(userId))
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });

        btnPayment.setOnClickListener(v -> {
            String userPhoneValue = tvPhone.getText().toString();
            if (!userPhoneValue.isEmpty()) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, PaymentAccountFragment.newInstance(userPhoneValue))
                        .addToBackStack(null)
                        .commit();
            }
        });

        btnSupport.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, SupportFragment.newInstance())
                    .addToBackStack(null)
                    .commit();
        });

        btnLogout.setOnClickListener(v -> {
            // Hiển thị thông báo
            Toast.makeText(getContext(), "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();

            // 1. Xóa dữ liệu giỏ hàng khỏi cơ sở dữ liệu Room trên một luồng riêng
            // Để tránh xung đột, chúng ta sẽ tạo một Executor mới
            java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                AppDatabase.getDatabase(getContext()).cartDao().deleteAll();
            });

            // 2. Xóa thông tin phiên đăng nhập (token) từ SessionManager
            com.example.fastfood.data.api.SessionManager sessionManager = new com.example.fastfood.data.api.SessionManager(getContext());
            sessionManager.logout();

            // 3. Xóa thông tin chi tiết của người dùng (userId, userName, userPhone)
            SharedPreferences prefs = getActivity().getSharedPreferences("USER_PREFS", android.content.Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.commit(); // Dùng commit() để xóa ngay lập tức

            // 4. Điều hướng về màn hình Đăng nhập và xóa hết các màn hình cũ
            Intent intent = new Intent(getActivity(), com.example.fastfood.activity.LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            // 5. Kết thúc MainActivity
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load hoặc refresh dữ liệu người dùng mỗi khi Fragment được hiển thị
        loadUserData();
    }

    private void loadUserData() {
        // Kiểm tra xem có userId không (để tránh lỗi khi chưa đăng nhập)
        if (userId == null || userId.isEmpty()) {
            // Hiển thị thông tin mặc định hoặc thông báo yêu cầu đăng nhập
            tvName.setText("Khách");
            tvPhone.setText("Vui lòng đăng nhập");
            return;
        }

        // Gọi API để lấy thông tin mới nhất từ server
        foodAPI.getUser(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    tvName.setText(user.getName());
                    tvPhone.setText(user.getPhone());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Nếu gọi API lỗi, có thể tạm hiển thị thông tin đã lưu trong SharedPreferences
                if (getActivity() != null) {
                    SharedPreferences prefs = getActivity().getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE);
                    tvName.setText(prefs.getString("userName", "Người dùng"));
                    tvPhone.setText(prefs.getString("userPhone", ""));
                }
                Toast.makeText(getContext(), "Không thể cập nhật thông tin mới nhất", Toast.LENGTH_SHORT).show();
            }
        });
    }
}