package com.example.hotelmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelmanagement.dto.ChangePasswordRequest;
import com.example.hotelmanagement.dto.ChangePasswordResponse;
import com.example.hotelmanagement.dto.LoginResponse;
import com.example.hotelmanagement.extensions.JwtHelper;
import com.example.hotelmanagement.services.api.ApiService;
import com.example.hotelmanagement.services.api.Callback;
import com.example.hotelmanagement.services.api.TokenManager;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText oldPasswordField, newPasswordField, confirmPasswordField;
    private TextView resultText;
    private TokenManager tokenManager;
    private JwtHelper jwtHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldPasswordField = findViewById(R.id.oldPassword);
        newPasswordField = findViewById(R.id.newPassword);
        confirmPasswordField = findViewById(R.id.confirmPassword);
        resultText = findViewById(R.id.statusMessage);
        Button submitBtn = findViewById(R.id.submitBtn);

        tokenManager = new TokenManager(this);

        if (tokenManager.hasToken()) {
            jwtHelper = new JwtHelper(tokenManager.getToken());
        }

        submitBtn.setOnClickListener(v -> {
            String oldPass = oldPasswordField.getText().toString().trim();
            String newPass = newPasswordField.getText().toString().trim();
            String confirmPass = confirmPasswordField.getText().toString().trim();

            if (!newPass.equals(confirmPass)) {
                resultText.setText("New passwords do not match");
                return;
            }

            String userid = jwtHelper.getUserId();

            ChangePasswordRequest request = new ChangePasswordRequest(userid, oldPass, newPass, confirmPass);

            ApiService.getInstance(this).putAsync(
                    "api/user/UpdatePassword",
                    request,
                    ChangePasswordResponse.class,
                    new Callback<ChangePasswordResponse>() {
                        @Override
                        public void onSuccess(ChangePasswordResponse result) {
                            resultText.setText("Password changed successfully.");
                            resultText.setVisibility(TextView.VISIBLE);
                            Intent intent = new Intent(ChangePasswordActivity.this, RoomListActivity.class);
                            try {
                                TimeUnit.SECONDS.sleep(2);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onFailure(Throwable error) {
                            runOnUiThread(() ->
                                    resultText.setText("Password change failed: " + error.getMessage()));
                            resultText.setVisibility(TextView.VISIBLE);
                        }
                    }
            );
        });
    }


}
