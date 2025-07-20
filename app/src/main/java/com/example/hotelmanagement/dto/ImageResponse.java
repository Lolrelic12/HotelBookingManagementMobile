package com.example.hotelmanagement.dto;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ImageResponse implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("roomId")
    private String roomId;

    @SerializedName("serviceId")
    private String serviceId;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("lastModifiedAt")
    private String lastModifiedAt;

    @SerializedName("createdBy")
    private String createdBy;

    @SerializedName("updatedBy")
    private String updatedBy;

    @SerializedName("lastModifiedBy")
    private String lastModifiedBy;

    public ImageResponse() {}

    public ImageResponse(String id, String imageUrl, String roomId, String serviceId) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.roomId = roomId;
        this.serviceId = serviceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(String lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public boolean isRoomImage() {
        return roomId != null && !roomId.isEmpty();
    }

    public boolean isServiceImage() {
        return serviceId != null && !serviceId.isEmpty();
    }

    @Override
    public String toString() {
        return "ImageResponse{" +
                "id='" + id + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", roomId='" + roomId + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", lastModifiedAt='" + lastModifiedAt + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                ", lastModifiedBy='" + lastModifiedBy + '\'' +
                '}';
    }


}
