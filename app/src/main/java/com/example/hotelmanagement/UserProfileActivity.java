package com.example.hotelmanagement;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagement.adapter.BookingAdapter;
import com.example.hotelmanagement.dto.BookingResponse;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends BaseActivity {

    private RecyclerView bookingRecyclerView;
    private BookingAdapter adapter;
    private List<BookingResponse> bookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setupFooterNavigation();
        highlightFooterIcon(R.id.iconUser);

        // Dummy profile data
        TextView username = findViewById(R.id.usernameText);
        username.setText("John Doe");

        // Dummy booking data
        bookings = new ArrayList<>();
//        bookings.add(new BookingResponse("Room 101", "2025-07-15", "Checked In"));
//        bookings.add(new BookingResponse("Room 202", "2025-06-12", "Checked Out"));
//        bookings.add(new BookingResponse("Room 305", "2025-05-05", "Cancelled"));

        bookingRecyclerView = findViewById(R.id.bookingRecyclerView);
        bookingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BookingAdapter(bookings);
        bookingRecyclerView.setAdapter(adapter);
    }
}