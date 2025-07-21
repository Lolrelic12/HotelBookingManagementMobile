package com.example.hotelmanagement.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.ServiceListActivity;
import com.example.hotelmanagement.dto.BookedRoomResponse;
import com.example.hotelmanagement.dto.BookingResponse;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private List<BookedRoomResponse> bookingList;
    private Context context;
    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView roomName, date, status;
        Button orderServiceBtn;
        public BookingViewHolder(View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.roomNameText);
            date = itemView.findViewById(R.id.checkInOutText);
            status = itemView.findViewById(R.id.bookingStatusText);
            orderServiceBtn = itemView.findViewById(R.id.btnOrderService);
        }
    }

    public BookingAdapter(Context context, List<BookedRoomResponse> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @Override
    public BookingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookingViewHolder holder, int position) {
        BookedRoomResponse booking = bookingList.get(position);
        holder.roomName.setText(booking.getRoomNumber());
        holder.date.setText(booking.getCheckInDate());
        holder.status.setText(getBookingStatus(booking.getBookingStatus()));
        if (booking.getBookingStatus() != 3) {
            holder.orderServiceBtn.setEnabled(false);
            holder.orderServiceBtn.setAlpha(0.5f);
        } else {
            holder.orderServiceBtn.setEnabled(true);
            holder.orderServiceBtn.setAlpha(1f);
            holder.orderServiceBtn.setOnClickListener(v -> {
                Intent intent = new Intent(context, ServiceListActivity.class);
                intent.putExtra("bookingId", booking.getBookingId());
                intent.putExtra("roomId", booking.getRoomId());
                context.startActivity(intent);
            });
        }

    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public String getBookingStatus(int bookingStatus){
        switch (bookingStatus){
            case 1:
                return "Pending";
            case 2:
                return "Confirmed";
            case 3:
                return "Checked In";
            case 4:
                return "Completed";
            case 5:
                return "Cancelled";
            case 6:
                return "No Show";
        }
        return "unknown";
    }
}