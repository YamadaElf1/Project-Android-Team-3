package com.example.myfinalproject;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FoodMenuFragment extends Fragment {

    Button btnAddFood;
    ListView foodListView;
    DatabaseReference foodMenuDatabase;
    FoodAdapter foodAdapter;
    List<FoodItem> foodList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_menu, container, false);

        btnAddFood = view.findViewById(R.id.btn_addFood);
        foodListView = view.findViewById(R.id.lv_foodList);

        foodMenuDatabase = FirebaseDatabase.getInstance().getReference("foodmenu");

        foodList = new ArrayList<>();
        foodAdapter = new FoodAdapter(getActivity(), foodList,true,null);
        foodListView.setAdapter(foodAdapter);

        loadFoodItemsFromFirebase();

        btnAddFood.setOnClickListener(v -> showAddFoodDialog());

        return view;
    }

    private void showAddFoodDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setTitle("Add Food");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_food, null);

        EditText etFoodType = dialogView.findViewById(R.id.et_foodType);
        EditText etFoodName = dialogView.findViewById(R.id.et_foodName);
        EditText etFoodPrice = dialogView.findViewById(R.id.et_foodPrice);

        builder.setView(dialogView);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            String foodType = etFoodType.getText().toString().trim();
            String foodName = etFoodName.getText().toString().trim();
            String foodPrice = etFoodPrice.getText().toString().trim();

            if (!foodType.isEmpty() && !foodName.isEmpty() && !foodPrice.isEmpty()) {
                saveFoodToFirebase(foodType, foodName, foodPrice);
            } else {
                Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void saveFoodToFirebase(String foodType, String foodName, String foodPrice) {
        String foodId = foodMenuDatabase.push().getKey();

        FoodItem foodItem = new FoodItem(foodId, foodType, foodName, foodPrice);

        if (foodId != null) {
            foodMenuDatabase.child(foodId).setValue(foodItem)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getActivity(), "Food item added", Toast.LENGTH_SHORT).show();
                        loadFoodItemsFromFirebase();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void loadFoodItemsFromFirebase() {
        foodMenuDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                foodList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FoodItem foodItem = snapshot.getValue(FoodItem.class);
                    foodItem.setId(snapshot.getKey());
                    foodList.add(foodItem);
                }
                foodAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}