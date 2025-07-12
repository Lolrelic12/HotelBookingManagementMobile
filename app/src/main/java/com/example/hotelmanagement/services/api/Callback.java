package com.example.hotelmanagement.services.api;

public interface Callback<T> {
    void onSuccess(T result);
    void onFailure(Throwable error);
}
