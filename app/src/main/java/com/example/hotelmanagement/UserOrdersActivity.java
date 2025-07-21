package com.example.hotelmanagement;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hotelmanagement.adapter.ServiceOrderedAdapter;
import com.example.hotelmanagement.dto.Service.ServiceOrderedResponse;
import com.example.hotelmanagement.dto.Service.ServiceOrderedWrapper;
import com.example.hotelmanagement.extensions.JwtHelper;
import com.example.hotelmanagement.services.api.ApiService;
import com.example.hotelmanagement.services.api.Callback;
import com.example.hotelmanagement.services.api.TokenManager;

import java.util.ArrayList;
import java.util.List;

public class UserOrdersActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ServiceOrderedAdapter adapter;
    private ApiService apiService;
    private ArrayList<ServiceOrderedResponse> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_orders);
        setupFooterNavigation();
        highlightFooterIcon(R.id.iconService);
        recyclerView = findViewById(R.id.recyclerOrderedServices);
        apiService = ApiService.getInstance(this);
        getOrders();
        adapter = new ServiceOrderedAdapter(list);
        recyclerView.setAdapter(adapter);
    }

    private void getOrders() {
        TokenManager tokenManager = new TokenManager(this);
        JwtHelper jwtHelper = new JwtHelper(tokenManager.getToken());
        String userid = jwtHelper.getUserId();

        apiService.getAsync("api/ServiceBooking/GetAllOrdersByUserId?userId=" + userid, ServiceOrderedWrapper.class, new Callback<ServiceOrderedWrapper>() {
            @Override
            public void onSuccess(ServiceOrderedWrapper result) {
                runOnUiThread(() -> {
                    if (result != null && result.getValue() != null) {
                        list.clear(); // Clear old data
                        list.addAll(result.getValue()); // Add new data
                        adapter.notifyDataSetChanged(); // Notify adapter to refresh view
                    }
                });
            }

            @Override
            public void onFailure(Throwable error) {
                runOnUiThread(() ->
                        Toast.makeText(UserOrdersActivity.this, "No orders to be found. " + error.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}
