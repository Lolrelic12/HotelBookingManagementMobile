package com.example.hotelmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hotelmanagement.adapter.RoomAdapter;
import com.example.hotelmanagement.dto.ImageResponse;
import com.example.hotelmanagement.dto.RoomResponse;
import com.example.hotelmanagement.dto.RoomTypeResponse;
import com.example.hotelmanagement.services.api.ApiService;
import com.example.hotelmanagement.services.api.Callback;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;

public class RoomListActivity extends BaseActivity {
    private static final int ITEMS_PER_PAGE = 10;
    private RecyclerView recyclerView;
    private RoomAdapter roomAdapter;
    private List<RoomResponse> originalRoomList = new ArrayList<>();
    private List<RoomResponse> filteredRoomList = new ArrayList<>();
    private List<RoomResponse> currentPageRoomList = new ArrayList<>();
    private List<ImageResponse> allImages = new ArrayList<>();

    private EditText etSearchRoomNumber;
    private Spinner spinnerAvailabilityFilter, spinnerSortBy;
    private Button btnPrevPage, btnNextPage;
    private TextView tvPageInfo;

    private int currentPage = 1;
    private int totalPages = 1;
    private String currentSearchQuery = "";
    private String currentAvailabilityFilter = "all";
    private String currentSortBy = "room_number";

    private ApiService apiService;
    private RoomTypeActivity roomTypeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);
        setupFooterNavigation();
        highlightFooterIcon(R.id.iconRoom);
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

    private void setupApi() {
        apiService = ApiService.getInstance(this);
        roomTypeService = new RoomTypeActivity(this);
    }

    private void setupRecyclerView() {
        roomAdapter = new RoomAdapter(currentPageRoomList, roomTypeService);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(roomAdapter);
        loadRoomTypes();
    }

    private void setupSearchAndFilters() {
        etSearchRoomNumber.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().trim();
                applyFiltersAndSort();
            }
        });

        ArrayAdapter<CharSequence> availabilityAdapter = ArrayAdapter.createFromResource(
                this, R.array.availability_filter_options, android.R.layout.simple_spinner_item);
        availabilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAvailabilityFilter.setAdapter(availabilityAdapter);

        spinnerAvailabilityFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onNothingSelected(AdapterView<?> parent) {}

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] options = {"all", "available", "unavailable"};
                currentAvailabilityFilter = options[position];
                applyFiltersAndSort();
            }
        });

        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(
                this, R.array.sort_options, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortBy.setAdapter(sortAdapter);

        spinnerSortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onNothingSelected(AdapterView<?> parent) {}

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] sortOptions = {"room_number", "price", "rating"};
                currentSortBy = sortOptions[position];
                applyFiltersAndSort();
            }
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

    private void loadRoomTypes() {
        roomTypeService.getAllRoomTypes(new Callback<RoomTypeResponse[]>() {
            @Override
            public void onSuccess(RoomTypeResponse[] result) {
                runOnUiThread(RoomListActivity.this::loadRooms);
            }

            @Override
            public void onFailure(Throwable error) {
                runOnUiThread(() -> {
                    Toast.makeText(RoomListActivity.this, "Failed to load room types", Toast.LENGTH_SHORT).show();
                    loadRooms();
                });
            }
        });
    }

    private void loadRooms() {
        apiService.getAsync("api/Room/GetAll", RoomResponse[].class, new Callback<RoomResponse[]>() {
            @Override
            public void onSuccess(RoomResponse[] result) {
                runOnUiThread(() -> {
                    originalRoomList.clear();
                    if (result != null) {
                        Collections.addAll(originalRoomList, result);
                    }
                    applyFiltersAndSort();
                    loadImages();
                });
            }

            @Override
            public void onFailure(Throwable error) {
                runOnUiThread(() ->
                        Toast.makeText(RoomListActivity.this, "Failed to load rooms", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void loadImages() {
        apiService.getAsync("api/Image/GetAll", JsonElement.class, new Callback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement result) {
                runOnUiThread(() -> {
                    allImages.clear();
                    Gson gson = new Gson();

                    try {
                        JsonElement arrayElement = result;

                        if (result.isJsonObject()) {
                            JsonObject obj = result.getAsJsonObject();
                            for (String key : Arrays.asList("data", "images", "result", "items")) {
                                if (obj.has(key)) {
                                    arrayElement = obj.get(key);
                                    break;
                                }
                            }
                        }

                        if (arrayElement != null && arrayElement.isJsonArray()) {
                            ImageResponse[] images = gson.fromJson(arrayElement, ImageResponse[].class);
                            Collections.addAll(allImages, images);
                        }

                        roomAdapter.setImages(allImages);
                    } catch (Exception e) {
                        roomAdapter.setImages(new ArrayList<>());
                    }
                });
            }

            @Override
            public void onFailure(Throwable error) {
                runOnUiThread(() -> {
                    Toast.makeText(RoomListActivity.this, "Failed to load images", Toast.LENGTH_SHORT).show();
                    roomAdapter.setImages(new ArrayList<>());
                });
            }
        });
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
        Comparator<RoomResponse> comparator = null;
        switch (currentSortBy) {
            case "room_number":
                comparator = Comparator.comparing(RoomResponse::getRoomNumber);
                break;
            case "price":
                comparator = Comparator.comparingDouble(RoomResponse::getPrice);
                break;
            case "rating":
                comparator = Comparator.comparingDouble(RoomResponse::getAverageRating).reversed();
                break;
        }
        if (comparator != null) Collections.sort(filteredRoomList, comparator);
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

        currentPageRoomList.addAll(filteredRoomList.subList(startIndex, endIndex));
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
