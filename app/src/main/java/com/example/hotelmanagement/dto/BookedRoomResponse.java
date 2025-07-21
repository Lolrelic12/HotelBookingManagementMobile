package com.example.hotelmanagement.dto;

import com.google.gson.annotations.SerializedName;

public class BookedRoomResponse {
    @SerializedName("id")
    private String bookingId;
    @SerializedName("roomNumber")
    private String roomNumber;
    @SerializedName("checkInDate")
    private String checkInDate;
    @SerializedName("checkOutDate")
    private String checkOutDate;
    @SerializedName("totalPrice")
    private double totalPrice;
    @SerializedName("bookingStatus")
    private int bookingStatus;
    @SerializedName("createdAt")
    private String createdAt;

    public BookedRoomResponse() {
    }

    public BookedRoomResponse(String bookingId, String roomNumber, String checkInDate, String checkOutDate, double totalPrice, int bookingStatus, String createdAt) {
        this.bookingId = bookingId;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
        this.bookingStatus = bookingStatus;
        this.createdAt = createdAt;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
