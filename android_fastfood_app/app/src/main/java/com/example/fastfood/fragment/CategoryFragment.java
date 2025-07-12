package com.example.fastfood.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastfood.R;
import com.example.fastfood.adapter.CategoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener {
    private static final String ARG_CATEGORY_LIST = "category_list";
    private RecyclerView rvCategories;
    private CategoryAdapter categoryAdapter;
    private ImageView btnBack;
    private List<String> categoryList;

    public static CategoryFragment newInstance(ArrayList<String> categoryList) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_CATEGORY_LIST, categoryList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Lấy danh sách category từ Arguments (Bundle)
            categoryList = getArguments().getStringArrayList(ARG_CATEGORY_LIST);
        } else {
            // Trường hợp không có category nào được truyền vào, khởi tạo danh sách rỗng
            categoryList = new ArrayList<>();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCategories = view.findViewById(R.id.rv_categories);
        btnBack = view.findViewById(R.id.btn_back);

        setupRecyclerView();
        setupBackButton();
    }

    private void setupRecyclerView() {
        // Add "All Dishes" category at the beginning
        List<String> displayCategories = new ArrayList<>();
        displayCategories.add("Tất cả món ăn");
        if (categoryList != null) {
            displayCategories.addAll(categoryList);
        }

        // Use GridLayoutManager with 2 columns for better category display
        rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 2));
        categoryAdapter = new CategoryAdapter(getContext(), displayCategories, this);
        rvCategories.setAdapter(categoryAdapter);
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> {
            // Navigate back to previous fragment
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    @Override
    public void onCategoryClick(String category) {
        Toast.makeText(getContext(), "Đã chọn: " + category, Toast.LENGTH_SHORT).show();

        // Handle "All Dishes" category by passing null to show all foods
        String selectedCategory = category;
        if ("Tất cả món ăn".equals(category)) {
            selectedCategory = null; // This will show all foods in FoodListFragment
        }

        // Navigate to FoodListFragment with selected category
        FoodListFragment foodListFragment = FoodListFragment.newInstance(selectedCategory);
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, foodListFragment)
                .addToBackStack(null)
                .commit();
    }
}
