package com.example.hotelmanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.dto.BookingResponse;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private List<BookingResponse> bookingList;

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView roomName, date, status;

        public BookingViewHolder(View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.roomName);
            date = itemView.findViewById(R.id.date);
            status = itemView.findViewById(R.id.status);
        }
    }

    public BookingAdapter(List<BookingResponse> bookingList) {
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
//        BookingResponse booking = bookingList.get(position);
//        holder.roomName.setText(booking.getRoomName());
//        holder.date.setText(booking.getDate());
//        holder.status.setText(booking.getStatus());
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }
}