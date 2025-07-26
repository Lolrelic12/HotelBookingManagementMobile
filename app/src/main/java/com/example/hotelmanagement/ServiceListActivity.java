package com.example.hotelmanagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hotelmanagement.adapter.ServiceAdapter;
import com.example.hotelmanagement.dto.Service.Service;
import com.example.hotelmanagement.dto.Service.ServiceInRoomWrapper;
import com.example.hotelmanagement.dto.Service.ServiceOrderRequest;
import com.example.hotelmanagement.dto.Service.ServiceOrderResponse;
import com.example.hotelmanagement.dto.Service.ServicesInRoomResponse;
import com.example.hotelmanagement.services.api.ApiService;
import com.example.hotelmanagement.services.api.Callback;

import java.util.ArrayList;
import java.util.List;


public class ServiceListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvTotalPrice;
    private double total = 0;
    private Button btnConfirm;
    ServiceAdapter adapter;
    ServicesInRoomResponse serviceItems = new ServicesInRoomResponse();
    ArrayList<String> serviceIds = new ArrayList<>();
    private ApiService apiService;
    private LinearLayout selectedServiceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_service_list);
        apiService = ApiService.getInstance(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerServices);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnConfirm = findViewById(R.id.btnConfirmOrder);
        btnConfirm.setOnClickListener(v -> {
            if (selectedServiceList.getChildCount() == 0) {
                Toast.makeText(this, "Please select at least one service.", Toast.LENGTH_SHORT).show();
                return;
            }
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Confirm Order")
                    .setMessage("Are you sure you want to order the selected services?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        ServiceOrderRequest serviceOrderRequest = new ServiceOrderRequest();
                        serviceOrderRequest.ServiceIds = serviceIds;
                        orderService(serviceOrderRequest);
                        selectedServiceList.removeAllViews();
                        total = 0;
                        tvTotalPrice.setText("VND 0.00");
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
        selectedServiceList = findViewById(R.id.selectedServiceList);
        loadMockServices();
        adapter = new ServiceAdapter(serviceItems, this::onAddService, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void onAddService(Service item) {
        // Skip if the service is unavailable
        if (!item.isAvailable()) {
            Toast.makeText(this, "This service is unavailable.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prevent duplicate service being added
        for (int i = 0; i < selectedServiceList.getChildCount(); i++) {
            TextView existingName = selectedServiceList.getChildAt(i).findViewById(R.id.tvSelectedServiceName);
            if (serviceIds.contains(item.getId())) {
                Toast.makeText(this, "Service already added.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Inflate service row
        View view = LayoutInflater.from(this)
                .inflate(R.layout.item_selected_service, selectedServiceList, false);

        TextView tvName = view.findViewById(R.id.tvSelectedServiceName);
        ImageView img = view.findViewById(R.id.imgSelectedService);
        Button btnRemove = view.findViewById(R.id.btnRemove);

        tvName.setText(item.getName() + " - VND " + item.getPrice());

        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.ic_placeholder)
                    .into(img);
        } else {
            img.setImageResource(R.drawable.ic_placeholder);
        }

        // Handle remove
        btnRemove.setOnClickListener(v -> {
            selectedServiceList.removeView(view);
            total -= item.getPrice();
            if (total < 0) total = 0;
            tvTotalPrice.setText(String.format("VND %.2f", total));
            serviceIds.remove((String) item.getId());
        });

        selectedServiceList.addView(view);
        total += item.getPrice();
        tvTotalPrice.setText(String.format("VND %.2f", total));
        serviceIds.add(item.getId());
    }


    private void orderService(ServiceOrderRequest request){
        String bookingId = getIntent().getStringExtra("bookingId");
        request.RoomBookingId = bookingId;
        apiService.postAsync("api/ServiceBooking/BookService",request, ServiceOrderResponse.class, new Callback<ServiceOrderResponse>() {
            @Override
            public void onSuccess(ServiceOrderResponse result) {
                runOnUiThread(() -> {
                    if (result != null) {
                        Toast.makeText(ServiceListActivity.this, "Order confirmed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Throwable error) {
                runOnUiThread(() ->
                        Toast.makeText(ServiceListActivity.this, "Failed to order service" + error.toString(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
    private void loadMockServices() {
        String roomId = getIntent().getStringExtra("roomId");
        String bookingId = getIntent().getStringExtra("bookingId");
        apiService.getAsync("api/Service/GetServicesByRoomId?roomId=" + roomId, ServiceInRoomWrapper.class, new Callback<ServiceInRoomWrapper>() {
            @Override
            public void onSuccess(ServiceInRoomWrapper result) {
                runOnUiThread(() -> {
                    if (result != null) {
                        serviceItems = result.getValue();
                        serviceItems = result.getValue();
                        adapter.updateData(serviceItems);
                    }
                });
            }

            @Override
            public void onFailure(Throwable error) {
                runOnUiThread(() ->
                        Toast.makeText(ServiceListActivity.this, "Failed to load services", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}
