package com.example.hotelmanagement.services.api;

import android.content.Context;

import com.example.hotelmanagement.R;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SSLCertHelper {

    public static SSLContext getSslContext(Context context) throws Exception {
        // 1. Load the self-signed cert from res/raw
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = context.getResources().openRawResource(R.raw.my_api_cert);
        Certificate ca = cf.generateCertificate(caInput);
        caInput.close();

        // 2. Create a KeyStore and add the cert
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setCertificateEntry("my_api", ca);

        // 3. Create TrustManagerFactory with this KeyStore
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        // 4. Get X509TrustManager from the factory
        TrustManager[] trustManagers = tmf.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers");
        }
        X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

        // 5. Create SSLContext that uses our TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{trustManager}, null);

        return sslContext;
    }

    public static X509TrustManager getTrustManager(Context context) throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = context.getResources().openRawResource(R.raw.my_api_cert);
        Certificate ca = cf.generateCertificate(caInput);
        caInput.close();

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setCertificateEntry("my_api", ca);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        TrustManager[] trustManagers = tmf.getTrustManagers();
        return (X509TrustManager) trustManagers[0];
    }
}

