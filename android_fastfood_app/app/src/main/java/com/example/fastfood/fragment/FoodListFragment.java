package com.example.fastfood.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodListFragment extends Fragment implements FoodAdapter.OnItemAddListener {

    private static final String ARG_CATEGORY = "category";

    private RecyclerView rvFoods;
    private FoodAdapter foodAdapter;
    private FloatingActionButton fabCart;
    private TextView tvTitle;
    private ImageView btnBack;
    private AppDatabase database;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    private String selectedCategory;
    private List<FoodModel> allFoodItems = new ArrayList<>();
    private FoodAPI foodApi;

    public static FoodListFragment newInstance(String category) {
        FoodListFragment fragment = new FoodListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedCategory = getArguments().getString(ARG_CATEGORY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_food_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = AppDatabase.getDatabase(getContext());
        rvFoods = view.findViewById(R.id.rv_foods);
        fabCart = view.findViewById(R.id.fab_cart);
        tvTitle = view.findViewById(R.id.tv_title);
        btnBack = view.findViewById(R.id.btn_back);
        FoodAPI api = RetrofitClient.getApi(); // Khởi tạo FoodAPI đúng chuẩn

        setupUI();
        setupRecyclerView();
        fetchData();
        setupCartButton();
        setupBackButton();
    }

    private void setupUI() {
        if (selectedCategory != null) {
            tvTitle.setText("Danh mục: " + selectedCategory);
        } else {
            tvTitle.setText("Tất cả món ăn");
        }
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

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> {
            // Navigate back to previous fragment
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
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
                    filterFoodsByCategory();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<FoodModel>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterFoodsByCategory() {
        List<FoodModel> filteredFoods = new ArrayList<>();

        if (selectedCategory == null || selectedCategory.isEmpty()) {
            // Show all foods if no category is selected
            filteredFoods.addAll(allFoodItems);
        } else {
            // Filter foods by selected category
            for (FoodModel food : allFoodItems) {
                if (selectedCategory.equals(food.getCategory())) {
                    filteredFoods.add(food);
                }
            }
        }

        foodAdapter.updateData(filteredFoods);

        if (filteredFoods.isEmpty()) {
            Toast.makeText(getContext(), "Không có món ăn nào trong danh mục này", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemAdd(FoodModel food) {
        Toast.makeText(getContext(), "Đã thêm: " + food.getName(), Toast.LENGTH_SHORT).show();

        databaseExecutor.execute(() -> {
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
        // Navigate to FoodDetailFragment
        FoodDetailFragment foodDetailFragment = FoodDetailFragment.newInstance(food);
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, foodDetailFragment)
                .addToBackStack(null)
                .commit();
    }
}
