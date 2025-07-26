package com.example.hotelmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected LinearLayout navRoom;
    protected LinearLayout navUser;
    protected LinearLayout navService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupFooterNavigation() {
        navRoom = findViewById(R.id.navRoom);
        navUser = findViewById(R.id.navUser);
        navService = findViewById(R.id.navService);

        if (navRoom != null) {
            navRoom.setOnClickListener(v -> {

                if (!(this instanceof RoomListActivity)) {
                    startActivity(new Intent(this, RoomListActivity.class));
                    finish();
                }
            });
        }

        if (navUser != null) {
            navUser.setOnClickListener(v -> {
                if (!(this instanceof UserProfileActivity)) {
                    startActivity(new Intent(this, UserProfileActivity.class));
                    finish();
                }
            });
        }
        if (navService != null) {
            navService.setOnClickListener(v -> {
                if (!(this instanceof UserOrdersActivity)) {
                    startActivity(new Intent(this, UserOrdersActivity.class));
                    finish();
                }
            });
        }
    }

    protected void highlightFooterIcon(int iconResId) {
        if (iconResId == R.id.iconRoom) {
            navRoom.setBackgroundColor(getResources().getColor(R.color.gray_dark));
        } else if (iconResId == R.id.iconUser) {
            navUser.setBackgroundColor(getResources().getColor(R.color.gray_dark));
        }
        else if (iconResId == R.id.iconService) {
            navService.setBackgroundColor(getResources().getColor(R.color.gray_dark));
        }
    }
}
