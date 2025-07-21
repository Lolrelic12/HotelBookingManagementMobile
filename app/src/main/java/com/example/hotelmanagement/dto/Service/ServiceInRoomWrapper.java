package com.example.hotelmanagement.dto.Service;

import com.google.gson.annotations.SerializedName;

public class ServiceInRoomWrapper {
    @SerializedName("value")
    private ServicesInRoomResponse value;

    public ServicesInRoomResponse getValue() {
        return value;
    }

    public void setValue(ServicesInRoomResponse value) {
        this.value = value;
    }
}
