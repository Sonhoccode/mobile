package com.example.fastfood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fastfood.R;
import com.example.fastfood.data.local.CartItem;
import com.example.fastfood.data.model.PaymentAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// **ĐÂY LÀ TÊN LỚP ĐÚNG**
public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_FOOD_ITEM = 1;
    private static final int TYPE_SUMMARY = 2;

    private Context context;
    private List<Object> displayList;
    private CartItemListener listener;
    private List<PaymentAccount> visaCards;

    public interface CartItemListener {
        void onQuantityIncrease(CartItem item);
        void onQuantityDecrease(CartItem item);
        void onItemDelete(CartItem item);
        void onCheckout(int paymentMethodId, int visaCardPosition, double totalAmount);
    }

    // **ĐÂY LÀ HÀM DỰNG ĐÚNG**
    public CartAdapter(Context context, List<Object> displayList, CartItemListener listener) {
        this.context = context;
        this.displayList = displayList;
        this.listener = listener;
        this.visaCards = new ArrayList<>();
    }

    public void setVisaCards(List<PaymentAccount> cards) {
        this.visaCards.clear();
        if (cards != null) {
            this.visaCards.addAll(cards);
        }
        int summaryPosition = -1;
        for (int i = 0; i < displayList.size(); i++) {
            if (displayList.get(i) instanceof String && ((String) displayList.get(i)).equals("SUMMARY")) {
                summaryPosition = i;
                break;
            }
        }
        if (summaryPosition != -1) {
            notifyItemChanged(summaryPosition);
        }
    }

    public List<PaymentAccount> getVisaCards() {
        return this.visaCards;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = displayList.get(position);
        if (item instanceof String && "HEADER".equals(item)) {
            return TYPE_HEADER;
        } else if (item instanceof CartItem) {
            return TYPE_FOOD_ITEM;
        } else if (item instanceof String && "SUMMARY".equals(item)) {
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
                return new RecyclerView.ViewHolder(new View(context)) {};
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case TYPE_FOOD_ITEM:
                ((FoodItemViewHolder) holder).bind((CartItem) displayList.get(position), listener);
                break;
            case TYPE_SUMMARY:
                ((SummaryViewHolder) holder).bind(displayList, visaCards, listener);
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
            android.content.SharedPreferences prefs = itemView.getContext().getSharedPreferences("USER_PREFS", android.content.Context.MODE_PRIVATE);
            String userName = prefs.getString("userName", "Khách hàng");
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
            itemDescription.setText(item.notes != null ? item.notes : "");
            itemPrice.setText(String.format(Locale.GERMAN, "%,.0fđ", item.price));
            itemQuantity.setText(String.valueOf(item.quantity));

            Glide.with(itemView.getContext()).load(item.imageUrl).placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_error).into(itemImage);

            btnIncrease.setOnClickListener(v -> listener.onQuantityIncrease(item));
            btnDecrease.setOnClickListener(v -> listener.onQuantityDecrease(item));
            btnDelete.setOnClickListener(v -> listener.onItemDelete(item));
        }
    }

    static class SummaryViewHolder extends RecyclerView.ViewHolder {
        TextView subtotal, deliveryFee, total;
        Button checkoutButton;
        RadioGroup rgPaymentMethod;
        RadioButton rbVisa;
        Spinner spinnerVisa;

        public SummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            subtotal = itemView.findViewById(R.id.text_subtotal_value);
            deliveryFee = itemView.findViewById(R.id.text_delivery_fee_value);
            total = itemView.findViewById(R.id.text_total_value);
            checkoutButton = itemView.findViewById(R.id.button_checkout);
            rgPaymentMethod = itemView.findViewById(R.id.rgPaymentMethod);
            rbVisa = itemView.findViewById(R.id.rbVisa);
            spinnerVisa = itemView.findViewById(R.id.spinnerVisa);
        }

        void bind(List<Object> items, List<PaymentAccount> visaCards, final CartItemListener listener) {
            double subtotalValue = 0;
            for (Object item : items) {
                if (item instanceof CartItem) {
                    subtotalValue += ((CartItem) item).price * ((CartItem) item).quantity;
                }
            }
            double deliveryFeeValue = 15000;
            final double totalValue = subtotalValue + deliveryFeeValue;

            subtotal.setText(String.format(Locale.GERMAN, "%,.0fđ", subtotalValue));
            deliveryFee.setText(String.format(Locale.GERMAN, "%,.0fđ", deliveryFeeValue));
            total.setText(String.format(Locale.GERMAN, "%,.0fđ", totalValue));

            if (visaCards != null && !visaCards.isEmpty()) {
                rbVisa.setVisibility(View.VISIBLE);
                List<String> cardDisplayNames = new ArrayList<>();
                for (PaymentAccount card : visaCards) {
                    String lastFour = card.getCardNumber().length() > 4 ? card.getCardNumber().substring(card.getCardNumber().length() - 4) : card.getCardNumber();
                    cardDisplayNames.add("Visa **** " + lastFour);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(itemView.getContext(), android.R.layout.simple_spinner_item, cardDisplayNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerVisa.setAdapter(adapter);
            } else {
                rbVisa.setVisibility(View.GONE);
                spinnerVisa.setVisibility(View.GONE);
            }

            rgPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.rbVisa) {
                    spinnerVisa.setVisibility(View.VISIBLE);
                } else {
                    spinnerVisa.setVisibility(View.GONE);
                }
            });

            checkoutButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCheckout(rgPaymentMethod.getCheckedRadioButtonId(), spinnerVisa.getSelectedItemPosition(), totalValue);
                }
            });
        }
    }
}
