package com.example.myfinalproject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {

    private ListView foodListView;
    private List<FoodItem> menuList;
    private FoodAdapter menuAdapter;
    private DatabaseReference menuDatabase;
    private String userId;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_menu_fragment, container, false);

        foodListView = view.findViewById(R.id.list_view_menu);
        menuList = new ArrayList<>();

        if (getArguments() != null) {
            userId = getArguments().getString("userId");
        }

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(getContext(), "User ID not found", Toast.LENGTH_SHORT).show();
            return view; // 如果 userId 为空，直接返回
        }

        menuAdapter = new FoodAdapter(getContext(), menuList, false, userId);
        foodListView.setAdapter(menuAdapter);

        menuDatabase = FirebaseDatabase.getInstance().getReference("foodmenu");
        loadMenuItems();

        return view;
    }

    private void loadMenuItems() {
        menuDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                menuList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    FoodItem item = data.getValue(FoodItem.class);
                    if (item != null) {
                        menuList.add(item);
                    }
                }
                menuAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load menu items", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
