package com.example.hotelmanagement.dto;

import java.util.UUID;

public class ReviewRequest {
    private UUID roomId;
    private String content;
    private double rating;

    public ReviewRequest(UUID roomId, String content, double rating) {
        this.roomId = roomId;
        this.content = content;
        this.rating = rating;
    }

    public UUID getRoomId() {
        return roomId;
    }

    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
