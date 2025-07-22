package com.example.hotelmanagement.services.api;

import android.content.Context;

import com.google.gson.Gson;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManager;
public class ApiService {
    private static ApiService instance;
    private final Retrofit retrofit;

    private final Gson gson = new Gson();
    private final TokenManager tokenManager;
    private final AuthInterceptor authInterceptor;
    private final RawService rawService;

    private ApiService(Context context) {
        tokenManager = new TokenManager(context);
        authInterceptor = new AuthInterceptor(tokenManager);

        OkHttpClient client;

        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true) // THAY ĐỔI: true thay vì check hostname
                    .addInterceptor(authInterceptor)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up SSL", e);
        }

        retrofit = new Retrofit.Builder()
                .baseUrl("https://10.0.2.2:7019/")
                //.baseUrl("https://localhost:7019/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        rawService = retrofit.create(RawService.class);
    }

    public static synchronized ApiService getInstance(Context context) {
        if (instance == null) {
            instance = new ApiService(context.getApplicationContext());
        }
        return instance;
    }

    // Generic GET
    public <T> void getAsync(String endpoint, Class<T> responseType, Callback<T> callback) {
        Call<ResponseBody> call = rawService.get(endpoint);
        call.enqueue(new RawCallback<>(responseType, callback));
    }

    // Generic POST
    public <T> void postAsync(String endpoint, Object data, Class<T> responseType, Callback<T> callback) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), gson.toJson(data));
        Call<ResponseBody> call = rawService.post(endpoint, body);
        call.enqueue(new RawCallback<>(responseType, callback));
    }

    // PUT
    public <T> void putAsync(String endpoint, Object data, Class<T> responseType, Callback<T> callback) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), gson.toJson(data));
        Call<ResponseBody> call = rawService.put(endpoint, body);
        call.enqueue(new RawCallback<>(responseType, callback));
    }

    // DELETE
    public void deleteAsync(String endpoint, Callback<Boolean> callback) {
        Call<ResponseBody> call = rawService.delete(endpoint);
        call.enqueue(new RawCallback<>(Boolean.class, callback));
    }
}
