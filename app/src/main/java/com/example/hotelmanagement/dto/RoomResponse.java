package com.example.hotelmanagement.dto;

import com.google.gson.annotations.SerializedName;

public class RoomResponse {
    @SerializedName("id")
    private String id;

    @SerializedName("roomId")
    private String roomId;

    @SerializedName("roomNumber")
    private String roomNumber;

    @SerializedName("roomTypeId")
    private String roomTypeId;

    @SerializedName("description")
    private String description;

    @SerializedName("capacity")
    private int capacity;

    @SerializedName("price")
    private double price;

    @SerializedName("isAvailable")
    private boolean isAvailable;

    @SerializedName("averageRating")
    private double averageRating;

    @SerializedName("totalReviews")
    private int totalReviews;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    // Constructors
    public RoomResponse() {}

    public RoomResponse(String id, String roomId, String roomNumber, String roomTypeId,
                        String description, int capacity, double price, boolean isAvailable,
                        double averageRating, int totalReviews, String createdAt, String updatedAt) {
        this.id = id;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.roomTypeId = roomTypeId;
        this.description = description;
        this.capacity = capacity;
        this.price = price;
        this.isAvailable = isAvailable;
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getRoomTypeId() { return roomTypeId; }
    public void setRoomTypeId(String roomTypeId) { this.roomTypeId = roomTypeId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public int getTotalReviews() { return totalReviews; }
    public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "RoomResponse{" +
                "id='" + id + '\'' +
                ", roomId='" + roomId + '\'' +
                ", roomNumber='" + roomNumber + '\'' +
                ", roomTypeId='" + roomTypeId + '\'' +
                ", description='" + description + '\'' +
                ", capacity=" + capacity +
                ", price=" + price +
                ", isAvailable=" + isAvailable +
                ", averageRating=" + averageRating +
                ", totalReviews=" + totalReviews +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}