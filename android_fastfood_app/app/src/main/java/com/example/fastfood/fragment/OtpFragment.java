package com.example.fastfood.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fastfood.R;
import com.example.fastfood.activity.ForgotPasswordActivity;
import com.example.fastfood.activity.RegisterActivity;
import com.example.fastfood.data.api.RetrofitClient;

import com.example.fastfood.data.model.ApiResponse;
import com.example.fastfood.data.model.ForgotPasswordRequest;
import com.example.fastfood.data.model.ForgotPasswordResponse;
import com.example.fastfood.data.model.VerifyOtpRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpFragment extends Fragment {

    private static final String ARG_PHONE = "user_phone";
    private String userPhone;

    private TextInputEditText edtOtp;
    private MaterialButton btnVerifyOtp;
    private TextView tvResendOtp;
    private TextView tvTitle;

    public static OtpFragment newInstance(String phone) {
        OtpFragment fragment = new OtpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PHONE, phone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userPhone = getArguments().getString(ARG_PHONE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_otp, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtOtp = view.findViewById(R.id.edt_otp);
        btnVerifyOtp = view.findViewById(R.id.btn_verify_otp);
        tvResendOtp = view.findViewById(R.id.tv_resend_otp);
        tvTitle = view.findViewById(R.id.title);
        ImageView ivBack = view.findViewById(R.id.iv_back);

        tvTitle.setText("Nhập mã OTP");

        TextView tvRegister = view.findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RegisterActivity.class);
            startActivity(intent);
        });

        ivBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        setupClickListeners();
    }

    private void setupClickListeners() {
        btnVerifyOtp.setOnClickListener(v -> {
            String otp = edtOtp.getText().toString().trim();

            if (otp.length() < 4) {
                edtOtp.setError("Mã OTP không hợp lệ");
                return;
            }

            // Gọi API xác thực OTP
            VerifyOtpRequest request = new VerifyOtpRequest(userPhone, otp);
            RetrofitClient.getAuthApi().verifyOtp(request)
                    .enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Xác thực thành công", Toast.LENGTH_SHORT).show();
                                ((ForgotPasswordActivity) requireActivity()).navigateToResetPasswordFragment(userPhone, otp);
                            } else {
                                Toast.makeText(getContext(), "Mã OTP không đúng hoặc đã hết hạn", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {
                            Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("OTP", "Lỗi xác thực OTP: ", t);
                        }
                    });
        });

        tvResendOtp.setOnClickListener(v -> {
            // Gọi lại API gửi OTP
            ForgotPasswordRequest request = new ForgotPasswordRequest(userPhone);
            RetrofitClient.getAuthApi().forgotPassword(request)
                    .enqueue(new Callback<ForgotPasswordResponse>() {
                        @Override
                        public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                            if (response.isSuccessful()) {
                                String newOtp = response.body().getResetToken();
                                Toast.makeText(getContext(), "Đã gửi lại OTP: " + newOtp, Toast.LENGTH_LONG).show(); // chỉ để test
                            } else {
                                Toast.makeText(getContext(), "Không thể gửi lại OTP", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                            Toast.makeText(getContext(), "Lỗi mạng khi gửi lại OTP", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
