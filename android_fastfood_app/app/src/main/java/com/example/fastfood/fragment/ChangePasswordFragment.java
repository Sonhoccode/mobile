package com.example.fastfood.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fastfood.R;
import com.example.fastfood.data.api.FoodAPI;
import com.example.fastfood.data.api.RetrofitClient;
import com.example.fastfood.data.model.ApiResponse;
import com.example.fastfood.data.model.ChangePasswordRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordFragment extends Fragment {

    private EditText edtOldPass, edtNewPass, edtConfirmPass;

    public static ChangePasswordFragment newInstance() {
        return new ChangePasswordFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        edtOldPass = view.findViewById(R.id.edtOldPass);
        edtNewPass = view.findViewById(R.id.edtNewPass);
        edtConfirmPass = view.findViewById(R.id.edtConfirmPass);
        Button btnConfirmChange = view.findViewById(R.id.btnConfirmChange);
        ImageView btnBack = view.findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        btnConfirmChange.setOnClickListener(v -> handleChangePassword());

        return view;
    }

    private void handleChangePassword() {
        String oldPassword = edtOldPass.getText().toString().trim();
        String newPassword = edtNewPass.getText().toString().trim();
        String confirmPassword = edtConfirmPass.getText().toString().trim();

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(getContext(), "Mật khẩu mới phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy số điện thoại đã lưu từ SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE);
        String userPhone = prefs.getString("userPhone", null);

        if (userPhone == null) {
            Toast.makeText(getContext(), "Lỗi: không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        FoodAPI foodAPI = RetrofitClient.getApi();
        ChangePasswordRequest request = new ChangePasswordRequest(userPhone, oldPassword, newPassword);

        foodAPI.changePassword(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack(); // Quay lại màn hình trước
                } else {
                    Toast.makeText(getContext(), "Đổi mật khẩu thất bại. Vui lòng kiểm tra lại mật khẩu cũ.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}