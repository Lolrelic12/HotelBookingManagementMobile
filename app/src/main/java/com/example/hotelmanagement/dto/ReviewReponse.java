package com.example.hotelmanagement.dto;

import com.google.gson.annotations.SerializedName;

public class ReviewReponse {
    @SerializedName("id")
    private String reviewId;

    @SerializedName("roomId")
    private String roomId;

    @SerializedName("userId")
    private String userId;

    @SerializedName("userName")
    private String userName;

    private double rating;
    private String content;
    private String createdAt;

    public ReviewReponse() {}

    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}