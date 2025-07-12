package com.example.hotelmanagement.services.api;

import com.google.gson.Gson;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class RawCallback<T> implements retrofit2.Callback<ResponseBody> {
    private final Class<T> responseType;
    private final Callback<T> userCallback;
    private final Gson gson = new Gson();

    public RawCallback(Class<T> responseType, Callback<T> userCallback) {
        this.responseType = responseType;
        this.userCallback = userCallback;
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        if (!response.isSuccessful()) {
            userCallback.onFailure(new Exception("HTTP " + response.code()));
            return;
        }

        try {
            if (response.code() == 204) {
                userCallback.onSuccess(null);
                return;
            }

            String json = response.body().string();
            T result = gson.fromJson(json, responseType);
            userCallback.onSuccess(result);
        } catch (Exception e) {
            userCallback.onFailure(e);
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        userCallback.onFailure(t);
    }
}

