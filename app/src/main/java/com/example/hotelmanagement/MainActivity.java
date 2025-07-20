package com.example.hotelmanagement;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelmanagement.extensions.JwtHelper;
import com.example.hotelmanagement.services.api.ApiService;
import com.example.hotelmanagement.services.api.Callback;
import com.example.hotelmanagement.services.api.TokenManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity {

    private TokenManager tokenManager;
    private JwtHelper jwtHelper;

    private DrawerLayout drawerLayout;
    private LinearLayout guestLayout;
    private LinearLayout authLayout;
    private TextView welcomeText;
    private Button loginButton;
    private Button registerButton;
    private Button viewRoomsButton;
    private Button profileButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tokenManager = new TokenManager(this);

        drawerLayout = findViewById(R.id.drawerLayout);
        guestLayout = findViewById(R.id.guestLayout);
        authLayout = findViewById(R.id.authLayout);
        welcomeText = findViewById(R.id.welcomeText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        viewRoomsButton = findViewById(R.id.viewRoomsButton);
        profileButton = findViewById(R.id.profileButton);
        logoutButton = findViewById(R.id.logoutButton);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        updateUIBasedOnAuth();

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

//        registerButton.setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
//            startActivity(intent);
//        });
//
        viewRoomsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RoomListActivity.class);
            startActivity(intent);
        });
//
//        profileButton.setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
//            startActivity(intent);
//        });

        logoutButton.setOnClickListener(v -> {
            ApiService.getInstance(this).postAsync("api/Auth/Logout", new Object(), Void.class, new Callback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    tokenManager.clearToken();
                    updateUIBasedOnAuth();
                }

                @Override
                public void onFailure(Throwable error) {
                    // You can display error message here
                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIBasedOnAuth();
    }

    private void updateUIBasedOnAuth() {
        boolean isLoggedIn = tokenManager.hasToken();

        guestLayout.setVisibility(isLoggedIn ? View.GONE : View.VISIBLE);
        authLayout.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
        drawerLayout.setDrawerLockMode(isLoggedIn ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        if (isLoggedIn) {
            jwtHelper = new JwtHelper(tokenManager.getToken());
            String username = jwtHelper.getUsername();
            welcomeText.setText("Welcome, " + username + "!");
        }
    }
}
