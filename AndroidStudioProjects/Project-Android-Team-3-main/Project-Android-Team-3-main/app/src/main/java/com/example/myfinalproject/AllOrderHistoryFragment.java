package com.example.myfinalproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class AllOrderHistoryFragment extends Fragment {

    private RecyclerView ordersRecyclerView;
    private OrderHistoryAdapter ordersAdapter;
    private List<OrderItem> ordersList;
    private DatabaseReference ordersDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_order_history, container, false);

        ordersRecyclerView = view.findViewById(R.id.orders_recycler_view);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ordersList = new ArrayList<>();

        ordersAdapter = new OrderHistoryAdapter(getContext(), ordersList);
        ordersRecyclerView.setAdapter(ordersAdapter);

        ordersDatabase = FirebaseDatabase.getInstance().getReference("orderHistory");
        
        loadOrders();

        return view;
    }

    private void loadOrders() {
        ordersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ordersList.clear();
                for (DataSnapshot orderRecordSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot userOrderSnapshot : orderRecordSnapshot.getChildren()) {
                        OrderItem order = userOrderSnapshot.getValue(OrderItem.class);
                        if (order != null) {
                            Log.d("OrderHistoryFragment", "Order ID: " + order.getOrderId());
                            Log.d("OrderHistoryFragment", "User ID: " + order.getUserId());
                            Log.d("OrderHistoryFragment", "Order Date: " + order.getDate());
                            Log.d("OrderHistoryFragment", "Total Amount: " + order.getTotalAmount());

                            if (order.getItems() != null) {
                                for (OrderItem.OrderDetail detail : order.getItems()) {
                                    Log.d("OrderHistoryFragment", "Food Name: " + detail.getFoodName());
                                    Log.d("OrderHistoryFragment", "Quantity: " + detail.getQuantity());
                                    Log.d("OrderHistoryFragment", "Total Amount for Item: " + detail.getTotalAmount());
                                }
                            } else {
                                Log.d("OrderHistoryFragment", "No items found in this order.");
                            }
                            ordersList.add(order);
                        } else {
                            Log.e("OrderHistoryFragment", "Failed to parse userOrderSnapshot to OrderItem.");
                        }
                    }
                }
                ordersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load orders: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
