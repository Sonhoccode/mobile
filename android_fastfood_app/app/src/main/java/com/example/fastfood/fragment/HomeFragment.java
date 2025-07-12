package com.example.fastfood.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastfood.R;
import com.example.fastfood.activity.CartActivity;
import com.example.fastfood.adapter.FoodAdapter;
import com.example.fastfood.data.api.FoodAPI;
import com.example.fastfood.data.api.RetrofitClient;
import com.example.fastfood.data.local.AppDatabase;
import com.example.fastfood.data.local.CartItem;
import com.example.fastfood.data.model.FoodModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements FoodAdapter.OnItemAddListener {

    private RecyclerView rvFoods;
    private FoodAdapter foodAdapter;
    private FloatingActionButton fabCart;
    private AppDatabase database;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();
    private TextView tvSeeMore;
    private List<String> categoryList = new ArrayList<>();
    private List<FoodModel> allFoodItems = new ArrayList<>();
    private FoodAPI foodApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = AppDatabase.getDatabase(getContext());
        rvFoods = view.findViewById(R.id.rv_foods);
        fabCart = view.findViewById(R.id.fab_cart);
        tvSeeMore = view.findViewById(R.id.tv_see_more);

        foodApi = RetrofitClient.getApi(); // Khởi tạo FoodAPI đúng chuẩn

        setupRecyclerView();
        fetchData();
        setupCartButton();
        handleSeeMoreClick();
    }

    private void handleSeeMoreClick() {
        tvSeeMore.setOnClickListener(v -> {
            CategoryFragment categoryFragment = new CategoryFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, CategoryFragment.newInstance(new ArrayList<>(categoryList)))
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void setupRecyclerView() {
        rvFoods.setLayoutManager(new LinearLayoutManager(getContext()));
        foodAdapter = new FoodAdapter(getContext(), new ArrayList<>(), this);
        rvFoods.setAdapter(foodAdapter);
    }

    private void setupCartButton() {
        fabCart.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CartActivity.class);
            startActivity(intent);
        });
    }

    private void fetchData() {
        Call<List<FoodModel>> call = foodApi.getFoods(); // Gọi qua instance FoodAPI
        call.enqueue(new Callback<List<FoodModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<FoodModel>> call, @NonNull Response<List<FoodModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allFoodItems.clear();
                    allFoodItems.addAll(response.body());

                    foodAdapter.updateData(response.body());

                    Set<String> categories = new HashSet<>();
                    for (FoodModel item : allFoodItems) {
                        if (item.getCategory() != null && !item.getCategory().isEmpty()) {
                            categories.add(item.getCategory());
                        }
                    }
                    categoryList.clear();
                    categoryList.addAll(new ArrayList<>(categories));
                } else {
                    Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<FoodModel>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemAdd(FoodModel food) {
        Toast.makeText(getContext(), "Đã thêm: " + food.getName(), Toast.LENGTH_SHORT).show();

        databaseExecutor.execute(() -> {
            // Lưu ý dùng String.valueOf(food.getId()) như trong code 2
            CartItem existingItem = database.cartDao().findItemById(String.valueOf(food.getId()));

            if (existingItem != null) {
                existingItem.quantity++;
                database.cartDao().update(existingItem);
            } else {
                CartItem newItem = new CartItem();
                newItem.foodId = String.valueOf(food.getId());
                newItem.name = food.getName();
                newItem.price = food.getPrice();
                newItem.imageUrl = food.getImageUrl();
                newItem.quantity = 1;
                database.cartDao().insert(newItem);
            }
        });
    }

    @Override
    public void onItemClick(FoodModel food) {
        FoodDetailFragment foodDetailFragment = FoodDetailFragment.newInstance(food);
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, foodDetailFragment)
                .addToBackStack(null)
                .commit();
    }
}
