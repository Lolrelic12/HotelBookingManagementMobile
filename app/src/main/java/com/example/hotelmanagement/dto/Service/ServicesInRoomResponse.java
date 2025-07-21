package com.example.hotelmanagement.dto.Service;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ServicesInRoomResponse {
    @SerializedName("roomId")
    private String roomId;
    @SerializedName("services")
    private ArrayList<Service> services;
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public ArrayList<Service> getServices() {
        return services;
    }

    public void setServices(ArrayList<Service> services) {
        this.services = services;
    }

    public ServicesInRoomResponse(String roomId, ArrayList<Service> services) {
        this.roomId = roomId;
        this.services = services;
    }

    public ServicesInRoomResponse() {
    }
}
