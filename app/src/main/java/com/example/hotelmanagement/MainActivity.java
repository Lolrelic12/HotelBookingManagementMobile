package com.example.hotelmanagement;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelmanagement.services.api.ApiService;
import com.example.hotelmanagement.services.api.Callback;

public class MainActivity extends AppCompatActivity {
    private EditText urlInput;
    private Button sendRequestBtn;
    private TextView resultOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlInput = findViewById(R.id.urlInput);
        sendRequestBtn = findViewById(R.id.sendRequestBtn);
        resultOutput = findViewById(R.id.resultOutput);

        ApiService api = ApiService.getInstance(this);

        sendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String endpoint = urlInput.getText().toString().trim();

                if (endpoint.isEmpty()) {
                    resultOutput.setText("Please enter an endpoint.");
                    return;
                }

                resultOutput.setText("Loading...");

                api.getAsync(endpoint, Object.class, new Callback<Object>() {
                    @Override
                    public void onSuccess(Object result) {
                        String json = new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(result);
                        runOnUiThread(() -> resultOutput.setText(json));
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        runOnUiThread(() -> resultOutput.setText("Error: " + error.getMessage()));
                        Log.e("API_FAIL", "Request failed", error);
                    }
                });
            }
        });
    }
}
