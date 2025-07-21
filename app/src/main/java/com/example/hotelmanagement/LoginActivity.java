package com.example.hotelmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelmanagement.dto.LoginRequest;
import com.example.hotelmanagement.dto.LoginResponse;
import com.example.hotelmanagement.services.api.ApiService;
import com.example.hotelmanagement.services.api.Callback;
import com.example.hotelmanagement.services.api.TokenManager;


public class LoginActivity extends AppCompatActivity {

    private EditText usernameField, passwordField;
    private Button loginButton;
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        resultText = findViewById(R.id.resultText);

        loginButton.setOnClickListener(v -> attemptLogin());

        TokenManager tokenManager = new TokenManager(this);
    }

    private void attemptLogin() {
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            resultText.setText("Please enter both username and password.");
            return;
        }

        LoginRequest loginRequest = new LoginRequest(username, password);

        ApiService.getInstance(this).postAsync(
                "api/auth/login",
                loginRequest,
                LoginResponse.class,
                new Callback<LoginResponse>() {
                    @Override
                    public void onSuccess(LoginResponse result) {
                        TokenManager tm = new TokenManager(LoginActivity.this);
                        tm.saveToken(result.getToken());

                        String savedToken = tm.getToken();

                        Intent intent = new Intent(LoginActivity.this, RoomListActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        runOnUiThread(() ->
                                resultText.setText("Login failed: " + error.getMessage()));
                    }
                }
        );
    }
}