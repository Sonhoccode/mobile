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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fastfood.R;
import com.example.fastfood.data.api.FoodAPI;
import com.example.fastfood.data.api.RetrofitClient;
import com.example.fastfood.data.model.ShopInfo;
import com.example.fastfood.data.model.SupportRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SupportFragment extends Fragment {

    private TextView tvEmail, tvPhone, tvAddress;
    private EditText edtSupportContent;
    private Button btnSendSupport;
    private ImageView btnBack, imgDropdown;
    private LinearLayout contactInfoContent;
    private boolean contactInfoVisible = true;
    private String userPhone;
    private FoodAPI foodAPI;

    public static SupportFragment newInstance() {
        return new SupportFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_support, container, false);

        // Ánh xạ các view từ layout
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvAddress = view.findViewById(R.id.tvAddress);
        edtSupportContent = view.findViewById(R.id.edtSupportContent);
        btnSendSupport = view.findViewById(R.id.btnSendSupport);
        btnBack = view.findViewById(R.id.btnBack);
        imgDropdown = view.findViewById(R.id.imgDropdown);
        contactInfoContent = view.findViewById(R.id.contactInfoContent);

        // Khởi tạo API
        foodAPI = RetrofitClient.getApi();

        // Lấy thông tin người dùng từ SharedPreferences
        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE);
            userPhone = prefs.getString("userPhone", "");
        }

        setupEventListeners(view);
        loadShopInfo();

        return view;
    }

    private void setupEventListeners(View view) {
        // Sự kiện click cho nút quay lại
        btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });

        // Sự kiện click để ẩn/hiện thông tin liên hệ
        view.findViewById(R.id.headerContactInfo).setOnClickListener(v -> {
            contactInfoVisible = !contactInfoVisible;
            contactInfoContent.setVisibility(contactInfoVisible ? View.VISIBLE : View.GONE);
            imgDropdown.setRotation(contactInfoVisible ? 0 : -90);
        });

        // Sự kiện click cho nút gửi hỗ trợ
        btnSendSupport.setOnClickListener(v -> sendSupportRequest());
    }

    private void loadShopInfo() {
        foodAPI.getShopInfo().enqueue(new Callback<ShopInfo>() {
            @Override
            public void onResponse(Call<ShopInfo> call, Response<ShopInfo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ShopInfo shop = response.body();
                    tvEmail.setText("Email liên hệ: " + shop.getEmail());
                    tvPhone.setText("Hotline: " + shop.getPhone());
                    tvAddress.setText("Địa chỉ: " + shop.getAddress());
                } else {
                    Toast.makeText(getContext(), "Không thể tải thông tin cửa hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ShopInfo> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng khi tải thông tin cửa hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendSupportRequest() {
        String content = edtSupportContent.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(getContext(), "Bạn cần nhập nội dung hỗ trợ.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userPhone == null || userPhone.isEmpty()) {
            Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        SupportRequest request = new SupportRequest(userPhone, content);
        foodAPI.sendSupportRequest(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã gửi yêu cầu hỗ trợ thành công!", Toast.LENGTH_SHORT).show();
                    edtSupportContent.setText(""); // Xóa nội dung đã nhập
                } else {
                    Toast.makeText(getContext(), "Gửi yêu cầu thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối. Không thể gửi yêu cầu.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}