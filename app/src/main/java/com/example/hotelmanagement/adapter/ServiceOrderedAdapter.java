package com.example.hotelmanagement.adapter;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.dto.Service.ServiceOrderedResponse;

import java.util.ArrayList;
import java.util.List;

public class ServiceOrderedAdapter extends RecyclerView.Adapter<ServiceOrderedAdapter.ViewHolder> {

    private ArrayList<ServiceOrderedResponse> serviceOrderedList = new ArrayList<>();

    public ServiceOrderedAdapter(ArrayList<ServiceOrderedResponse> serviceOrderedList) {
        this.serviceOrderedList = serviceOrderedList;
    }

    @NonNull
    @Override
    public ServiceOrderedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service_ordered, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceOrderedAdapter.ViewHolder holder, int position) {
        ServiceOrderedResponse service = serviceOrderedList.get(position);

        holder.tvServiceName.setText(service.getService().getName());
        holder.tvServicePrice.setText(String.format("Price: VND %.2f", service.getUnitPrice()));
        String status;
        if (service.isApproved()){
            status = "Approved";
        }
        else{
            status = "Not approved";
        }
        holder.tvServiceStatus.setText("Status: " + status);

        // Optional: Change text color based on status
        if (service.isApproved()) {
            holder.tvServiceStatus.setTextColor(Color.GREEN);
        } else {
            holder.tvServiceStatus.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return serviceOrderedList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName, tvServicePrice, tvServiceStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tvServiceOrderedName);
            tvServicePrice = itemView.findViewById(R.id.tvServicePrice);
            tvServiceStatus = itemView.findViewById(R.id.tvServiceStatus);
        }
    }
}
