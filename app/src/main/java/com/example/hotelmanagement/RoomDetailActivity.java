package com.example.hotelmanagement;

import android.app.AlertDialog;
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
import com.example.hotelmanagement.adapter.ImageAdapter;
import com.example.hotelmanagement.adapter.ReviewAdapter;
import com.example.hotelmanagement.dto.*;
import com.example.hotelmanagement.services.api.ApiService;
import com.example.hotelmanagement.services.api.Callback;
import com.example.hotelmanagement.services.api.TokenManager;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.*;

public class RoomDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ROOM_ID = "room_id";
    public static final String EXTRA_ROOM_DATA = "room_data";

    private ApiService apiService;
    private RoomTypeActivity roomTypeService;
    private Gson gson = new Gson();

    private String roomId, roomGuid;
    private boolean hasBookedRoom = false;
    private RoomResponse cachedRoomData;

    private DecimalFormat priceFormat = new DecimalFormat("0.00 VND");
    private DecimalFormat ratingFormat = new DecimalFormat("#0.00");

    private List<ImageResponse> roomImages = new ArrayList<>();
    private List<ReviewReponse> reviewList = new ArrayList<>();

    private ImageAdapter imageAdapter;
    private ReviewAdapter reviewAdapter;

    private TextView tvRoomNumber, tvRoomId, tvRoomType, tvAvailability, tvCapacity, tvPrice,
            tvDescription, tvRating, tvReviews, tvNoReviews;
    private Button btnBookRoom, btnWriteReview;
    private RecyclerView rvRoomImages, rvReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        setupToolbar();
        roomId = getIntent().getStringExtra(EXTRA_ROOM_ID);
        if (roomId == null) {
            showToast("Room ID not provided");
            finish();
            return;
        }

        initViews();
        setupServices();
        setupRecyclerViews();
        loadRoomData();
        checkBookingSuccess();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Room Details");
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
        btnWriteReview = findViewById(R.id.btnWriteReview);
        btnWriteReview.setVisibility(View.VISIBLE);
        rvRoomImages = findViewById(R.id.rvRoomImages);
        rvReviews = findViewById(R.id.rvReviews);

        btnBookRoom.setOnClickListener(v -> handleBookRoom());
        btnWriteReview.setOnClickListener(v -> handleWriteReview());
    }

    private void setupServices() {
        apiService = ApiService.getInstance(this);
        roomTypeService = new RoomTypeActivity(this);
    }

    private void setupRecyclerViews() {
        imageAdapter = new ImageAdapter(roomImages, this);
        rvRoomImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvRoomImages.setAdapter(imageAdapter);

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
                runOnUi(() -> {
                    if (cachedRoomData != null)
                        updateRoomTypeDisplay(cachedRoomData);
                });
            }
            public void onFailure(Throwable error) {
                runOnUi(() -> showToast("Failed to load room types"));
            }
        });
    }

    private void loadRoomDetails() {
        roomTypeService.getAllRoomTypes(new Callback<RoomTypeResponse[]>() {
            public void onSuccess(RoomTypeResponse[] result) { runOnUi(RoomDetailActivity.this::fetchRoomDetails); }
            public void onFailure(Throwable error) { runOnUi(RoomDetailActivity.this::fetchRoomDetails); }
        });
    }

    private void fetchRoomDetails() {
        apiService.getAsync("api/Room/GetByRoomId/" + roomId, RoomResponse.class, new Callback<RoomResponse>() {
            public void onSuccess(RoomResponse result) {
                runOnUi(() -> {
                    if (result != null) {
                        roomGuid = result.getId();
                        cachedRoomData = result;
                        checkUserBookingStatus();
                        displayRoomDetails(result);
                        loadRoomReviews();
                    } else {
                        showToast("Room not found");
                        finish();
                    }
                });
            }
            public void onFailure(Throwable error) {
                runOnUi(() -> {
                    showToast("Failed to load room details");
                    finish();
                });
            }
        });
    }

    private void displayRoomDetails(RoomResponse room) {
        tvRoomNumber.setText(textOrDefault(room.getRoomNumber()));
        tvRoomId.setText(textOrDefault(room.getRoomId()));
        tvCapacity.setText(room.getCapacity() + " guests");
        tvPrice.setText(priceFormat.format(room.getPrice()) + "/night");
        tvDescription.setText(textOrDefault(room.getDescription(), "No description available"));
        tvRating.setText(room.getTotalReviews() > 0 ? ratingFormat.format(room.getAverageRating()) + "/5" : "N/A");
        tvReviews.setText(room.getTotalReviews() > 0 ? room.getTotalReviews() + " reviews" : "No reviews");

        tvAvailability.setText(room.isAvailable() ? "Available" : "Unavailable");
        tvAvailability.setTextColor(Color.parseColor(room.isAvailable() ? "#4CAF50" : "#F44336"));
        btnBookRoom.setEnabled(room.isAvailable());

        updateRoomTypeDisplay(room);
        loadRoomImages();
    }

    private void updateRoomTypeDisplay(RoomResponse room) {
        String desc = roomTypeService.getRoomTypeDescription(room.getRoomTypeId());
        tvRoomType.setText(textOrDefault(desc, "Unknown Type"));
    }

    private void loadRoomImages() {
        if (roomGuid == null) {
            hideRoomImages();
            return;
        }

        apiService.getAsync("api/Image/GetImagesByRoomId/" + roomGuid, ImageResponse[].class, new Callback<>() {
            public void onSuccess(ImageResponse[] result) {
                runOnUi(() -> {
                    roomImages.clear();
                    if (result != null && result.length > 0) {
                        roomImages.addAll(Arrays.asList(result));
                        imageAdapter.notifyDataSetChanged();
                        rvRoomImages.setVisibility(View.VISIBLE);
                    } else hideRoomImages();
                });
            }

            public void onFailure(Throwable error) {
                runOnUi(() -> {
                    showToast("Failed to load room images");
                    hideRoomImages();
                });
            }
        });
    }

    private void hideRoomImages() {
        roomImages.clear();
        imageAdapter.notifyDataSetChanged();
        rvRoomImages.setVisibility(View.GONE);
    }

    private void loadRoomReviews() {
        if (roomGuid == null) {
            hideRoomReviews();
            return;
        }

        apiService.getAsync("api/Review/GetByRoomId/" + roomGuid, ReviewReponse[].class, new Callback<>() {
            public void onSuccess(ReviewReponse[] result) {
                runOnUi(() -> {
                    reviewList.clear();
                    if (result != null && result.length > 0) {
                        reviewList.addAll(Arrays.asList(result));
                        reviewAdapter.notifyDataSetChanged();
                        rvReviews.setVisibility(View.VISIBLE);
                        tvNoReviews.setVisibility(View.GONE);
                    } else hideRoomReviews();
                });
            }

            public void onFailure(Throwable error) {
                runOnUi(() -> {
                    showToast("Failed to load reviews");
                    hideRoomReviews();
                });
            }
        });
    }

    private void hideRoomReviews() {
        reviewList.clear();
        reviewAdapter.notifyDataSetChanged();
        rvReviews.setVisibility(View.GONE);
        tvNoReviews.setVisibility(View.VISIBLE);
    }

    private void handleBookRoom() {
        if (cachedRoomData != null) {
            Intent intent = new Intent(this, BookingRoomActivity.class);
            intent.putExtra(BookingRoomActivity.EXTRA_ROOM_DATA, gson.toJson(cachedRoomData));
            startActivity(intent);
        } else {
            showToast("Room data not available");
        }
    }

    private void handleWriteReview() {
        if (!hasBookedRoom) {
            new AlertDialog.Builder(this)
                    .setTitle("Booking Required")
                    .setMessage("You need to book this room before writing a review.")
                    .setPositiveButton("Book Now", (d, w) -> handleBookRoom())
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            showCreateReviewDialog();
        }
    }

    private void showCreateReviewDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_create_review, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        EditText etContent = view.findViewById(R.id.etReviewContent);
        TextView tvValue = view.findViewById(R.id.tvRatingValue);
        Button btnSubmit = view.findViewById(R.id.btnSubmitReview), btnCancel = view.findViewById(R.id.btnCancel);

        ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> tvValue.setText(String.format("%.1f", rating)));
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSubmit.setOnClickListener(v -> {
            String content = etContent.getText().toString().trim();
            float rating = ratingBar.getRating();

            if (content.isEmpty()) etContent.setError("Enter review");
            else if (rating == 0) showToast("Please provide a rating");
            else {
                submitReview(content, rating);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void submitReview(String content, double rating) {
        if (roomGuid == null) {
            showToast("Room data not available");
            return;
        }

        TokenManager tokenManager = new TokenManager(this);
        if (tokenManager.getToken() == null) {
            showToast("Please login first");
            return;
        }

        ReviewRequest request = new ReviewRequest(UUID.fromString(roomGuid), content, rating);
        apiService.postAsync("api/Review/Create", request, Object.class, new Callback<>() {
            public void onSuccess(Object res) {
                runOnUi(() -> {
                    showToast("Review submitted!");
                    loadRoomReviews();
                    loadRoomDetails();
                });
            }

            public void onFailure(Throwable err) {
                runOnUi(() -> {
                    if (err.getMessage() != null && err.getMessage().contains("401")) {
                        tokenManager.clearToken();
                        startActivity(new Intent(RoomDetailActivity.this, LoginActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    } else {
                        showToast("Failed to submit review");
                    }
                });
            }
        });
    }

    private void checkBookingSuccess() {
        if (getIntent().getBooleanExtra("booking_success", false)) {
            hasBookedRoom = true;
            showToast("Booking successful! You can now write a review.");
            loadRoomDetails();
        }
        checkUserBookingStatus();
    }

    private void checkUserBookingStatus() {
        if (roomGuid == null || hasBookedRoom) return;

        String userId = "26A2DA04-D00F-41CB-8783-17FCF11F099C";
        String url = "api/Booking/GetAll?filterBy=UserId&searchTerm=" + userId;

        apiService.getAsync(url, BookingResponse[].class, new Callback<>() {
            public void onSuccess(BookingResponse[] result) {
                runOnUi(() -> {
                    hasBookedRoom = result != null && Arrays.stream(result).anyMatch(b -> roomGuid.equals(b.getRoomId()));
                });
            }

            public void onFailure(Throwable error) {
                runOnUi(() -> hasBookedRoom = false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cachedRoomData != null) {
            loadRoomDetails();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void runOnUi(Runnable r) {
        runOnUiThread(r);
    }

    private String textOrDefault(String value) {
        return value != null && !value.trim().isEmpty() ? value : "N/A";
    }

    private String textOrDefault(String value, String fallback) {
        return value != null && !value.trim().isEmpty() ? value : fallback;
    }
}
