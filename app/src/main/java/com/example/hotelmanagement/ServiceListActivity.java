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
import com.example.hotelmanagement.dto.RoomResponse;
import com.example.hotelmanagement.dto.Service.ServiceResponse;
import com.example.hotelmanagement.services.api.ApiService;
import com.example.hotelmanagement.services.api.Callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvTotalPrice;
    private double total = 0;
    private Button btnConfirm;

    List<ServiceResponse> serviceItems;

    private ApiService apiService = ApiService.getInstance(this);
    private LinearLayout selectedServiceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_service_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerServices);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnConfirm = findViewById(R.id.btnConfirmOrder);
        selectedServiceList = findViewById(R.id.selectedServiceList);
        loadMockServices();
        ServiceAdapter adapter = new ServiceAdapter(serviceItems, this::onAddService, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void onAddService(ServiceResponse item) {
        // Skip if the service is unavailable
        if (!item.isAvailable()) {
            Toast.makeText(this, "This service is unavailable.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prevent duplicate service being added
        for (int i = 0; i < selectedServiceList.getChildCount(); i++) {
            TextView existingName = selectedServiceList.getChildAt(i).findViewById(R.id.tvSelectedServiceName);
            if (existingName != null && existingName.getText().toString().startsWith(item.name)) {
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

        tvName.setText(item.name + " - VND " + item.price);

        if (item.imageUrl != null && !item.imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(img);
        } else {
            img.setImageResource(R.drawable.ic_placeholder);
        }

        // Handle remove
        btnRemove.setOnClickListener(v -> {
            selectedServiceList.removeView(view);
            total -= item.price;
            if (total < 0) total = 0;
            tvTotalPrice.setText(String.format("VND %.2f", total));
        });

        selectedServiceList.addView(view);
        total += item.price;
        tvTotalPrice.setText(String.format("VND %.2f", total));
    }


    private void loadMockServices() {
//        List<ServiceResponse> list = new ArrayList<>();
//        list.add(new ServiceResponse("Laundry", "Fast 1-day laundry", "", 10000, true));
//        list.add(new ServiceResponse("Breakfast", "Continental meal", "", 15000, true));
//        list.add(new ServiceResponse("Massage", "Relaxing massage", "", 25000, false));
//        return list;
//        apiService.getAsync("api/Room/GetAll", ServiceResponse[].class, new Callback<ServiceResponse[]>() {
//            @Override
//            public void onSuccess(ServiceResponse[] result) {
//                runOnUiThread(() -> {
//                    serviceItems.clear();
//                    if (result != null) {
//                        Collections.addAll(serviceItems, result);
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(Throwable error) {
//                runOnUiThread(() ->
//                        Toast.makeText(ServiceListActivity.this, "Failed to load services", Toast.LENGTH_SHORT).show()
//                );
//            }
//        });
    }
}
