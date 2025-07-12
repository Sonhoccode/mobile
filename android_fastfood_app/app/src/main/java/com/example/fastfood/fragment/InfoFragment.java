package com.example.fastfood.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.Calendar;

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

        // **PHẦN THÊM MỚI BẮT ĐẦU TỪ ĐÂY**
        addTextWatcherForDate(edtDate);
        // **PHẦN THÊM MỚI KẾT THÚC**

        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        btnConfirm.setOnClickListener(v -> saveUserData());
        btnChangePassword.setOnClickListener(v -> {
            // Thay thế Toast cũ bằng đoạn mã điều hướng
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, ChangePasswordFragment.newInstance())
                    .addToBackStack(null)
                    .commit();
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
                    getParentFragmentManager().popBackStack();
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

    // **PHƯƠNG THỨC THÊM MỚI**
    private void addTextWatcherForDate(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private final String ddmmyyyy = "DDMMYYYY";
            private final Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    editText.setText(current);
                    editText.setSelection(Math.min(sel, current.length()));
                }
            }
        });
    }
}