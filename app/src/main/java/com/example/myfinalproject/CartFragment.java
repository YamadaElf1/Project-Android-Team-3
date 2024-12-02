package com.example.myfinalproject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartList;
    private DatabaseReference cartDatabase;

    private TextView totalItemsView, totalPriceView;
    private Button proceedToPaymentButton;

    private String name;
    private String userId;
    private PaymentSheet paymentSheet;
    private String paymentIntentClientSecret;
    private PaymentSheet.CustomerConfiguration customerConfig;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_cart_fragment, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        totalItemsView = view.findViewById(R.id.total_items);
        totalPriceView = view.findViewById(R.id.total_price);
        proceedToPaymentButton = view.findViewById(R.id.btnpayment);

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

        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

        proceedToPaymentButton.setOnClickListener(v -> {
            if (!cartList.isEmpty()) {
                double totalPrice = 0.0;

                for (CartItem item : cartList) {
                    totalPrice += item.getQuantity() * Double.parseDouble(item.getFoodPrice());
                }

                int amountInCents = (int) Math.round(totalPrice * 100);

                getDetails(String.valueOf(amountInCents));
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

    private void getDetails(String amount) {
        Fuel.INSTANCE.post("https://strippayment-zsct3t7h6a-uc.a.run.app?amt=" + amount, null)
                .responseString(new Handler<String>() {
                    @Override
                    public void success(String s) {
                        try {
                            JSONObject result = new JSONObject(s);
                            customerConfig = new PaymentSheet.CustomerConfiguration(
                                    result.getString("customer"),
                                    result.getString("ephemeralKey")
                            );
                            paymentIntentClientSecret = result.getString("paymentIntent");
                            PaymentConfiguration.init(requireContext(), result.getString("publishableKey"));

                            requireActivity().runOnUiThread(() -> showStripePaymentSheet());

                        } catch (JSONException e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void failure(@NonNull FuelError fuelError) {
                        Toast.makeText(getContext(), "Network error: " + fuelError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showStripePaymentSheet() {
        PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("Your App Name")
                .customer(customerConfig)
                .allowsDelayedPaymentMethods(true)
                .build();

        paymentSheet.presentWithPaymentIntent(
                paymentIntentClientSecret,
                configuration
        );
    }

    private void onPaymentSheetResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(getContext(), "Payment Cancelled", Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Toast.makeText(getContext(), ((PaymentSheetResult.Failed) paymentSheetResult).getError().toString(), Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(getContext(), "Payment Successful", Toast.LENGTH_SHORT).show();
            clearCart();
        }
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

    private void clearCart() {
        if (userId != null) {
            String orderId = String.valueOf(System.currentTimeMillis());
            String orderDate = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date()); // 获取当前日期和时间

            List<OrderItem.OrderDetail> orderItems = new ArrayList<>();
            double totalPrice = 0.0;

            for (CartItem item : cartList) {
                double itemTotal = item.getQuantity() * Double.parseDouble(item.getFoodPrice());
                totalPrice += itemTotal;

                orderItems.add(new OrderItem.OrderDetail(item.getFoodName(), item.getQuantity(), itemTotal));
            }

            OrderItem orderHistory = new OrderItem(userId,orderId, orderDate, totalPrice, orderItems);

            FirebaseDatabase.getInstance().getReference("orderHistory")
                    .child(userId)
                    .child(orderId)
                    .setValue(orderHistory)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            cartDatabase.removeValue();
                            cartList.clear();
                            cartAdapter.notifyDataSetChanged();
                            totalItemsView.setText("Total Items: 0");
                            totalPriceView.setText("Total Price: $0.00");

                            Toast.makeText(getContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to place order", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}

