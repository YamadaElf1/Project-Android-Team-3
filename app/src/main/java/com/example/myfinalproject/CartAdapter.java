package com.example.myfinalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartList;
    private QuantityUpdateListener quantityUpdateListener;
    private ItemRemoveListener itemRemoveListener;

    public CartAdapter(Context context, List<CartItem> cartList,
                       QuantityUpdateListener quantityUpdateListener, ItemRemoveListener itemRemoveListener) {
        this.context = context;
        this.cartList = cartList;
        this.quantityUpdateListener = quantityUpdateListener;
        this.itemRemoveListener = itemRemoveListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item_row, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartList.get(position);

        holder.textItemName.setText(cartItem.getFoodName());
        holder.textItemPrice.setText("$" + cartItem.getFoodPrice());
        holder.textItemQuantity.setText(String.valueOf(cartItem.getQuantity()));

        holder.buttonIncrease.setOnClickListener(v -> {
            int newQuantity = cartItem.getQuantity() + 1;
            quantityUpdateListener.onUpdateQuantity(cartItem, newQuantity);
        });

        holder.buttonDecrease.setOnClickListener(v -> {
            int newQuantity = cartItem.getQuantity() - 1;
            if (newQuantity <= 0) {
                itemRemoveListener.onRemoveItem(cartItem);
            } else {
                quantityUpdateListener.onUpdateQuantity(cartItem, newQuantity);
            }
        });

        holder.buttonRemove.setOnClickListener(v -> itemRemoveListener.onRemoveItem(cartItem));
    }


    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView textItemName, textItemPrice, textItemQuantity;
        Button buttonIncrease, buttonDecrease, buttonRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            textItemName = itemView.findViewById(R.id.cart_item_name);
            textItemPrice = itemView.findViewById(R.id.cart_item_price);
            textItemQuantity = itemView.findViewById(R.id.cart_item_quantity);
            buttonIncrease = itemView.findViewById(R.id.btn_increase_quantity);
            buttonDecrease = itemView.findViewById(R.id.btn_decrease_quantity);
            buttonRemove = itemView.findViewById(R.id.btn_remove_item);
        }
    }

    public interface QuantityUpdateListener {
        void onUpdateQuantity(CartItem item, int newQuantity);
    }

    public interface ItemRemoveListener {
        void onRemoveItem(CartItem item);
    }
}
