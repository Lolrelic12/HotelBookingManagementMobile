package com.example.hotelmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelmanagement.dto.LoginRequest;
import com.example.hotelmanagement.dto.LoginResponse;
import com.example.hotelmanagement.dto.RegisterRequest;
import com.example.hotelmanagement.dto.RegisterResponse;
import com.example.hotelmanagement.services.api.ApiService;
import com.example.hotelmanagement.services.api.Callback;
import com.example.hotelmanagement.services.api.TokenManager;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameField, emailField, passwordField, confirmPasswordField;
    private CheckBox termsCheckBox;
    private Button registerButton;
    private TextView resultText;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameField = findViewById(R.id.usernameField);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        confirmPasswordField = findViewById(R.id.confirmPasswordField);
        registerButton = findViewById(R.id.registerButton);
        resultText = findViewById(R.id.resultText);
        termsCheckBox = findViewById(R.id.termsCheckbox);

        resultText.setVisibility(View.GONE);

        tokenManager = new TokenManager(this);

        if (tokenManager.hasToken()) {
            tokenManager.clearToken();
        }

        registerButton.setOnClickListener(v -> attemptRegistration());
    }

    private void attemptRegistration() {
        String username = usernameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            resultText.setText("Please enter username, email and password.");
            resultText.setVisibility(View.VISIBLE);
            return;
        }

        if (!confirmPassword.equals(password)) {
            resultText.setText("Passwords do not match.");
            resultText.setVisibility(View.VISIBLE);
            return;
        }

        if (!termsCheckBox.isChecked()) {
            resultText.setText("Please agree to the terms and conditions.");
            resultText.setVisibility(View.VISIBLE);
            return;
        }

        RegisterRequest registerRequest = new RegisterRequest(username, email, password);

        ApiService.getInstance(this).postAsync(
                "api/auth/register",
                registerRequest,
                RegisterResponse.class,
                new Callback<RegisterResponse>() {
                    @Override
                    public void onSuccess(RegisterResponse result) {
                        attemptLogin(username, password);
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        runOnUiThread(() ->
                                resultText.setText("Registration failed: " + error.getMessage()));
                                resultText.setVisibility(View.VISIBLE);
                    }
                }
        );
    }

    private void attemptLogin(String username, String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);

        ApiService.getInstance(this).postAsync(
                "api/auth/login",
                loginRequest,
                LoginResponse.class,
                new Callback<LoginResponse>() {
                    @Override
                    public void onSuccess(LoginResponse result) {
                        tokenManager.saveToken(result.getToken());

                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        runOnUiThread(() ->
                                resultText.setText("Registration failed: " + error.getMessage()));
                                resultText.setVisibility(View.VISIBLE);
                    }
                }
        );
    }

}