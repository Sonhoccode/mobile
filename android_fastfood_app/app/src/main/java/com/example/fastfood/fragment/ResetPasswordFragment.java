package com.example.fastfood.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fastfood.R;
import com.example.fastfood.activity.LoginActivity;
import com.example.fastfood.activity.RegisterActivity;
import com.example.fastfood.data.api.RetrofitClient;
import com.example.fastfood.data.model.ApiResponse;
import com.example.fastfood.data.model.ResetPasswordRequest;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordFragment extends Fragment {

    private static final String ARG_PHONE = "phone_number";
    private static final String ARG_OTP = "otp_code";

    private String phone;
    private String otp;

    private TextInputEditText edtNewPassword, edtConfirmPassword;
    private Button btnConfirm;

    public static ResetPasswordFragment newInstance(String phone, String otp) {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PHONE, phone);
        args.putString(ARG_OTP, otp);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_reset, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Lấy dữ liệu từ arguments
        phone = getArguments() != null ? getArguments().getString(ARG_PHONE) : "";
        otp = getArguments() != null ? getArguments().getString(ARG_OTP) : "";

        // Ánh xạ view
        edtNewPassword = view.findViewById(R.id.edt_new_password);
        edtConfirmPassword = view.findViewById(R.id.edt_confirm_password);
        btnConfirm = view.findViewById(R.id.btn_reset_password);
        ImageView ivBack = view.findViewById(R.id.iv_back);
        TextView tvRegister = view.findViewById(R.id.tvRegister);

        // Quay lại
        ivBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Điều hướng đăng ký
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RegisterActivity.class);
            startActivity(intent);
        });

        // Gửi yêu cầu đặt lại mật khẩu
        btnConfirm.setOnClickListener(v -> {
            String newPassword = edtNewPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(getContext(), "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            ResetPasswordRequest request = new ResetPasswordRequest(phone, newPassword, confirmPassword);
            RetrofitClient.getAuthApi().resetPassword(request)
                    .enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Đặt lại mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getContext(), LoginActivity.class));
                                requireActivity().finish();
                            } else {
                                Toast.makeText(getContext(), "Không thể đặt lại mật khẩu", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {
                            Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
