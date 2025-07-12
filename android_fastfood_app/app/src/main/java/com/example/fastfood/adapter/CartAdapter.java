package com.example.fastfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fastfood.R;
import com.example.fastfood.data.local.CartItem;

import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_FOOD_ITEM = 1;
    private static final int TYPE_SUMMARY = 2;

    private Context context;
    private List<Object> displayList;
    private CartItemListener listener;


    public interface CartItemListener {
        void onQuantityIncrease(CartItem item);
        void onQuantityDecrease(CartItem item);
        void onItemDelete(CartItem item);
    }

    public CartAdapter(Context context, List<Object> displayList, CartItemListener listener) {
        this.context = context;
        this.displayList = displayList;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = displayList.get(position);
        if (item instanceof String && item.equals("HEADER")) {
            return TYPE_HEADER;
        } else if (item instanceof CartItem) {
            return TYPE_FOOD_ITEM;
        } else if (item instanceof String && item.equals("SUMMARY")) {
            return TYPE_SUMMARY;
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType) {
            case TYPE_HEADER:
                View headerView = inflater.inflate(R.layout.item_cart_header, parent, false);
                return new HeaderViewHolder(headerView);
            case TYPE_FOOD_ITEM:
                View foodView = inflater.inflate(R.layout.item_cart, parent, false);
                return new FoodItemViewHolder(foodView);
            case TYPE_SUMMARY:
                View summaryView = inflater.inflate(R.layout.item_cart_summary, parent, false);
                return new SummaryViewHolder(summaryView);
            default:
                // Trong trường hợp an toàn, trả về một ViewHolder rỗng thay vì gây crash
                return new RecyclerView.ViewHolder(new View(context)) {};
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case TYPE_FOOD_ITEM:
                FoodItemViewHolder foodHolder = (FoodItemViewHolder) holder;
                CartItem cartItem = (CartItem) displayList.get(position);
                foodHolder.bind(cartItem, listener);
                break;
            case TYPE_SUMMARY:
                SummaryViewHolder summaryHolder = (SummaryViewHolder) holder;
                summaryHolder.bind(displayList);
                break;
            case TYPE_HEADER:
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    public void updateItems(List<Object> newItems) {
        this.displayList.clear();
        this.displayList.addAll(newItems);
        notifyDataSetChanged();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView textCustomerName;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            textCustomerName = itemView.findViewById(R.id.text_customer_name);
            android.content.SharedPreferences prefs = itemView.getContext().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE);
            String userName = prefs.getString("user_name", "Khách hàng");
            textCustomerName.setText("Anh/chị: " + userName);
        }
    }
    static class FoodItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemDescription, itemPrice, itemQuantity;
        ImageButton btnIncrease, btnDecrease, btnDelete;

        public FoodItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.image_cart_item);
            itemName = itemView.findViewById(R.id.text_cart_item_name);
            itemDescription = itemView.findViewById(R.id.text_cart_item_description);
            itemPrice = itemView.findViewById(R.id.text_cart_item_price);
            itemQuantity = itemView.findViewById(R.id.text_cart_item_quantity);
            btnIncrease = itemView.findViewById(R.id.button_increase_quantity);
            btnDecrease = itemView.findViewById(R.id.button_decrease_quantity);
            btnDelete = itemView.findViewById(R.id.button_delete_item);
        }

        void bind(final CartItem item, final CartItemListener listener) {
            itemName.setText(item.name);
            itemDescription.setText(item.notes);
            itemPrice.setText(String.format(Locale.GERMAN, "%,.0fđ", item.price));
            itemQuantity.setText(String.valueOf(item.quantity));

            Glide.with(itemView.getContext())
                    .load(item.imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(itemImage);

            btnIncrease.setOnClickListener(v -> listener.onQuantityIncrease(item));
            btnDecrease.setOnClickListener(v -> listener.onQuantityDecrease(item));
            btnDelete.setOnClickListener(v -> listener.onItemDelete(item));
        }
    }

    static class SummaryViewHolder extends RecyclerView.ViewHolder {
        TextView subtotal, deliveryFee, total;
        Button checkoutButton;
        public SummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            subtotal = itemView.findViewById(R.id.text_subtotal_value);
            deliveryFee = itemView.findViewById(R.id.text_delivery_fee_value);
            total = itemView.findViewById(R.id.text_total_value);
            checkoutButton = itemView.findViewById(R.id.button_checkout);
        }

        void bind(List<Object> items) {
            double subtotalValue = 0;
            for (Object item : items) {
                if (item instanceof CartItem) {
                    subtotalValue += ((CartItem) item).price * ((CartItem) item).quantity;
                }
            }
            double deliveryFeeValue = 15000;
            double totalValue = subtotalValue + deliveryFeeValue;

            subtotal.setText(String.format(Locale.GERMAN, "%,.0fđ", subtotalValue));
            deliveryFee.setText(String.format(Locale.GERMAN, "%,.0fđ", deliveryFeeValue));
            total.setText(String.format(Locale.GERMAN, "%,.0fđ", totalValue));
        }
    }
}