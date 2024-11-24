package com.example.myfinalproject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartList;
    private DatabaseReference cartDatabase;


    private TextView totalItemsView, totalPriceView;

    private String userId;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_cart_fragment, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        totalItemsView = view.findViewById(R.id.total_items);
        totalPriceView = view.findViewById(R.id.total_price);


        cartList = new ArrayList<>();

        if (getArguments() != null) {
            userId = getArguments().getString("userId");
        }

        if (userId == null || userId.isEmpty()) {
            Log.e("CartFragment", "User ID is null or empty");
            Toast.makeText(getContext(), "User ID not found", Toast.LENGTH_SHORT).show();
            return view;
        } else {
            Log.d("CartFragment", "Using User ID: " + userId);
        }

        cartDatabase = FirebaseDatabase.getInstance().getReference("cart").child(userId);
        cartAdapter = new CartAdapter(getContext(), cartList, this::updateQuantity, this::removeItem);
        recyclerView.setAdapter(cartAdapter);

        loadCart();

        Button proceedToPaymentButton = view.findViewById(R.id.btnpayment);
        proceedToPaymentButton.setOnClickListener(v -> {
            if (!cartList.isEmpty()) {
                double totalPrice = 0.0;

                // 计算总价
                for (CartItem item : cartList) {
                    totalPrice += item.getQuantity() * Double.parseDouble(item.getFoodPrice());
                }

                // 跳转到 PaymentFragment
                Fragment paymentFragment = new PaymentFragment();
                Bundle bundle = new Bundle();
                bundle.putString("userId", userId); // 传递用户 ID
                bundle.putString("totalPrice", String.format("%.2f", totalPrice)); // 传递总价
                paymentFragment.setArguments(bundle);

                // 替换当前 Fragment
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, paymentFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(getContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
            }
        });
        return view;

    }

    private void loadCart() {
        cartDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartList.clear();
                int totalItems = 0;
                double totalPrice = 0.0;

                for (DataSnapshot data : snapshot.getChildren()) {
                    CartItem item = data.getValue(CartItem.class);
                    if (item != null) {
                        Log.d("CartFragment", "Loaded item: " + item.getFoodName());
                        cartList.add(item);
                        totalItems += item.getQuantity();
                        totalPrice += item.getQuantity() * Double.parseDouble(item.getFoodPrice());
                    } else {
                        Log.e("CartFragment", "Failed to parse CartItem");
                    }
                }

                cartAdapter.notifyDataSetChanged();
                totalItemsView.setText("Total Items: " + totalItems);
                totalPriceView.setText("Total Price: $" + String.format("%.2f", totalPrice));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CartFragment", "Database error: " + error.getMessage());
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void updateQuantity(CartItem item, int newQuantity) {
        if (newQuantity <= 0) {
            removeItem(item);
        } else {
            cartDatabase.child(item.getId()).child("quantity").setValue(newQuantity);
            Toast.makeText(getContext(), "Quantity Updated", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeItem(CartItem item) {
        cartDatabase.child(item.getId()).removeValue();
        Toast.makeText(getContext(), "Item Removed", Toast.LENGTH_SHORT).show();
    }
}
