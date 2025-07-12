package com.example.fastfood.fragment;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastfood.R;
import com.example.fastfood.adapter.PaymentAccountAdapter;
import com.example.fastfood.data.api.FoodAPI;
import com.example.fastfood.data.api.RetrofitClient;
import com.example.fastfood.data.model.PaymentAccount;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentAccountFragment extends Fragment {
    private static final String ARG_USER_PHONE = "user_phone";

    private RecyclerView rvCards;
    private PaymentAccountAdapter adapter;
    private List<PaymentAccount> cardList = new ArrayList<>();
    private FoodAPI foodAPI;
    private String userPhone;

    public static PaymentAccountFragment newInstance(String userPhone) {
        PaymentAccountFragment fragment = new PaymentAccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_PHONE, userPhone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userPhone = getArguments().getString(ARG_USER_PHONE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_payment_account, container, false);

        rvCards = view.findViewById(R.id.rvCards);
        Button btnAddCard = view.findViewById(R.id.btnAddCard);
        ImageView btnBack = view.findViewById(R.id.btnBack);

        foodAPI = RetrofitClient.getApi();

        setupRecyclerView();
        loadCards();

        btnAddCard.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, VisaDetailFragment.newInstance())
                    .addToBackStack(null)
                    .commit();
        });

        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load lại danh sách thẻ khi quay lại fragment
        loadCards();
    }

    private void setupRecyclerView() {
        adapter = new PaymentAccountAdapter(cardList);
        rvCards.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCards.setAdapter(adapter);

        adapter.setOnDeleteClickListener((account, position) -> {
            foodAPI.deletePaymentAccount(account.get_id()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        cardList.remove(position);
                        adapter.notifyItemRemoved(position);
                        Toast.makeText(getContext(), "Đã xoá thẻ", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Xoá thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getContext(), "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadCards() {
        if (userPhone == null || userPhone.isEmpty()) {
            Toast.makeText(getContext(), "Không tìm thấy số điện thoại người dùng.", Toast.LENGTH_SHORT).show();
            return;
        }
        foodAPI.getPaymentAccounts(userPhone).enqueue(new Callback<List<PaymentAccount>>() {
            @Override
            public void onResponse(Call<List<PaymentAccount>> call, Response<List<PaymentAccount>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cardList.clear();
                    cardList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<PaymentAccount>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi tải danh sách thẻ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}