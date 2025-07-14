package com.example.hotelmanagement.dto;

import com.google.gson.annotations.SerializedName;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class BookingRequest {
    @SerializedName("Id")
    private String id;

    @SerializedName("UserId")
    private String userId;

    @SerializedName("RoomId")
    private String roomId;

    @SerializedName("CheckInDate")
    private String checkInDate;

    @SerializedName("CheckOutDate")
    private String checkOutDate;

    @SerializedName("TotalPrice")
    private double totalPrice;

    @SerializedName("BookingStatus")
    private int bookingStatus;

    @SerializedName("CreatedAt")
    private String createdAt;

    public BookingRequest() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = getCurrentTimestamp();
        this.bookingStatus = 0;
    }

    public BookingRequest(String userId, String roomId, String checkInDate, String checkOutDate, double totalPrice) {
        this();
        this.userId = userId;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
        this.bookingStatus = 0;
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(int bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public void setBookingStatusByName(String statusName) {
        switch (statusName.toLowerCase()) {
            case "pending":
                this.bookingStatus = 0;
                break;
            case "confirmed":
                this.bookingStatus = 1;
                break;
            case "checkedin":
                this.bookingStatus = 2;
                break;
            case "completed":
                this.bookingStatus = 3;
                break;
            case "cancelled":
                this.bookingStatus = 4;
                break;
            case "noshow":
                this.bookingStatus = 5;
                break;
            default:
                this.bookingStatus = 0;
        }
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}