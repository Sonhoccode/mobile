package com.example.fastfood.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fastfood.R;
import com.example.fastfood.activity.ForgotPasswordActivity;
import com.example.fastfood.activity.LoginActivity;
import com.example.fastfood.activity.RegisterActivity;
import com.example.fastfood.data.api.RetrofitClient;
import com.example.fastfood.data.model.ForgotPasswordRequest;
import com.example.fastfood.data.model.ForgotPasswordResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneEntryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_phone, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText etPhoneNumber = view.findViewById(R.id.edt_phone);
        View btnSendOtp = view.findViewById(R.id.btn_get_otp);
        ImageView ivBack = view.findViewById(R.id.iv_back);

        btnSendOtp.setOnClickListener(v -> {
            String phone = etPhoneNumber.getText().toString();

            if (phone.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
                return;
            }
            ForgotPasswordRequest request = new ForgotPasswordRequest(phone);
            RetrofitClient.getAuthApi().forgotPassword(request)
                    .enqueue(new Callback<ForgotPasswordResponse>() {
                        @Override
                        public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                String otp = response.body().getResetToken();
                                Toast.makeText(getContext(), "Mã OTP là: " + otp, Toast.LENGTH_LONG).show();

                                ((ForgotPasswordActivity) requireActivity()).navigateToOtpFragment(phone);
                            } else {
                                Toast.makeText(getContext(), "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                            Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });


        TextView tvRegister = view.findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(v -> {
            // Điều hướng tới màn hình đăng ký
            Intent intent = new Intent(getActivity(), RegisterActivity.class);
            startActivity(intent);
        });
        ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }
}