package com.example.hotelmanagement;

import android.content.Context;
import com.example.hotelmanagement.dto.RoomTypeResponse;
import com.example.hotelmanagement.services.api.ApiService;
import com.example.hotelmanagement.services.api.Callback;
import java.util.HashMap;
import java.util.Map;

public class RoomTypeActivity {
    private ApiService apiService;
    private Map<String, RoomTypeResponse> roomTypeCache;
    private boolean isLoading = false;
    private boolean hasLoadedOnce = false;

    public RoomTypeActivity(Context context) {
        this.apiService = ApiService.getInstance(context);
        this.roomTypeCache = new HashMap<>();
    }

    public void getAllRoomTypes(Callback<RoomTypeResponse[]> callback) {
        if (isLoading) {
            return;
        }

        isLoading = true;

        apiService.getAsync("api/RoomType/GetAll", RoomTypeResponse[].class, new Callback<RoomTypeResponse[]>() {
            @Override
            public void onSuccess(RoomTypeResponse[] result) {
                isLoading = false;
                hasLoadedOnce = true;

                if (result != null && result.length > 0) {
                    roomTypeCache.clear();

                    for (RoomTypeResponse roomType : result) {
                        if (roomType != null && roomType.getId() != null) {
                            String idAsString = roomType.getId().toString();
                            roomTypeCache.put(idAsString, roomType);
                        }
                    }
                }

                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Throwable error) {
                isLoading = false;
                callback.onFailure(error);
            }
        });
    }

    public String getRoomTypeDescription(String roomTypeId) {
        if (roomTypeId == null || roomTypeId.trim().isEmpty()) {
            return "Unknown Type";
        }

        String cleanedRoomTypeId = roomTypeId.trim();

        RoomTypeResponse roomType = roomTypeCache.get(cleanedRoomTypeId);
        if (roomType != null) {
            String description = roomType.getDescription();
            return description != null ? description : "Unknown Type";
        } else {
            return "Unknown Type";
        }
    }

    public boolean isRoomTypeCached(String roomTypeId) {
        return roomTypeId != null && roomTypeCache.containsKey(roomTypeId.trim());
    }

    public void clearCache() {
        roomTypeCache.clear();
        hasLoadedOnce = false;
    }

    public boolean hasLoadedOnce() {
        return hasLoadedOnce;
    }

    public int getCacheSize() {
        return roomTypeCache.size();
    }
}