package com.example.myfinalproject;

import java.util.List;

public class OrderItem {


    private String userId;
    private String orderId;
    private String date;
    private double totalAmount;
    private List<OrderDetail> items;

    public OrderItem() {
    }

    public OrderItem(String orderId, String userId, String date, double totalAmount, List<OrderDetail> items) {
        this.orderId = orderId;
        this.userId = userId;
        this.date = date;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<OrderDetail> getItems() {
        return items;
    }

    public void setItems(List<OrderDetail> items) {
        this.items = items;
    }


    public static class OrderDetail {
        private String foodName;
        private int quantity;
        private double totalAmount;

        public OrderDetail() {
        }

        public OrderDetail(String foodName, int quantity, double totalAmount) {
            this.foodName = foodName;
            this.quantity = quantity;
            this.totalAmount = totalAmount;
        }

        public String getFoodName() {
            return foodName;
        }

        public void setFoodName(String foodName) {
            this.foodName = foodName;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
        }
    }
}