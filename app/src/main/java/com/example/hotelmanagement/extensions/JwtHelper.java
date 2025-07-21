package com.example.hotelmanagement.extensions;

import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class JwtHelper {
    private String rawToken = "";
    private JSONObject payload;

    public JwtHelper() {

    }

    public JwtHelper(String token) {
        this.rawToken = token;
        parsePayload();
    }

    public void parsePayload() {
        try {
            String[] parts = rawToken.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid JWT format");
            }

            String payloadJson = new String(
                    Base64.decode(parts[1], Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP),
                    StandardCharsets.UTF_8
            );

            payload = new JSONObject(payloadJson);
        } catch (Exception e) {
            Log.e("JwtHelper", "Failed to parse JWT", e);
            payload = new JSONObject();
        }
    }

    public String getClaim(String key) {
        return payload.optString(key, null);
    }

    public String getUserId() {
        return payload.optString("sub", null);
    }

    public String getUsername() {
        return payload.optString("name", null);
    }

    public String getRole() {
        return payload.optString("role", null);
    }

    public boolean isExpired() {
        long exp = payload.optLong("exp", 0);
        long now = System.currentTimeMillis() / 1000;
        return exp > 0 && exp < now;
    }

    public JSONObject getPayloadJson() {
        return payload;
    }
}
