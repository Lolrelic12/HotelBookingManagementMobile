package com.example.hotelmanagement.dto;

import java.util.Date;

public class ReviewReponse {
    private String reviewId;
    private String roomId;
    private String userId;
    private String userName;
    private double rating;
    private String content;
    private String  createdAt;
    private String  updatedAt;

    // Constructors
    public ReviewReponse() {}

    // Getters and Setters
    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String  getCreatedAt() { return createdAt; }
    public void setCreatedAt(String  createdAt) { this.createdAt = createdAt; }

    public String  getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String  updatedAt) { this.updatedAt = updatedAt; }
}