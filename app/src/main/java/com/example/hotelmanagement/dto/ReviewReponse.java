package com.example.hotelmanagement.dto;

import com.google.gson.annotations.SerializedName;

public class ReviewReponse {
    @SerializedName("id")
    private String reviewId;

    @SerializedName("roomId")
    private String roomId;

    @SerializedName("userId")
    private String userId;

    @SerializedName("userEmail")
    private String userEmail;

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
        return convertEmailToUsername(userEmail);
    }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    private String convertEmailToUsername(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Anonymous";
        }

        try {
            String username = email.split("@")[0];

            if (username.contains(".")) {
                String[] parts = username.split("\\.");
                StringBuilder result = new StringBuilder();
                for (int i = 0; i < parts.length; i++) {
                    if (i > 0) result.append(" ");
                    String part = parts[i].replaceAll("[0-9_-]", "");
                    if (!part.isEmpty()) {
                        result.append(part.substring(0, 1).toUpperCase())
                                .append(part.substring(1).toLowerCase());
                    }
                }
                return result.toString().trim().isEmpty() ? "Anonymous" : result.toString();
            } else {
                username = username.replaceAll("[0-9_-]", "");
                if (username.length() > 0) {
                    return username.substring(0, 1).toUpperCase() +
                            username.substring(1).toLowerCase();
                }
            }
            return "Anonymous";

        } catch (Exception e) {
            return "Anonymous";
        }
    }
}