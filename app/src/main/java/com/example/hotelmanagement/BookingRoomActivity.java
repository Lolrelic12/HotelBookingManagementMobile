package com.example.hotelmanagement;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.hotelmanagement.dto.BookingRequest;
import com.example.hotelmanagement.dto.RoomResponse;
import com.google.gson.Gson;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BookingRoomActivity extends AppCompatActivity {
    public static final String EXTRA_ROOM_DATA = "room_data";
    public static final String EXTRA_BOOKING_DATA = "booking_data";
    public static final String EXTRA_CHECK_IN_DATE = "check_in_date";
    public static final String EXTRA_CHECK_OUT_DATE = "check_out_date";
    public static final String EXTRA_TOTAL_PRICE = "total_price";
    public static final String EXTRA_ROOM_ID = "room_id";
    public static final String EXTRA_NIGHTS = "nights";

    private RoomResponse roomData;
    private Gson gson;
    private DecimalFormat decimalFormat;
    private SimpleDateFormat dateFormat;
    private Calendar calendar;

    private TextView tvRoomName, tvRoomPrice, tvTotalNights, tvTotalPrice;
    private EditText etCheckInDate, etCheckOutDate;
    private Button btnSubmit, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_room);

        setupToolbar();
        initViews();
        setupServices();
        loadRoomData();
        setupDatePickers();
        setupButtons();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Book Room");
        }
    }

    private void initViews() {
        tvRoomName = findViewById(R.id.tvRoomName);
        tvRoomPrice = findViewById(R.id.tvRoomPrice);
        etCheckInDate = findViewById(R.id.etCheckInDate);
        etCheckOutDate = findViewById(R.id.etCheckOutDate);
        tvTotalNights = findViewById(R.id.tvTotalNights);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void setupServices() {
        gson = new Gson();
        decimalFormat = new DecimalFormat("0.00 VND");
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        calendar = Calendar.getInstance();
    }

    private void loadRoomData() {
        String roomDataJson = getIntent().getStringExtra(EXTRA_ROOM_DATA);
        if (roomDataJson != null) {
            try {
                roomData = gson.fromJson(roomDataJson, RoomResponse.class);
                displayRoomInfo();
            } catch (Exception e) {
                Toast.makeText(this, "Error loading room data", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Room data not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayRoomInfo() {
        if (roomData != null) {
            tvRoomName.setText("Room " + roomData.getRoomNumber());
            tvRoomPrice.setText(decimalFormat.format(roomData.getPrice()) + "/night");
        }
    }

    private void setupDatePickers() {
        etCheckInDate.setOnClickListener(v -> showDatePickerDialog(true));
        etCheckOutDate.setOnClickListener(v -> showDatePickerDialog(false));
    }

    private void showDatePickerDialog(boolean isCheckIn) {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String selectedDate = dateFormat.format(calendar.getTime());

            if (isCheckIn) {
                etCheckInDate.setText(selectedDate);
            } else {
                etCheckOutDate.setText(selectedDate);
            }

            calculateTotalPrice();
        };

        Calendar minDate = Calendar.getInstance();
        if (!isCheckIn && !etCheckInDate.getText().toString().isEmpty()) {
            try {
                Date checkInDate = dateFormat.parse(etCheckInDate.getText().toString());
                minDate.setTime(checkInDate);
                minDate.add(Calendar.DAY_OF_MONTH, 1);
            } catch (ParseException ignored) {
            }
        }

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        dialog.show();
    }

    private void calculateTotalPrice() {
        String checkInStr = etCheckInDate.getText().toString();
        String checkOutStr = etCheckOutDate.getText().toString();

        if (!checkInStr.isEmpty() && !checkOutStr.isEmpty()) {
            try {
                Date checkInDate = dateFormat.parse(checkInStr);
                Date checkOutDate = dateFormat.parse(checkOutStr);

                if (checkOutDate.after(checkInDate)) {
                    long nights = (checkOutDate.getTime() - checkInDate.getTime()) / (1000 * 60 * 60 * 24);
                    double totalPrice = nights * roomData.getPrice();
                    tvTotalNights.setText(nights + " nights");
                    tvTotalPrice.setText(decimalFormat.format(totalPrice));
                    btnSubmit.setEnabled(true);
                } else {
                    resetTotalFields();
                    Toast.makeText(this, "Check-out date must be after check-in date", Toast.LENGTH_SHORT).show();
                }
            } catch (ParseException e) {
                resetTotalFields();
            }
        } else {
            resetTotalFields();
        }
    }

    private void resetTotalFields() {
        tvTotalNights.setText("0 nights");
        tvTotalPrice.setText("0.00 VND");
        btnSubmit.setEnabled(false);
    }

    private void setupButtons() {
        btnSubmit.setOnClickListener(v -> handleSubmit());
        btnCancel.setOnClickListener(v -> finish());
        btnSubmit.setEnabled(false);
    }

    private void handleSubmit() {
        String checkInStr = etCheckInDate.getText().toString();
        String checkOutStr = etCheckOutDate.getText().toString();

        if (checkInStr.isEmpty() || checkOutStr.isEmpty()) {
            Toast.makeText(this, "Please select both check-in and check-out dates", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Date checkInDate = dateFormat.parse(checkInStr);
            Date checkOutDate = dateFormat.parse(checkOutStr);

            if (checkOutDate.after(checkInDate)) {
                long nights = (checkOutDate.getTime() - checkInDate.getTime()) / (1000 * 60 * 60 * 24);
                double totalPrice = nights * roomData.getPrice();

                SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                BookingRequest bookingRequest = new BookingRequest();
                bookingRequest.setRoomId(roomData.getId());
                bookingRequest.setCheckInDate(apiDateFormat.format(checkInDate));
                bookingRequest.setCheckOutDate(apiDateFormat.format(checkOutDate));
                bookingRequest.setTotalPrice(totalPrice);
                bookingRequest.setBookingStatus(1);

                Intent intent = new Intent(this, BookingDetailActivity.class);
                intent.putExtra(EXTRA_ROOM_DATA, gson.toJson(roomData));
                intent.putExtra(EXTRA_BOOKING_DATA, gson.toJson(bookingRequest));
                intent.putExtra(EXTRA_CHECK_IN_DATE, checkInStr);
                intent.putExtra(EXTRA_CHECK_OUT_DATE, checkOutStr);
                intent.putExtra(EXTRA_TOTAL_PRICE, totalPrice);
                intent.putExtra(EXTRA_NIGHTS, nights);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Check-out date must be after check-in date", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
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
}
