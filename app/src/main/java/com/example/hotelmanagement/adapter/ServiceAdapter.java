package com.example.hotelmanagement.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.ServiceDetailActivity;
import com.example.hotelmanagement.dto.Service.ServiceResponse;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<ServiceResponse> services;
    private final OnAddClickListener listener;
    private final Context context;

    public interface OnAddClickListener {
        void onAddClick(ServiceResponse item);
    }

    public ServiceAdapter(List<ServiceResponse> services, OnAddClickListener listener, Context context) {
        this.services = services;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_card, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        ServiceResponse item = services.get(position);
        holder.tvName.setText(item.name);
        holder.tvPrice.setText(String.format("Price: VND %.2f", item.price));
        holder.tvStatus.setText(item.available ? "Status: Available" : "Status: Unavailable");
        holder.tvStatus.setTextColor(item.available ? Color.GREEN : Color.RED);
        holder.btnAdd.setOnClickListener(v -> listener.onAddClick(item));
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ServiceDetailActivity.class);
            intent.putExtra("name", item.getName());
            intent.putExtra("price", item.getPrice());
            intent.putExtra("description", item.getDescription());
            intent.putExtra("image", item.getImageUrl());
            intent.putExtra("available", item.isAvailable());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc, tvPrice, tvStatus;
        ImageView img;
        Button btnAdd;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvServiceName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            img = itemView.findViewById(R.id.imgService);
            btnAdd = itemView.findViewById(R.id.btnAddService);
        }
    }
}
