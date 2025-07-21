package com.example.hotelmanagement.dto.Service;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ServiceOrderRequest {
    @SerializedName("RoomBookingId")
    public String RoomBookingId;
    @SerializedName("ServiceIds")
    public ArrayList<String> ServiceIds;

    public ServiceOrderRequest() {
    }
}
