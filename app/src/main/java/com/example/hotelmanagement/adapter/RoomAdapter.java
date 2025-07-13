package com.example.hotelmanagement.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.*;
import com.example.hotelmanagement.dto.RoomResponse;
import java.text.DecimalFormat;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private List<RoomResponse> roomList;
    private DecimalFormat decimalFormat;
    private RoomTypeActivity roomTypeService;

    public RoomAdapter(List<RoomResponse> roomList, RoomTypeActivity roomTypeService) {
        this.roomList = roomList;
        this.roomTypeService = roomTypeService;
        this.decimalFormat = new DecimalFormat("#0.00");
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        RoomResponse room = roomList.get(position);

        holder.tvRoomNumber.setText("Room: " + (room.getRoomNumber() != null ? room.getRoomNumber() : "N/A"));

        holder.tvRoomId.setText("ID: " + (room.getRoomId() != null ? room.getRoomId() : "N/A"));

        if (room.isAvailable()) {
            holder.tvAvailability.setText("Available");
            holder.tvAvailability.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            holder.tvAvailability.setText("Unavailable");
            holder.tvAvailability.setTextColor(Color.parseColor("#F44336"));
        }

        String roomTypeDescription = "Unknown Type";
        if (roomTypeService != null && room.getRoomTypeId() != null) {
            String description = roomTypeService.getRoomTypeDescription(room.getRoomTypeId());
            if (description != null && !description.equals("Unknown Type")) {
                roomTypeDescription = description;
            }
        }
        holder.tvRoomType.setText("Type: " + roomTypeDescription);

        holder.tvCapacity.setText("Capacity: " + room.getCapacity() + " guests");

        holder.tvPrice.setText("Price: $" + decimalFormat.format(room.getPrice()) + "/night");

        String description = room.getDescription();
        if (description == null || description.trim().isEmpty()) {
            description = "No description available";
        }
        holder.tvDescription.setText(description);

        if (room.getTotalReviews() > 0) {
            holder.tvRating.setText("Rating: " + decimalFormat.format(room.getAverageRating()) + "/5");
            holder.tvReviews.setText("(" + room.getTotalReviews() + " reviews)");
        } else {
            holder.tvRating.setText("Rating: N/A");
            holder.tvReviews.setText("(No reviews)");
        }
    }

    public void refreshData() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomNumber, tvRoomId, tvRoomType, tvAvailability, tvCapacity, tvPrice, tvDescription, tvRating, tvReviews;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomNumber = itemView.findViewById(R.id.tvRoomNumber);
            tvRoomId = itemView.findViewById(R.id.tvRoomId);
            tvRoomType = itemView.findViewById(R.id.tvRoomType);
            tvAvailability = itemView.findViewById(R.id.tvAvailability);
            tvCapacity = itemView.findViewById(R.id.tvCapacity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvReviews = itemView.findViewById(R.id.tvReviews);
        }
    }
}