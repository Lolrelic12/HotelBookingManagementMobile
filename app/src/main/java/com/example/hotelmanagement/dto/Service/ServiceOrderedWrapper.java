package com.example.hotelmanagement.dto.Service;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ServiceOrderedWrapper {
    @SerializedName("value")
    private ArrayList<ServiceOrderedResponse> value;

    public ArrayList<ServiceOrderedResponse> getValue() {
        return value;
    }

    public void setValue(ArrayList<ServiceOrderedResponse> value) {
        this.value = value;
    }
}
