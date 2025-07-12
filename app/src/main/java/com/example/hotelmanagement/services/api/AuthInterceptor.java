package com.example.hotelmanagement.services.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final TokenManager tokenManager;

    public AuthInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String token = tokenManager.getToken();

        if (token != null) {
            Request.Builder builder = original.newBuilder()
                    .header("Authorization", "Bearer " + token);
            Request request = builder.build();
            return chain.proceed(request);
        }

        return chain.proceed(original);
    }
}
