package com.example.fastfood.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fastfood.R;
import com.example.fastfood.data.local.AppDatabase;
import com.example.fastfood.data.local.CartItem;
import com.example.fastfood.data.model.FoodModel;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FoodDetailFragment extends Fragment {

    private static final String ARG_FOOD = "food";

    private ImageView imgFood, btnBack, btnDecrease, btnIncrease;
    private TextView tvFoodName, tvFoodDescription, tvFoodPrice, tvQuantity, tvTotalPrice;
    private EditText etNotes;
    private Button btnAddToCart;

    private FoodModel food;
    private int quantity = 1;
    private AppDatabase database;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    public static FoodDetailFragment newInstance(FoodModel food) {
        FoodDetailFragment fragment = new FoodDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FOOD, food);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            food = (FoodModel) getArguments().getSerializable(ARG_FOOD);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_food_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = AppDatabase.getDatabase(getContext());
        initViews(view);
        setupUI();
        setupClickListeners();
    }

    private void initViews(View view) {
        imgFood = view.findViewById(R.id.img_food);
        btnBack = view.findViewById(R.id.btn_back);
        tvFoodName = view.findViewById(R.id.tv_food_name);
        tvFoodDescription = view.findViewById(R.id.tv_food_description);
        tvFoodPrice = view.findViewById(R.id.tv_food_price);
        btnDecrease = view.findViewById(R.id.btn_decrease);
        btnIncrease = view.findViewById(R.id.btn_increase);
        tvQuantity = view.findViewById(R.id.tv_quantity);
        tvTotalPrice = view.findViewById(R.id.tv_total_price);
        etNotes = view.findViewById(R.id.et_notes);
        btnAddToCart = view.findViewById(R.id.btn_add_to_cart);
    }

    private void setupUI() {
        if (food != null) {
            tvFoodName.setText(food.getName());
            tvFoodDescription.setText(food.getDescription() != null ? food.getDescription() : "Mô tả món ăn ngon");

            String formattedPrice = String.format(Locale.GERMAN, "%,.0fđ", food.getPrice());
            tvFoodPrice.setText(formattedPrice);

            Glide.with(this)
                    .load(food.getImageUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(imgFood);

            updateQuantityAndPrice();
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });

        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantityAndPrice();
            }
        });

        btnIncrease.setOnClickListener(v -> {
            quantity++;
            updateQuantityAndPrice();
        });

        btnAddToCart.setOnClickListener(v -> addToCart());
    }

    private void updateQuantityAndPrice() {
        tvQuantity.setText(String.valueOf(quantity));
        double totalPrice = food.getPrice() * quantity;
        String formattedTotalPrice = String.format(Locale.GERMAN, "%,.0fđ", totalPrice);
        tvTotalPrice.setText(formattedTotalPrice);
    }

    private void addToCart() {
        if (food == null) return;

        String notes = etNotes.getText().toString().trim();

        databaseExecutor.execute(() -> {
            CartItem existingItem = database.cartDao().findItemById(String.valueOf(food.getId()));

            if (existingItem != null) {
                existingItem.quantity += quantity;
                // Update notes if provided
                if (!notes.isEmpty()) {
                    existingItem.notes = notes;
                }
                database.cartDao().update(existingItem);
            } else {
                CartItem newItem = new CartItem();
                newItem.foodId = String.valueOf(food.getId());
                newItem.name = food.getName();
                newItem.price = food.getPrice();
                newItem.imageUrl = food.getImageUrl();
                newItem.quantity = quantity;
                newItem.notes = notes;
                database.cartDao().insert(newItem);
            }

            // Show success message on main thread
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(),
                            "Đã thêm " + quantity + " " + food.getName() + " vào giỏ hàng",
                            Toast.LENGTH_SHORT).show();

                    // Navigate back after adding to cart
                    if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                        getParentFragmentManager().popBackStack();
                    }
                });
            }
        });
    }
}