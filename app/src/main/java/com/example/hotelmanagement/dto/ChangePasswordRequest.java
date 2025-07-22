package com.example.hotelmanagement.dto;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordRequest {
    @SerializedName("userId")
    public String userId;

    @SerializedName("oldPassword")
    public String oldPassword;

    @SerializedName("newPassword")
    public String newPassword;

    @SerializedName("confirmPassword")
    public String confirmPassword;

    public ChangePasswordRequest(String userId, String oldPassword, String newPassword, String confirmPassword) {
        this.userId = userId;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }
}
