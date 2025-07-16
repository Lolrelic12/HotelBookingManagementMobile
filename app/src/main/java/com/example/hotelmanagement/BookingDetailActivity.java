package com.example.hotelmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.hotelmanagement.dto.BookingRequest;
import com.example.hotelmanagement.dto.BookingResponse;
import com.example.hotelmanagement.dto.RoomResponse;
import com.example.hotelmanagement.services.api.ApiService;
import com.example.hotelmanagement.services.api.Callback;
import com.google.gson.Gson;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class BookingDetailActivity extends AppCompatActivity {
    public static final String EXTRA_ROOM_DATA = "room_data";
    public static final String EXTRA_BOOKING_DATA = "booking_data";
    public static final String EXTRA_CHECK_IN_DATE = "check_in_date";
    public static final String EXTRA_CHECK_OUT_DATE = "check_out_date";
    public static final String EXTRA_TOTAL_PRICE = "total_price";
    public static final String EXTRA_NIGHTS = "nights";

    private RoomResponse roomData;
    private BookingRequest bookingRequest;
    private Gson gson;
    private DecimalFormat decimalFormat;
    private ApiService apiService;

    private TextView tvRoomNumber, tvRoomType, tvRoomPrice, tvCheckInDate,
            tvCheckOutDate, tvTotalNights, tvTotalPrice, tvBookingStatus;
    private Button btnConfirmBooking, btnBackToDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        setupToolbar();
        initViews();
        setupServices();
        loadBookingData();
        setupButtons();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Booking Details");
        }
    }

    private void initViews() {
        tvRoomNumber = findViewById(R.id.tvRoomNumber);
        tvRoomType = findViewById(R.id.tvRoomType);
        tvRoomPrice = findViewById(R.id.tvRoomPrice);
        tvCheckInDate = findViewById(R.id.tvCheckInDate);
        tvCheckOutDate = findViewById(R.id.tvCheckOutDate);
        tvTotalNights = findViewById(R.id.tvTotalNights);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvBookingStatus = findViewById(R.id.tvBookingStatus);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        btnBackToDetail = findViewById(R.id.btnBackToDetail);
    }

    private void setupServices() {
        gson = new Gson();
        decimalFormat = new DecimalFormat("0.00 VND");
        apiService = ApiService.getInstance(this);
    }

    private void loadBookingData() {
        String roomDataJson = getIntent().getStringExtra(EXTRA_ROOM_DATA);
        String bookingDataJson = getIntent().getStringExtra(EXTRA_BOOKING_DATA);

        if (roomDataJson != null && bookingDataJson != null) {
            try {
                roomData = gson.fromJson(roomDataJson, RoomResponse.class);
                bookingRequest = gson.fromJson(bookingDataJson, BookingRequest.class);
                displayBookingDetails();
            } catch (Exception e) {
                Toast.makeText(this, "Error loading booking data", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Booking data not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayBookingDetails() {
        if (roomData != null && bookingRequest != null) {
            tvRoomNumber.setText("Room " + roomData.getRoomNumber());
            tvRoomPrice.setText(decimalFormat.format(roomData.getPrice()) + "/night");

            String checkInDate = getIntent().getStringExtra(EXTRA_CHECK_IN_DATE);
            String checkOutDate = getIntent().getStringExtra(EXTRA_CHECK_OUT_DATE);
            long nights = getIntent().getLongExtra(EXTRA_NIGHTS, 0);
            double totalPrice = getIntent().getDoubleExtra(EXTRA_TOTAL_PRICE, 0.0);

            tvCheckInDate.setText(checkInDate);
            tvCheckOutDate.setText(checkOutDate);
            tvTotalNights.setText(nights + " nights");
            tvTotalPrice.setText(decimalFormat.format(totalPrice));
            tvBookingStatus.setText("Pending");
        }
    }

    private void setupButtons() {
        btnConfirmBooking.setOnClickListener(v -> confirmBooking());
        btnBackToDetail.setOnClickListener(v -> navigateBackToRoomDetail());
    }

    private void confirmBooking() {
        if (bookingRequest == null) {
            Toast.makeText(this, "Booking data not available", Toast.LENGTH_SHORT).show();
            return;
        }

        BookingRequest finalRequest = prepareBookingRequest();
        if (!isValidBookingRequest(finalRequest)) {
            Toast.makeText(this, "Invalid booking data", Toast.LENGTH_SHORT).show();
            return;
        }

        btnConfirmBooking.setEnabled(false);
        btnConfirmBooking.setText("Processing...");

        apiService.postAsync(
                "api/Booking/Create",
                finalRequest,
                BookingResponse.class,
                new Callback<BookingResponse>() {
                    @Override
                    public void onSuccess(BookingResponse result) {
                        runOnUiThread(() -> {
                            Toast.makeText(BookingDetailActivity.this,
                                    "Booking confirmed successfully!", Toast.LENGTH_LONG).show();
                            navigateBackToRoomDetail();
                        });
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        runOnUiThread(() -> {
                            Toast.makeText(BookingDetailActivity.this,
                                    "Failed to confirm booking", Toast.LENGTH_LONG).show();
                            btnConfirmBooking.setEnabled(true);
                            btnConfirmBooking.setText("Confirm Booking");
                        });
                    }
                }
        );
    }

    private BookingRequest prepareBookingRequest() {
        BookingRequest request = new BookingRequest();
        request.setId(UUID.randomUUID().toString());
        request.setUserId("26a2da04-d00f-41cb-8783-17fcf11f099c");

//        String userId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("userId", null);
//        if (userId == null) {
//            Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
//            return null;
//        }
//        request.setUserId(userId);
        request.setRoomId(roomData.getId());

        String checkInDate = getIntent().getStringExtra(EXTRA_CHECK_IN_DATE);
        String checkOutDate = getIntent().getStringExtra(EXTRA_CHECK_OUT_DATE);
        double totalPrice = getIntent().getDoubleExtra(EXTRA_TOTAL_PRICE, 0.0);

        request.setCheckInDate(formatDateForServer(checkInDate));
        request.setCheckOutDate(formatDateForServer(checkOutDate));
        request.setTotalPrice(totalPrice);
        request.setBookingStatus(0);
        request.setCreatedAt(getCurrentTimestamp());
        return request;
    }

    private String formatDateForServer(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (Exception e) {
            return null;
        }
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", Locale.getDefault());
        return sdf.format(new Date());
    }

    private boolean isValidBookingRequest(BookingRequest request) {
        return request != null &&
                request.getId() != null &&
                request.getUserId() != null &&
                request.getRoomId() != null &&
                request.getCheckInDate() != null &&
                request.getCheckOutDate() != null &&
                request.getTotalPrice() > 0;
    }

    private void navigateBackToRoomDetail() {
        Intent intent = new Intent(this, RoomDetailActivity.class);
        intent.putExtra(RoomDetailActivity.EXTRA_ROOM_ID, roomData.getRoomId());
        intent.putExtra(RoomDetailActivity.EXTRA_ROOM_DATA, gson.toJson(roomData));
        intent.putExtra("booking_success", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            navigateBackToRoomDetail();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateBackToRoomDetail();
    }
}
