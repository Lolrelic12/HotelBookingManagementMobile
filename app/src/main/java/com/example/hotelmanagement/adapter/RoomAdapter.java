package com.example.hotelmanagement.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.RoomDetailActivity;
import com.example.hotelmanagement.RoomTypeActivity;
import com.example.hotelmanagement.dto.ImageResponse;
import com.example.hotelmanagement.dto.RoomResponse;
import com.google.gson.Gson;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    private final List<RoomResponse> roomList;
    private final DecimalFormat decimalFormat;
    private final RoomTypeActivity roomTypeService;
    private final Gson gson = new Gson();
    private Context context;
    private List<ImageResponse> imageList = new ArrayList<>();

    public RoomAdapter(List<RoomResponse> roomList, RoomTypeActivity roomTypeService) {
        this.roomList = roomList;
        this.roomTypeService = roomTypeService;
        this.decimalFormat = new DecimalFormat("#0.00");
    }

    public void setImages(List<ImageResponse> images) {
        this.imageList = images;
        notifyDataSetChanged();
    }

    private List<ImageResponse> getImagesForRoom(String roomId) {
        List<ImageResponse> roomImages = new ArrayList<>();
        if (roomId == null || imageList == null) return roomImages;
        for (ImageResponse image : imageList) {
            if (roomId.equals(image.getRoomId())) {
                roomImages.add(image);
            }
        }
        return roomImages;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        RoomResponse room = roomList.get(position);

        holder.tvRoomNumber.setText("Room: " + defaultText(room.getRoomNumber()));
        holder.tvRoomId.setText("ID: " + defaultText(room.getRoomId()));
        holder.tvAvailability.setText(room.isAvailable() ? "Available" : "Unavailable");
        holder.tvAvailability.setTextColor(Color.parseColor(room.isAvailable() ? "#4CAF50" : "#F44336"));

        String roomTypeDescription = "Unknown Type";
        if (roomTypeService != null && room.getRoomTypeId() != null) {
            String description = roomTypeService.getRoomTypeDescription(room.getRoomTypeId());
            if (description != null && !description.equals("Unknown Type")) {
                roomTypeDescription = description;
            }
        }
        holder.tvRoomType.setText("Type: " + roomTypeDescription);
        holder.tvCapacity.setText("Capacity: " + room.getCapacity() + " guests");
        holder.tvPrice.setText("Price: " + decimalFormat.format(room.getPrice()) + " VND/night");

        String description = room.getDescription();
        holder.tvDescription.setText((description == null || description.trim().isEmpty()) ? "No description available" : description);

        if (room.getTotalReviews() > 0) {
            holder.tvRating.setText("Rating: " + decimalFormat.format(room.getAverageRating()) + "/5");
            holder.tvReviews.setText("(" + room.getTotalReviews() + " reviews)");
        } else {
            holder.tvRating.setText("Rating: N/A");
            holder.tvReviews.setText("(No reviews)");
        }

        List<ImageResponse> roomImages = getImagesForRoom(room.getId());
        if (!roomImages.isEmpty()) {
            holder.imageRecyclerView.setVisibility(View.VISIBLE);
            holder.imageRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.imageRecyclerView.setAdapter(new ImageAdapter(roomImages, context));
        } else {
            holder.imageRecyclerView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RoomDetailActivity.class);
            intent.putExtra(RoomDetailActivity.EXTRA_ROOM_ID, room.getRoomId());
            intent.putExtra(RoomDetailActivity.EXTRA_ROOM_DATA, gson.toJson(room));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public void refreshData() {
        notifyDataSetChanged();
    }

    private String defaultText(String text) {
        return (text != null) ? text : "N/A";
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomNumber, tvRoomId, tvRoomType, tvAvailability, tvCapacity, tvPrice, tvDescription, tvRating, tvReviews;
        RecyclerView imageRecyclerView;

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
            imageRecyclerView = itemView.findViewById(R.id.recyclerViewRoomImages);
        }
    }
}
