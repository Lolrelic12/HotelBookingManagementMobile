package com.example.hotelmanagement.dto;

import java.util.UUID;

public class RoomRequest {
    private String roomId;
    private String roomNumber;
    private int capacity;
    private double pricePerNight;
    private String description;
    private boolean isAvailable;
    private UUID roomTypeId;

    public RoomRequest() {}

    public RoomRequest(String roomId, String roomNumber, int capacity, double pricePerNight,
                       String description, boolean isAvailable, UUID roomTypeId) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.pricePerNight = pricePerNight;
        this.description = description;
        this.isAvailable = isAvailable;
        this.roomTypeId = roomTypeId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public UUID getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(UUID roomTypeId) {
        this.roomTypeId = roomTypeId;
    }
}
