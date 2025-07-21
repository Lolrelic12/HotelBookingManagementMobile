package com.example.hotelmanagement.dto.Service;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class ServiceOrderedResponse {
    @SerializedName("id")
    private String id;
    @SerializedName("serviceOrderId")
    private String serviceOrderId;
    @SerializedName("serviceId")
    private String serviceId;

    @SerializedName("unitPrice")
    private double unitPrice;
    @SerializedName("isApproved")
    private boolean isApproved;
    @SerializedName("service")
    private Service service;
    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceOrderId() {
        return serviceOrderId;
    }

    public void setServiceOrderId(String serviceOrderId) {
        this.serviceOrderId = serviceOrderId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public ServiceOrderedResponse(Service service, double unitPrice, boolean isApproved) {
        this.service = service;
        this.unitPrice = unitPrice;
        this.isApproved = isApproved;
    }

}
