package com.example.myfinalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class FoodAdapter extends ArrayAdapter<FoodItem> {

    private Context context;
    private List<FoodItem> foodList;
    private DatabaseReference foodMenuDatabase;
    private DatabaseReference cartDatabase;
    private boolean isRestaurantMode;
    private String userId; // 用户ID，用于顾客的购物车操作

    public FoodAdapter(Context context, List<FoodItem> foodList, boolean isRestaurantMode, @Nullable String userId) {
        super(context, R.layout.food_item_row, foodList);
        this.context = context;
        this.foodList = foodList;
        this.isRestaurantMode = isRestaurantMode;
        this.userId = userId;
        this.foodMenuDatabase = FirebaseDatabase.getInstance().getReference("foodmenu");
        // 如果是顾客模式，初始化购物车数据库引用
        if (!isRestaurantMode && userId != null) {
            this.cartDatabase = FirebaseDatabase.getInstance().getReference("cart").child(userId);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.food_item_row, parent, false);
        }

        FoodItem foodItem = foodList.get(position);

        TextView textFoodType = convertView.findViewById(R.id.tv_foodType);
        TextView textFoodName = convertView.findViewById(R.id.tv_foodName);
        TextView textFoodPrice = convertView.findViewById(R.id.tv_foodPrice);
        Button actionButton = convertView.findViewById(R.id.btnDeleteFood); // 动态按钮

        textFoodType.setText(foodItem.getFoodType());
        textFoodName.setText(foodItem.getFoodName());
        String priceWithDollar = "$" + foodItem.getFoodPrice();
        textFoodPrice.setText(priceWithDollar);

        if (isRestaurantMode) {
            // 餐厅端：删除按钮
            actionButton.setText("Delete");
            actionButton.setOnClickListener(v -> showDeleteConfirmationDialog(foodItem));
        } else {
            // 顾客端：添加到购物车按钮
            actionButton.setText("Add");
            actionButton.setOnClickListener(v -> addToCart(foodItem));
        }

        return convertView;
    }

    // 餐厅端：显示确认删除对话框
    private void showDeleteConfirmationDialog(FoodItem foodItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Food Item");
        builder.setMessage("Are you sure you want to delete this food item?");
        builder.setPositiveButton("Yes", (dialog, which) -> deleteFoodItem(foodItem.getId()));
        builder.setNegativeButton("No", null);
        builder.show();
    }

    // 餐厅端：删除菜品
    private void deleteFoodItem(String foodId) {
        foodMenuDatabase.child(foodId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (int i = 0; i < foodList.size(); i++) {
                    if (foodList.get(i).getId().equals(foodId)) {
                        foodList.remove(i);
                        break;
                    }
                }
                notifyDataSetChanged();
                Toast.makeText(context, "Food item deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to delete food item", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 顾客端：添加到购物车
    private void addToCart(FoodItem foodItem) {
        if (cartDatabase == null) {
            Toast.makeText(context, "Unable to access cart database", Toast.LENGTH_SHORT).show();
            return;
        }

        CartItem cartItem = new CartItem(foodItem.getId(), foodItem.getFoodName(), foodItem.getFoodPrice(), foodItem.getFoodType(), 1);
        cartDatabase.child(foodItem.getId()).setValue(cartItem)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to add to Cart", Toast.LENGTH_SHORT).show());
    }
}
