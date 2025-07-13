package com.example.hotelmanagement;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.hotelmanagement.dto.*;
import com.example.hotelmanagement.services.api.ApiService;
import com.example.hotelmanagement.services.api.Callback;
import com.google.gson.Gson;
import java.text.DecimalFormat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hotelmanagement.adapter.ReviewAdapter;
import java.util.ArrayList;
import java.util.List;

public class RoomDetailActivity extends AppCompatActivity {
    public static final String EXTRA_ROOM_ID = "room_id";
    public static final String EXTRA_ROOM_DATA = "room_data";

    private ApiService apiService;
    private RoomTypeActivity roomTypeService;
    private DecimalFormat decimalFormat;
    private String roomId;
    private String roomGuid;
    private DecimalFormat ratingFormat;
    private Gson gson;
    private RoomResponse cachedRoomData;

    private TextView tvRoomNumber;
    private TextView tvRoomId;
    private TextView tvRoomType;
    private TextView tvAvailability;
    private TextView tvCapacity;
    private TextView tvPrice;
    private TextView tvDescription;
    private TextView tvRating;
    private TextView tvReviews;
    private Button btnBookRoom;
    private Button btnEditRoom;
    private RecyclerView rvReviews;
    private TextView tvNoReviews;
    private ReviewAdapter reviewAdapter;
    private List<ReviewReponse> reviewList;

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
        btnBookRoom = findViewById(R.id.btnBookRoom);

        rvReviews = findViewById(R.id.rvReviews);
        tvNoReviews = findViewById(R.id.tvNoReviews);

        if (btnBookRoom != null) {
            btnBookRoom.setOnClickListener(v -> handleBookRoom());
        }
    }

    private void setupRecyclerView() {
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewList);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(reviewAdapter);
    }

    private void setupServices() {
        apiService = ApiService.getInstance(this);
        roomTypeService = new RoomTypeActivity(this);
        decimalFormat = new DecimalFormat("0.00 VND");
        ratingFormat = new DecimalFormat("#0.00");
        gson = new Gson();
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
            } catch (Exception e) {
                Toast.makeText(this, "Error parsing room data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        loadRoomDetails();
    }

    private void loadRoomTypes() {
        roomTypeService.getAllRoomTypes(new Callback<RoomTypeResponse[]>() {
            @Override
            public void onSuccess(RoomTypeResponse[] result) {
                runOnUiThread(() -> {
                    if (cachedRoomData != null) {
                        updateRoomTypeDisplay(cachedRoomData);
                    }
                });
            }
            @Override
            public void onFailure(Throwable error) {
                runOnUiThread(() -> {
                    Toast.makeText(RoomDetailActivity.this,
                            "Failed to load room types: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    private void loadRoomDetails() {
        roomTypeService.getAllRoomTypes(new Callback<RoomTypeResponse[]>() {
            @Override
            public void onSuccess(RoomTypeResponse[] result) {
                runOnUiThread(() -> {
                    fetchRoomDetails();
                });
            }
            @Override
            public void onFailure(Throwable error) {
                runOnUiThread(() -> {
                    Toast.makeText(RoomDetailActivity.this,
                            "Failed to load room types: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    fetchRoomDetails();
                });
            }
        });
    }
    private void fetchRoomDetails() {
        apiService.getAsync(
                "api/Room/GetByRoomId/" + roomId,
                RoomResponse.class,
                new Callback<RoomResponse>() {
                    @Override
                    public void onSuccess(RoomResponse result) {
                        runOnUiThread(() -> {
                            if (result != null) {
                                roomGuid = result.getId();
                                displayRoomDetails(result);
                                loadRoomReviews();
                            } else {
                                Toast.makeText(RoomDetailActivity.this,
                                        "Room not found",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        runOnUiThread(() -> {
                            Toast.makeText(RoomDetailActivity.this,
                                    "Failed to load room details: " + error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }
                }
        );
    }
    private void displayRoomDetails(RoomResponse room) {
        if (tvRoomNumber != null) {
            tvRoomNumber.setText(room.getRoomNumber() != null ? room.getRoomNumber() : "N/A");
        }
        if (tvRoomId != null) {
            tvRoomId.setText(room.getRoomId() != null ? room.getRoomId() : "N/A");
        }
        if (tvAvailability != null) {
            if (room.isAvailable()) {
                tvAvailability.setText("Available");
                tvAvailability.setTextColor(Color.parseColor("#4CAF50"));
                if (btnBookRoom != null) {
                    btnBookRoom.setEnabled(true);
                }
            } else {
                tvAvailability.setText("Unavailable");
                tvAvailability.setTextColor(Color.parseColor("#F44336"));
                if (btnBookRoom != null) {
                    btnBookRoom.setEnabled(false);
                }
            }
        }

        updateRoomTypeDisplay(room);

        if (tvCapacity != null) {
            tvCapacity.setText(room.getCapacity() + " guests");
        }
        if (tvPrice != null) {
            tvPrice.setText(decimalFormat.format(room.getPrice()) + "/night");
        }
        if (tvDescription != null) {
            String description = room.getDescription();
            if (description == null || description.trim().isEmpty()) {
                description = "No description available";
            }
            tvDescription.setText(description);
        }

        if (tvRating != null) {
            if (room.getTotalReviews() > 0) {
                tvRating.setText(ratingFormat.format(room.getAverageRating()) + "/5");
            } else {
                tvRating.setText("N/A");
            }
        }

        if (tvReviews != null) {
            if (room.getTotalReviews() > 0) {
                tvReviews.setText(room.getTotalReviews() + " reviews");
            } else {
                tvReviews.setText("No reviews");
            }
        }
    }

    private void updateRoomTypeDisplay(RoomResponse room) {
        if (tvRoomType != null) {
            String roomTypeDescription = "Unknown Type";
            if (roomTypeService != null && room.getRoomTypeId() != null) {
                String description = roomTypeService.getRoomTypeDescription(room.getRoomTypeId());
                if (description != null && !description.equals("Unknown Type")) {
                    roomTypeDescription = description;
                }
            }
            tvRoomType.setText(roomTypeDescription);
        }
    }

    private void handleBookRoom() {
        // Implementation for booking room
    }

    private void loadRoomReviews() {
        if (roomGuid == null) {
            reviewList.clear();
            reviewAdapter.notifyDataSetChanged();
            rvReviews.setVisibility(View.GONE);
            tvNoReviews.setVisibility(View.VISIBLE);
            return;
        }

        String url = "api/Review/GetByRoomId/" + roomGuid;

        apiService.getAsync(
                url,
                ReviewReponse[].class,
                new Callback<ReviewReponse[]>() {
                    @Override
                    public void onSuccess(ReviewReponse[] result) {
                        runOnUiThread(() -> {
                            if (result != null && result.length > 0) {
                                reviewList.clear();
                                for (ReviewReponse review : result) {
                                    reviewList.add(review);
                                }

                                reviewAdapter.notifyDataSetChanged();
                                rvReviews.setVisibility(View.VISIBLE);
                                tvNoReviews.setVisibility(View.GONE);
                            } else {
                                reviewList.clear();
                                reviewAdapter.notifyDataSetChanged();
                                rvReviews.setVisibility(View.GONE);
                                tvNoReviews.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        runOnUiThread(() -> {
                            Toast.makeText(RoomDetailActivity.this,
                                    "Failed to load reviews: " + error.getMessage(),
                                    Toast.LENGTH_SHORT).show();

                            reviewList.clear();
                            reviewAdapter.notifyDataSetChanged();
                            rvReviews.setVisibility(View.GONE);
                            tvNoReviews.setVisibility(View.VISIBLE);
                        });
                    }
                }
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}