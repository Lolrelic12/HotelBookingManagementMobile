package com.example.hotelmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagement.adapter.BookingAdapter;
import com.example.hotelmanagement.dto.BookedRoomResponse;
import com.example.hotelmanagement.dto.BookingResponse;
import com.example.hotelmanagement.dto.RoomResponse;
import com.example.hotelmanagement.extensions.JwtHelper;
import com.example.hotelmanagement.services.api.ApiService;
import com.example.hotelmanagement.services.api.Callback;
import com.example.hotelmanagement.services.api.TokenManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserProfileActivity extends BaseActivity {

    private RecyclerView bookingRecyclerView;
    private BookingAdapter adapter;
    private List<BookedRoomResponse> roomsBooked = new ArrayList<BookedRoomResponse>();
    private Button logoutButton;

    private ApiService apiService;
    private TokenManager tokenManager;
    JwtHelper jwtHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setupFooterNavigation();
        highlightFooterIcon(R.id.iconUser);
        apiService = ApiService.getInstance(this);
        logoutButton = findViewById(R.id.logoutButton);
        tokenManager = new TokenManager(this);
        jwtHelper = new JwtHelper(tokenManager.getToken());

        if (!tokenManager.hasToken()) {
            Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        logoutButton.setOnClickListener(v -> {
            ApiService.getInstance(this).postAsync("api/Auth/Logout", new Object(), Void.class, new Callback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    tokenManager.clearToken();
                    Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Throwable error) {
                    // You can display error message here
                }
            });
        });

        TextView username = findViewById(R.id.usernameText);
        username.setText(jwtHelper.getUsername());
        String userid = jwtHelper.getUserId();
        loadBookedRooms(userid);
        bookingRecyclerView = findViewById(R.id.bookingRecyclerView);
        bookingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BookingAdapter(this,roomsBooked);
        bookingRecyclerView.setAdapter(adapter);
    }

    private void loadBookedRooms(String userId) {
        apiService.getAsync("api/Booking/GetBookingByUserId?userId=" + userId, BookedRoomResponse[].class, new Callback<BookedRoomResponse[]>() {
            @Override
            public void onSuccess(BookedRoomResponse[] result) {
                runOnUiThread(() -> {
                    if (result != null) {
                        Collections.addAll(roomsBooked, result);
                    }
                });
            }

            @Override
            public void onFailure(Throwable error) {
                runOnUiThread(() ->
                        Toast.makeText(UserProfileActivity.this, "Failed to load booked rooms", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}