package com.example.hotelmanagement;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hotelmanagement.adapter.ReviewAdapter;
import com.example.hotelmanagement.dto.*;
import com.example.hotelmanagement.services.api.ApiService;
import com.example.hotelmanagement.services.api.Callback;
import com.example.hotelmanagement.services.api.TokenManager;
import com.google.gson.Gson;
import android.app.AlertDialog;

import java.text.DecimalFormat;
import java.util.*;

public class RoomDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ROOM_ID = "room_id";
    public static final String EXTRA_ROOM_DATA = "room_data";

    private ApiService apiService;
    private RoomTypeActivity roomTypeService;
    private Gson gson = new Gson();

    private String roomId, roomGuid;
    private RoomResponse cachedRoomData;
    private DecimalFormat priceFormat = new DecimalFormat("0.00 VND");
    private DecimalFormat ratingFormat = new DecimalFormat("#0.00");
    private Button btnWriteReview;
    private boolean hasBookedRoom = false;

    private TextView tvRoomNumber, tvRoomId, tvRoomType, tvAvailability, tvCapacity, tvPrice, tvDescription, tvRating, tvReviews, tvNoReviews;
    private Button btnBookRoom;
    private RecyclerView rvReviews;
    private ReviewAdapter reviewAdapter;
    private List<ReviewReponse> reviewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Room Details");

        roomId = getIntent().getStringExtra(EXTRA_ROOM_ID);
        if (roomId == null) {
            Toast.makeText(this, "Room ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupServices();
        setupRecyclerView();
        loadRoomData();
        checkBookingSuccess();
    }

    private void initViews() {
        tvRoomNumber = findViewById(R.id.tvRoomNumber);
        tvRoomId = findViewById(R.id.tvRoomId);
        tvRoomType = findViewById(R.id.tvRoomType);
        tvAvailability = findViewById(R.id.tvAvailability);
        tvCapacity = findViewById(R.id.tvCapacity);
        tvPrice = findViewById(R.id.tvPrice);
        tvDescription = findViewById(R.id.tvDescription);
        tvRating = findViewById(R.id.tvRating);
        tvReviews = findViewById(R.id.tvReviews);
        tvNoReviews = findViewById(R.id.tvNoReviews);
        btnBookRoom = findViewById(R.id.btnBookRoom);
        rvReviews = findViewById(R.id.rvReviews);
        btnWriteReview = findViewById(R.id.btnWriteReview);

        btnWriteReview.setVisibility(View.VISIBLE);
        btnWriteReview.setOnClickListener(v -> handleWriteReview());
        btnBookRoom.setOnClickListener(v -> handleBookRoom());
    }

    private void setupServices() {
        apiService = ApiService.getInstance(this);
        roomTypeService = new RoomTypeActivity(this);
    }

    private void setupRecyclerView() {
        reviewAdapter = new ReviewAdapter(reviewList);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(reviewAdapter);
    }

    private void loadRoomData() {
        String roomDataJson = getIntent().getStringExtra(EXTRA_ROOM_DATA);
        if (roomDataJson != null) {
            try {
                cachedRoomData = gson.fromJson(roomDataJson, RoomResponse.class);
                roomGuid = cachedRoomData.getId();
                displayRoomDetails(cachedRoomData);
                loadRoomTypes();
                loadRoomReviews();
                return;
            } catch (Exception ignored) {}
        }
        loadRoomDetails();
    }

    private void loadRoomTypes() {
        roomTypeService.getAllRoomTypes(new Callback<RoomTypeResponse[]>() {
            @Override
            public void onSuccess(RoomTypeResponse[] result) {
                runOnUiThread(() -> {
                    if (cachedRoomData != null) updateRoomTypeDisplay(cachedRoomData);
                });
            }

            @Override
            public void onFailure(Throwable error) {
                runOnUiThread(() ->
                        Toast.makeText(RoomDetailActivity.this, "Failed to load room types", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void loadRoomDetails() {
        roomTypeService.getAllRoomTypes(new Callback<RoomTypeResponse[]>() {
            @Override
            public void onSuccess(RoomTypeResponse[] result) {
                runOnUiThread(RoomDetailActivity.this::fetchRoomDetails);
            }

            @Override
            public void onFailure(Throwable error) {
                runOnUiThread(RoomDetailActivity.this::fetchRoomDetails);
            }
        });
    }

    private void fetchRoomDetails() {
        apiService.getAsync("api/Room/GetByRoomId/" + roomId, RoomResponse.class, new Callback<RoomResponse>() {
            @Override
            public void onSuccess(RoomResponse result) {
                runOnUiThread(() -> {
                    if (result != null) {
                        roomGuid = result.getId();
                        cachedRoomData = result;
                        checkUserBookingStatus();
                        displayRoomDetails(result);
                        loadRoomReviews();
                    } else {
                        Toast.makeText(RoomDetailActivity.this, "Room not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(Throwable error) {
                runOnUiThread(() -> {
                    Toast.makeText(RoomDetailActivity.this, "Failed to load room details", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void displayRoomDetails(RoomResponse room) {
        tvRoomNumber.setText(defaultText(room.getRoomNumber()));
        tvRoomId.setText(defaultText(room.getRoomId()));
        tvCapacity.setText(room.getCapacity() + " guests");
        tvPrice.setText(priceFormat.format(room.getPrice()) + "/night");
        tvDescription.setText(defaultText(room.getDescription(), "No description available"));
        tvRating.setText(room.getTotalReviews() > 0 ? ratingFormat.format(room.getAverageRating()) + "/5" : "N/A");
        tvReviews.setText(room.getTotalReviews() > 0 ? room.getTotalReviews() + " reviews" : "No reviews");

        tvAvailability.setText(room.isAvailable() ? "Available" : "Unavailable");
        tvAvailability.setTextColor(Color.parseColor(room.isAvailable() ? "#4CAF50" : "#F44336"));
        btnBookRoom.setEnabled(room.isAvailable());

        updateRoomTypeDisplay(room);
    }

    private void updateRoomTypeDisplay(RoomResponse room) {
        String description = roomTypeService.getRoomTypeDescription(room.getRoomTypeId());
        tvRoomType.setText(defaultText(description, "Unknown Type"));
    }

    private void handleBookRoom() {
        if (cachedRoomData != null) {
            Intent intent = new Intent(this, BookingRoomActivity.class);
            intent.putExtra(BookingRoomActivity.EXTRA_ROOM_DATA, gson.toJson(cachedRoomData));
            startActivity(intent);
        } else {
            Toast.makeText(this, "Room data not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadRoomReviews() {
        if (roomGuid == null) {
            showNoReviews();
            return;
        }

        apiService.getAsync("api/Review/GetByRoomId/" + roomGuid, ReviewReponse[].class, new Callback<ReviewReponse[]>() {
            @Override
            public void onSuccess(ReviewReponse[] result) {
                runOnUiThread(() -> {
                    reviewList.clear();
                    if (result != null && result.length > 0) {
                        reviewList.addAll(Arrays.asList(result));
                        reviewAdapter.notifyDataSetChanged();
                        rvReviews.setVisibility(View.VISIBLE);
                        tvNoReviews.setVisibility(View.GONE);
                    } else {
                        showNoReviews();
                    }
                });
            }

            @Override
            public void onFailure(Throwable error) {
                runOnUiThread(() -> {
                    Toast.makeText(RoomDetailActivity.this, "Failed to load reviews", Toast.LENGTH_SHORT).show();
                    showNoReviews();
                });
            }
        });
    }

    private void showNoReviews() {
        reviewList.clear();
        reviewAdapter.notifyDataSetChanged();
        rvReviews.setVisibility(View.GONE);
        tvNoReviews.setVisibility(View.VISIBLE);
    }

    private String defaultText(String input) {
        return input != null ? input : "N/A";
    }

    private String defaultText(String input, String fallback) {
        return (input == null || input.trim().isEmpty()) ? fallback : input;
    }

    private void checkBookingSuccess() {
        boolean bookingSuccess = getIntent().getBooleanExtra("booking_success", false);
        if (bookingSuccess) {
            hasBookedRoom = true;
            Toast.makeText(this, "Booking successful! You can now write a review.", Toast.LENGTH_LONG).show();
            loadRoomDetails();
            checkUserBookingStatus();
        } else {
            checkUserBookingStatus();
        }
    }

    private void checkUserBookingStatus() {
        if (roomGuid == null || hasBookedRoom) return;

        String userId = "26A2DA04-D00F-41CB-8783-17FCF11F099C";
        String url = "api/Booking/GetAll?filterBy=UserId&searchTerm=" + userId;

        apiService.getAsync(url, BookingResponse[].class, new Callback<BookingResponse[]>() {
            @Override
            public void onSuccess(BookingResponse[] result) {
                runOnUiThread(() -> {
                    hasBookedRoom = result != null && Arrays.stream(result).anyMatch(b -> roomGuid.equals(b.getRoomId()));
                });
            }

            @Override
            public void onFailure(Throwable error) {
                runOnUiThread(() -> hasBookedRoom = false);
            }
        });
    }

    private void handleWriteReview() {
        if (!hasBookedRoom) {
            new AlertDialog.Builder(this)
                    .setTitle("Booking Required")
                    .setMessage("You need to book this room first before writing a review. Would you like to book now?")
                    .setPositiveButton("Book Now", (dialog, which) -> handleBookRoom())
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            showCreateReviewDialog();
        }
    }

    private void showCreateReviewDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_review, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        TextView tvRatingValue = dialogView.findViewById(R.id.tvRatingValue);
        EditText etReviewContent = dialogView.findViewById(R.id.etReviewContent);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSubmitReview = dialogView.findViewById(R.id.btnSubmitReview);

        ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) ->
                tvRatingValue.setText(String.format("%.1f", rating)));

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSubmitReview.setOnClickListener(v -> {
            String content = etReviewContent.getText().toString().trim();
            float rating = ratingBar.getRating();

            if (content.isEmpty()) {
                etReviewContent.setError("Please enter your review");
            } else if (rating == 0) {
                Toast.makeText(this, "Please provide a rating", Toast.LENGTH_SHORT).show();
            } else {
                submitReview(content, rating);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void submitReview(String content, double rating) {
        if (roomGuid == null) {
            Toast.makeText(this, "Room data not available", Toast.LENGTH_SHORT).show();
            return;
        }

        TokenManager tokenManager = new TokenManager(this);
        String token = tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        ReviewRequest reviewRequest = new ReviewRequest(UUID.fromString(roomGuid), content, rating);

        apiService.postAsync("api/Review/Create", reviewRequest, Object.class, new Callback<Object>() {
            @Override
            public void onSuccess(Object result) {
                runOnUiThread(() -> {
                    Toast.makeText(RoomDetailActivity.this, "Review submitted successfully!", Toast.LENGTH_LONG).show();
                    loadRoomReviews();
                    loadRoomDetails();
                });
            }

            @Override
            public void onFailure(Throwable error) {
                runOnUiThread(() -> {
                    String errorMessage = error.getMessage();
                    if (errorMessage != null && errorMessage.contains("401")) {
                        Toast.makeText(RoomDetailActivity.this, "Authentication failed. Please login again.", Toast.LENGTH_LONG).show();
                        tokenManager.clearToken();
                        Intent intent = new Intent(RoomDetailActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RoomDetailActivity.this, "Failed to submit review", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (cachedRoomData != null) {
            loadRoomDetails();
        }
    }
}
