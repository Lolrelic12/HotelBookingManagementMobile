package com.example.hotelmanagement;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class ServiceDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        ImageView img = findViewById(R.id.imgService);
        TextView name = findViewById(R.id.tvServiceName);
        TextView desc = findViewById(R.id.tvServiceDescription);
        TextView price = findViewById(R.id.tvServicePrice);
        Button closeBtn = findViewById(R.id.btnClose);
        TextView availability = findViewById(R.id.serviceTvAvailability);
        // Get data from intent
        Intent intent = getIntent();
        String serviceName = intent.getStringExtra("name");
        String serviceDesc = intent.getStringExtra("description");
        double servicePrice = intent.getDoubleExtra("price", 0.0);
        String imageUrl = intent.getStringExtra("image");
        boolean available = intent.getBooleanExtra("available",true);
        if (available) {
            availability.setText("Available");
            availability.setTextColor(Color.parseColor("#4CAF50")); // Green
        } else {
            availability.setText("Unavailable");
            availability.setTextColor(Color.parseColor("#F44336")); // Red
        }
        name.setText(serviceName);
        desc.setText(serviceDesc);
        price.setText(String.format("Price: VND %.0f", servicePrice));

        // Load image (if using Glide)
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(img);

        closeBtn.setOnClickListener(v -> finish());
    }
}
