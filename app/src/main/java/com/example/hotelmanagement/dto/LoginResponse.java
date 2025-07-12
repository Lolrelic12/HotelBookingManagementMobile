package com.example.hotelmanagement.dto;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("accessToken")
    private String token;

    public String getToken() {
        return token;
    }
}
