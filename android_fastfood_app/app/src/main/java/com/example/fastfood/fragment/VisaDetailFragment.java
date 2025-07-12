
package com.example.fastfood.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fastfood.R;
import com.example.fastfood.data.api.FoodAPI;
import com.example.fastfood.data.api.RetrofitClient;
import com.example.fastfood.data.model.PaymentAccount;
import com.google.android.material.textfield.TextInputEditText;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisaDetailFragment extends Fragment {
    private TextInputEditText edtCardHolder, edtCardNumber, edtExpiry, edtCVV;
    private Button btnAddCard;
    private ImageView btnBack;
    private FoodAPI foodAPI;
    private String userPhone;

    // Phương thức static để tạo Fragment, giúp quản lý việc truyền dữ liệu
    public static VisaDetailFragment newInstance() {
        return new VisaDetailFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // "Thổi phồng" layout cho Fragment
        View view = inflater.inflate(R.layout.activity_visa_detail, container, false);

        // Ánh xạ view
        btnBack = view.findViewById(R.id.btnBack);
        edtCardHolder = view.findViewById(R.id.edtCardHolder);
        edtCardNumber = view.findViewById(R.id.edtCardNumber);
        edtExpiry = view.findViewById(R.id.edtExpiry);
        edtCVV = view.findViewById(R.id.edtCVV);
        btnAddCard = view.findViewById(R.id.btnAddCard);

        foodAPI = RetrofitClient.getApi();

        // Lấy userPhone từ SharedPreferences, sử dụng getActivity() để lấy context
        SharedPreferences prefs = getActivity().getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE);
        userPhone = prefs.getString("userPhone", "");

        btnAddCard.setOnClickListener(v -> {
            String cardHolder = edtCardHolder.getText().toString().trim();
            String cardNumber = edtCardNumber.getText().toString().trim();
            String expiry = edtExpiry.getText().toString().trim();
            String cvv = edtCVV.getText().toString().trim();

            if(cardHolder.isEmpty() || cardNumber.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
                Toast.makeText(getContext(), "Nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Sử dụng model PaymentAccount đã sửa
            PaymentAccount acc = new PaymentAccount(userPhone, cardHolder, cardNumber, expiry, cvv, "visa"); // Thêm type
            foodAPI.addPaymentAccount(acc).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()) {
                        Toast.makeText(getContext(), "Đã thêm thẻ!", Toast.LENGTH_SHORT).show();
                        // Quay lại màn hình trước đó
                        getParentFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getContext(), "Lỗi backend!", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getContext(), "Lỗi mạng!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Thay vì finish(), ta pop back stack
        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }
}