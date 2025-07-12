package com.example.fastfood.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastfood.R;
import com.example.fastfood.data.model.PaymentAccount;

import java.util.List;

public class PaymentAccountAdapter extends RecyclerView.Adapter<PaymentAccountAdapter.CardViewHolder> {
    private List<PaymentAccount> cardList;
    private OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDelete(PaymentAccount account, int position);
    }

    public PaymentAccountAdapter(List<PaymentAccount> cardList) {
        this.cardList = cardList;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_card, parent, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        PaymentAccount account = cardList.get(position);
        holder.tvCardHolder.setText(account.getCardHolder());

        // Ẩn số thẻ: chỉ hiện 4 số cuối
        String cardNum = account.getCardNumber();
        if (cardNum != null && cardNum.length() > 4) {
            String showNum = "**** **** **** " + cardNum.substring(cardNum.length() - 4);
            holder.tvCardNumber.setText(showNum);
        } else {
            holder.tvCardNumber.setText(cardNum != null ? cardNum : "");
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onDelete(account, position);
        });

        Log.d("AdapterCheck", "Item: " + account.getCardHolder() + " | " + cardNum);
    }

    @Override
    public int getItemCount() {
        return cardList != null ? cardList.size() : 0;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView tvCardHolder, tvCardNumber;
        ImageView btnDelete;
        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCardHolder = itemView.findViewById(R.id.tvCardHolder);
            tvCardNumber = itemView.findViewById(R.id.tvCardNumber);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
