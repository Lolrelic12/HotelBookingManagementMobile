package com.example.hotelmanagement.dto;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ImageRequest implements Serializable {

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("roomId")
    private String roomId;

    @SerializedName("serviceId")
    private String serviceId;

    // Default constructor
    public ImageRequest() {
    }

    // Constructor for creating image request
    public ImageRequest(String imageUrl, String roomId, String serviceId) {
        this.imageUrl = imageUrl;
        this.roomId = roomId;
        this.serviceId = serviceId;
    }

    // Constructor for room image
    public ImageRequest(String imageUrl, String roomId) {
        this.imageUrl = imageUrl;
        this.roomId = roomId;
        this.serviceId = null;
    }

    // Constructor for service image
    public static ImageRequest forService(String imageUrl, String serviceId) {
        return new ImageRequest(imageUrl, null, serviceId);
    }

    // Constructor for room image
    public static ImageRequest forRoom(String imageUrl, String roomId) {
        return new ImageRequest(imageUrl, roomId, null);
    }

    // Getters and Setters
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    // Helper methods
    public boolean isRoomImage() {
        return roomId != null && !roomId.isEmpty();
    }

    public boolean isServiceImage() {
        return serviceId != null && !serviceId.isEmpty();
    }

    public boolean isValid() {
        return imageUrl != null && !imageUrl.isEmpty() &&
                (isRoomImage() || isServiceImage());
    }

    @Override
    public String toString() {
        return "ImageRequest{" +
                "imageUrl='" + imageUrl + '\'' +
                ", roomId='" + roomId + '\'' +
                ", serviceId='" + serviceId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageRequest that = (ImageRequest) o;

        if (imageUrl != null ? !imageUrl.equals(that.imageUrl) : that.imageUrl != null)
            return false;
        if (roomId != null ? !roomId.equals(that.roomId) : that.roomId != null)
            return false;
        return serviceId != null ? serviceId.equals(that.serviceId) : that.serviceId == null;
    }

    @Override
    public int hashCode() {
        int result = imageUrl != null ? imageUrl.hashCode() : 0;
        result = 31 * result + (roomId != null ? roomId.hashCode() : 0);
        result = 31 * result + (serviceId != null ? serviceId.hashCode() : 0);
        return result;
    }
}