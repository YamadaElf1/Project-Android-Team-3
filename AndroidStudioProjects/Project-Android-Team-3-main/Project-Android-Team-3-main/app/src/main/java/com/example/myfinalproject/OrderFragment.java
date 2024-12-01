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
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {


    private RecyclerView recyclerView;
    private OrderHistoryAdapter adapter;
    private List<OrderItem> orderList;
    private DatabaseReference orderDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_order_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderList = new ArrayList<>();
        adapter = new OrderHistoryAdapter(getContext(), orderList);
        recyclerView.setAdapter(adapter);

        loadOrderHistory();

        return view;
    }

    private void loadOrderHistory() {
        String userId = getArguments() != null ? getArguments().getString("userId") : null;
        if (userId == null || userId.isEmpty()) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() != null) {
                userId = mAuth.getCurrentUser().getUid();  // 获取当前用户的 UID
            } else {
                // 用户未登录，处理错误
                Toast.makeText(getContext(), "User not logged in. Please log in to view order history.", Toast.LENGTH_SHORT).show();
                return;  // 停止加载订单历史
            }
        }
        orderDatabase = FirebaseDatabase.getInstance().getReference("orderHistory").child(userId);

        orderDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    OrderItem order = data.getValue(OrderItem.class);
                    if (order != null) {
                        orderList.add(order);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}