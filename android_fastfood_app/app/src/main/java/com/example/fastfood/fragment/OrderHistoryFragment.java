package com.example.fastfood.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastfood.R;
import com.example.fastfood.adapter.FoodAdapter;
import com.example.fastfood.data.api.FoodAPI;
import com.example.fastfood.data.api.RetrofitClient;
import com.example.fastfood.data.model.FoodModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryFragment extends Fragment {

    private RecyclerView recyclerViewFoods;
    private FoodAdapter foodAdapter;
    private List<FoodModel> foodModelList = new ArrayList<>();
    private FoodAPI foodApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_history, container, false); // Bạn nên tạo file XML tương ứng
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewFoods = view.findViewById(R.id.rv_order_history);
        recyclerViewFoods.setLayoutManager(new LinearLayoutManager(getContext()));

        foodAdapter = new FoodAdapter(getContext(), foodModelList, null);
        recyclerViewFoods.setAdapter(foodAdapter);

        foodApi = RetrofitClient.getApi();
        loadFoods();
    }

    private void loadFoods() {
        Call<List<FoodModel>> call = foodApi.getFoods(); // hoặc gọi getOrderHistory() nếu có API riêng
        call.enqueue(new Callback<List<FoodModel>>() {
            @Override
            public void onResponse(Call<List<FoodModel>> call, Response<List<FoodModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    foodAdapter.updateData(response.body());
                    Log.d("API_SUCCESS", "Đã tải " + response.body().size() + " món ăn.");
                } else {
                    String errorMessage = "Không tải được dữ liệu.";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += " Lỗi: " + response.code() + " - " + response.errorBody().string();
                        } catch (IOException e) {
                            errorMessage += " [Lỗi đọc errorBody]";
                        }
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<FoodModel>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
