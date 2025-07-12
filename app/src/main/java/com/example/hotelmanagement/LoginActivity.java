package com.example.hotelmanagement;

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
    private Button logoutButton;
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

        logoutButton = findViewById(R.id.logoutButton);
        TokenManager tokenManager = new TokenManager(this);

        if (tokenManager.hasToken()) {
            logoutButton.setVisibility(View.VISIBLE);
            resultText.setText("Token already exists:\n" + tokenManager.getToken());
        }

        logoutButton.setOnClickListener(v -> {
            tokenManager.clearToken();
            logoutButton.setVisibility(View.GONE);
            resultText.setText("Logged out.");
        });
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

                        runOnUiThread(() ->
                                resultText.setText("Login successful!\n\nToken:\n" + savedToken)
                        );

                        logoutButton.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        runOnUiThread(() ->
                                resultText.setText("Login failed: " + error.getMessage()));
                    }
                }
        );
    }

    private void attemptLogout() {
        ApiService.getInstance(this).postAsync(
                "api/Auth/Logout",
                new Object(),
                Boolean.class,
                new Callback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        TokenManager tm = new TokenManager(LoginActivity.this);
                        tm.clearToken();

                        runOnUiThread(() -> {
                            logoutButton.setVisibility(View.GONE);
                            resultText.setText("Logged out successfully.");
                        });
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        runOnUiThread(() ->
                                resultText.setText("Logout failed: " + error.getMessage())
                        );
                    }
                }
        );
    }
}