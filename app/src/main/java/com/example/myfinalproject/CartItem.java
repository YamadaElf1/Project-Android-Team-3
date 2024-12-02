package com.example.myfinalproject;


public class CartItem {
    private String id;
    private String foodName;
    private String foodPrice;
    private String foodType;
    private int quantity = 1;

    public CartItem() {

    }

    public CartItem(String id, String foodName, String foodPrice, String foodType, int quantity) {
        this.id = id;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodType = foodType;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(String foodPrice) {
        this.foodPrice = foodPrice;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}


