package com.example.fastfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.fastfood.R;
import com.example.fastfood.data.model.FoodModel;
import java.util.List;
import java.util.Locale;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    public interface OnItemAddListener {
        void onItemAdd(FoodModel food);
        void onItemClick(FoodModel food);
    }

    private List<FoodModel> foodList;
    private Context context;
    private OnItemAddListener listener;

    public FoodAdapter(Context context, List<FoodModel> foodList, OnItemAddListener listener) {
        this.context = context;
        this.foodList = foodList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodModel food = foodList.get(position);
        holder.bind(food, listener);
    }

    @Override
    public int getItemCount() {
        return foodList == null ? 0 : foodList.size();
    }

    public void updateData(List<FoodModel> newFoodList) {
        this.foodList.clear();
        if (newFoodList != null) {
            this.foodList.addAll(newFoodList);
        }
        notifyDataSetChanged();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImage;
        TextView foodName, foodDescription, foodPrice;
        ImageButton buttonAddItem;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.image_food);
            foodName = itemView.findViewById(R.id.text_food_name);
            foodDescription = itemView.findViewById(R.id.text_food_description);
            foodPrice = itemView.findViewById(R.id.text_food_price);
            buttonAddItem = itemView.findViewById(R.id.button_add_item);
        }

        void bind(final FoodModel food, final OnItemAddListener listener) {
            foodName.setText(food.getName());

            String formattedPrice = String.format(Locale.GERMAN, "%,.0fđ", food.getPrice());
            foodPrice.setText(formattedPrice);

            Glide.with(itemView.getContext())
                    .load(food.getImageUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(foodImage);

            buttonAddItem.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemAdd(food);
                }
            });
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(food);
                }
            });
        }
    }
}
