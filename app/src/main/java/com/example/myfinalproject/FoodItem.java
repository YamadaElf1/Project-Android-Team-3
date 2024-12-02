package com.example.myfinalproject;

public class FoodItem {
    private String id;
    private String foodType;
    private String foodName;
    private String foodPrice;

    public FoodItem() {
    }

    public FoodItem(String id, String foodType, String foodName, String foodPrice) {
        this.id = id;
        this.foodType = foodType;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
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
}