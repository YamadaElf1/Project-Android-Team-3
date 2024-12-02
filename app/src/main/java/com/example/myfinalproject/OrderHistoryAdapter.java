package com.example.myfinalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private final Context context;
    private final List<OrderItem> orderList;

    public OrderHistoryAdapter(Context context, List<OrderItem> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_history_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderItem order = orderList.get(position);

        holder.tvOrderDate.setText("Order Date: " + order.getDate());
        holder.tvOrderId.setText("Order id: " + order.getUserId());
        holder.tvOrderTotal.setText("Total: $" + String.format("%.2f", order.getTotalAmount()));

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            StringBuilder orderDetails = new StringBuilder();
            for (OrderItem.OrderDetail detail : order.getItems()) {
                orderDetails.append(detail.getFoodName())
                        .append(" (x")
                        .append(detail.getQuantity())
                        .append(") - $")
                        .append(String.format("%.2f", detail.getTotalAmount()))
                        .append("\n");
            }
            holder.tvOrderDetails.setText(orderDetails.toString());
        } else {
            holder.tvOrderDetails.setText("No items in this order.");
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId,tvOrderDate, tvOrderTotal, tvOrderDetails;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvOrderTotal = itemView.findViewById(R.id.tv_order_total);
            tvOrderDetails = itemView.findViewById(R.id.tv_order_details);
        }
    }
}
