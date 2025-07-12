package com.example.hotelmanagement.services.api;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class CustomHostnameVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String hostname, SSLSession session) {
        // Allow 10.0.2.2 even though cert says CN=localhost
        return "10.0.2.2".equals(hostname);
    }
}