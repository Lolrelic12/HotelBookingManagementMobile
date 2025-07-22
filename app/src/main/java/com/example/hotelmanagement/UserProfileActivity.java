package com.example.hotelmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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
import com.example.hotelmanagement.extensions.JwtHelper;
import com.example.hotelmanagement.services.api.ApiService;
import com.example.hotelmanagement.services.api.Callback;
import com.example.hotelmanagement.services.api.TokenManager;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends BaseActivity {

    private RecyclerView bookingRecyclerView;
    private BookingAdapter adapter;
    private List<BookingResponse> bookings;
    private Button logoutButton, changePasswordButton;
    private TokenManager tokenManager;
    JwtHelper jwtHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setupFooterNavigation();
        highlightFooterIcon(R.id.iconUser);

        logoutButton = findViewById(R.id.logoutButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);
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

        changePasswordButton.setOnClickListener(v -> {
                    Intent intent = new Intent(UserProfileActivity.this, ChangePasswordActivity.class);
                    startActivity(intent);
        });

        TextView username = findViewById(R.id.usernameText);
        username.setText(jwtHelper.getUsername());

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