package com.example.fastfood.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.fastfood.R;
import com.example.fastfood.data.api.FoodAPI;
import com.example.fastfood.data.api.RetrofitClient;
import com.example.fastfood.data.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoFragment extends Fragment {
    private static final String ARG_USER_ID = "user_id";

    private EditText edtName, edtDate, edtPhone, edtEmail, edtAddress;
    private FoodAPI foodAPI;
    private String userId;

    public static InfoFragment newInstance(String userId) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_info, container, false);

        edtName = view.findViewById(R.id.edtName);
        edtDate = view.findViewById(R.id.edtDate);
        edtPhone = view.findViewById(R.id.edtPhone);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtAddress = view.findViewById(R.id.edtAddress);
        ImageView btnBack = view.findViewById(R.id.btnBack);
        AppCompatButton btnConfirm = view.findViewById(R.id.btnConfirm);
        AppCompatButton btnChangePassword = view.findViewById(R.id.btnChangePassword);

        foodAPI = RetrofitClient.getApi();
        loadUserData();

        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        btnConfirm.setOnClickListener(v -> saveUserData());

        btnChangePassword.setOnClickListener(v -> {
            // Tạm thời chưa chuyển ChangePasswordActivity, bạn có thể chuyển tương tự
            Toast.makeText(getContext(), "Chức năng này cần chuyển ChangePasswordActivity thành Fragment", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void loadUserData() {
        if (userId == null) return;
        foodAPI.getUser(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    edtName.setText(user.getName());
                    edtDate.setText(user.getDate());
                    edtPhone.setText(user.getPhone());
                    edtEmail.setText(user.getEmail());
                    edtAddress.setText(user.getAddress());
                } else {
                    Toast.makeText(getContext(), "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserData() {
        User user = new User();
        user.setName(edtName.getText().toString());
        user.setDate(edtDate.getText().toString());
        user.setPhone(edtPhone.getText().toString());
        user.setEmail(edtEmail.getText().toString());
        user.setAddress(edtAddress.getText().toString());

        foodAPI.updateUser(userId, user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack(); // Quay lại sau khi cập nhật
                } else {
                    Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}