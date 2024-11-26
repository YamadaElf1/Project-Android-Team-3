package com.example.myfinalproject;

import java.util.List;

public class OrderItem {

    private String orderId;
    private String userId;
    private String date;
    private double totalAmount;
    private List<OrderDetail> items;

    // 无参构造函数 (Firebase 必须)
    public OrderItem() {
    }

    // 带参构造函数
    public OrderItem(String orderId, String userId, String date, double totalAmount, List<OrderDetail> items) {
        this.orderId = orderId;
        this.userId = userId;
        this.date = date;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    // Getter 和 Setter 方法
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

    /**
     * 内部类：OrderDetail，用于表示每个订单项的详细信息
     */
    public static class OrderDetail {
        private String foodName;     // 食物名称
        private int quantity;        // 数量
        private double totalAmount;  // 总金额 (单价 * 数量)

        // 无参构造函数
        public OrderDetail() {
        }

        // 带参构造函数
        public OrderDetail(String foodName, int quantity, double totalAmount) {
            this.foodName = foodName;
            this.quantity = quantity;
            this.totalAmount = totalAmount;
        }

        // Getter 和 Setter 方法
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