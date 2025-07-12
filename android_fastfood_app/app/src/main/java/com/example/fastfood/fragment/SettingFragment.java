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
            // (Thêm logic xóa SharedPreferences và quay về LoginActivity ở đây)
            Toast.makeText(getContext(), "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
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