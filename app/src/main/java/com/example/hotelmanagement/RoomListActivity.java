package com.example.hotelmanagement;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hotelmanagement.adapter.RoomAdapter;
import com.example.hotelmanagement.dto.RoomResponse;
import com.example.hotelmanagement.dto.RoomTypeResponse;
import com.example.hotelmanagement.services.api.ApiService;
import com.example.hotelmanagement.services.api.Callback;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RoomListActivity extends AppCompatActivity {
    private static final int ITEMS_PER_PAGE = 10;

    private RecyclerView recyclerView;
    private RoomAdapter roomAdapter;
    private List<RoomResponse> originalRoomList;
    private List<RoomResponse> filteredRoomList;
    private List<RoomResponse> currentPageRoomList;
    private ApiService apiService;

    private EditText etSearchRoomNumber;
    private Spinner spinnerAvailabilityFilter;
    private Spinner spinnerSortBy;

    private Button btnPrevPage;
    private Button btnNextPage;
    private TextView tvPageInfo;

    private int currentPage = 1;
    private int totalPages = 1;

    private String currentSearchQuery = "";
    private String currentAvailabilityFilter = "all";
    private String currentSortBy = "room_number";
    private RoomTypeActivity roomTypeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        initViews();
        setupApi();
        setupRecyclerView();
        setupSearchAndFilters();
        setupPagination();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewRooms);
        etSearchRoomNumber = findViewById(R.id.etSearchRoomNumber);
        spinnerAvailabilityFilter = findViewById(R.id.spinnerAvailabilityFilter);
        spinnerSortBy = findViewById(R.id.spinnerSortBy);
        btnPrevPage = findViewById(R.id.btnPrevPage);
        btnNextPage = findViewById(R.id.btnNextPage);
        tvPageInfo = findViewById(R.id.tvPageInfo);
    }

    private void setupRecyclerView() {
        originalRoomList = new ArrayList<>();
        filteredRoomList = new ArrayList<>();
        currentPageRoomList = new ArrayList<>();

        roomAdapter = new RoomAdapter(currentPageRoomList, roomTypeService);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(roomAdapter);

        loadRoomTypes();
    }

    private void setupSearchAndFilters() {
        etSearchRoomNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().trim();
                applyFiltersAndSort();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        ArrayAdapter<CharSequence> availabilityAdapter = ArrayAdapter.createFromResource(
                this, R.array.availability_filter_options, android.R.layout.simple_spinner_item);
        availabilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAvailabilityFilter.setAdapter(availabilityAdapter);

        spinnerAvailabilityFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] filterOptions = {"all", "available", "unavailable"};
                currentAvailabilityFilter = filterOptions[position];
                applyFiltersAndSort();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(
                this, R.array.sort_options, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortBy.setAdapter(sortAdapter);

        spinnerSortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] sortOptions = {"room_number", "price", "rating"};
                currentSortBy = sortOptions[position];
                applyFiltersAndSort();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupPagination() {
        btnPrevPage.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                updateCurrentPage();
            }
        });

        btnNextPage.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                currentPage++;
                updateCurrentPage();
            }
        });
    }

    private void setupApi() {
        apiService = ApiService.getInstance(this);
        roomTypeService = new RoomTypeActivity(this);
    }

    private void loadRoomTypes() {
        roomTypeService.getAllRoomTypes(new Callback<RoomTypeResponse[]>() {
            @Override
            public void onSuccess(RoomTypeResponse[] result) {
                runOnUiThread(() -> {
                    loadRooms();
                });
            }

            @Override
            public void onFailure(Throwable error) {
                runOnUiThread(() -> {
                    Toast.makeText(RoomListActivity.this,
                            "Failed to load room types: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    loadRooms();
                });
            }
        });
    }

    private void loadRooms() {
        apiService.getAsync(
                "api/Room/GetAll",
                RoomResponse[].class,
                new Callback<RoomResponse[]>() {
                    @Override
                    public void onSuccess(RoomResponse[] result) {
                        runOnUiThread(() -> {
                            try {
                                originalRoomList.clear();
                                if (result != null) {
                                    for (RoomResponse room : result) {
                                        originalRoomList.add(room);
                                    }
                                }
                                applyFiltersAndSort();
                            } catch (Exception e) {
                                Toast.makeText(RoomListActivity.this,
                                        "Error processing room data: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        runOnUiThread(() -> {
                            Toast.makeText(RoomListActivity.this,
                                    "Failed to load rooms: " + error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        });
                    }
                }
        );
    }

    private void applyFiltersAndSort() {
        filteredRoomList.clear();

        for (RoomResponse room : originalRoomList) {
            boolean matchesSearch = currentSearchQuery.isEmpty() ||
                    room.getRoomNumber().toLowerCase().contains(currentSearchQuery.toLowerCase());

            boolean matchesAvailability = currentAvailabilityFilter.equals("all") ||
                    (currentAvailabilityFilter.equals("available") && room.isAvailable()) ||
                    (currentAvailabilityFilter.equals("unavailable") && !room.isAvailable());

            if (matchesSearch && matchesAvailability) {
                filteredRoomList.add(room);
            }
        }

        sortRooms();

        currentPage = 1;
        updatePagination();
        updateCurrentPage();
    }

    private void sortRooms() {
        switch (currentSortBy) {
            case "room_number":
                Collections.sort(filteredRoomList, new Comparator<RoomResponse>() {
                    @Override
                    public int compare(RoomResponse r1, RoomResponse r2) {
                        return r1.getRoomNumber().compareTo(r2.getRoomNumber());
                    }
                });
                break;
            case "price":
                Collections.sort(filteredRoomList, new Comparator<RoomResponse>() {
                    @Override
                    public int compare(RoomResponse r1, RoomResponse r2) {
                        return Double.compare(r1.getPrice(), r2.getPrice());
                    }
                });
                break;
            case "rating":
                Collections.sort(filteredRoomList, new Comparator<RoomResponse>() {
                    @Override
                    public int compare(RoomResponse r1, RoomResponse r2) {
                        return Double.compare(r2.getAverageRating(), r1.getAverageRating());
                    }
                });
                break;
        }
    }

    private void updatePagination() {
        totalPages = (int) Math.ceil((double) filteredRoomList.size() / ITEMS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;

        updatePaginationButtons();
    }

    private void updateCurrentPage() {
        currentPageRoomList.clear();

        int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filteredRoomList.size());

        for (int i = startIndex; i < endIndex; i++) {
            currentPageRoomList.add(filteredRoomList.get(i));
        }

        roomAdapter.notifyDataSetChanged();
        updatePaginationButtons();
    }

    private void updatePaginationButtons() {
        btnPrevPage.setEnabled(currentPage > 1);
        btnNextPage.setEnabled(currentPage < totalPages);

        tvPageInfo.setText(String.format("Page %d of %d (%d total rooms)", currentPage, totalPages, filteredRoomList.size()));
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadRooms();
    }
}