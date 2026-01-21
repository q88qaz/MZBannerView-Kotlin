package com.vise.xsnow.http.request;


import android.annotation.SuppressLint;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SSLUtils {
    public SSLUtils() {
    }

    @SuppressLint({"TrulyRandom"})
    public static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory sSLSocketFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init((KeyManager[])null, new TrustManager[]{new TrustAllManager()}, new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception var2) {
        }

        return sSLSocketFactory;
    }

    public static class TrustAllHostnameVerifier implements HostnameVerifier {
        public TrustAllHostnameVerifier() {
        }

        @SuppressLint({"BadHostnameVerifier"})
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    public static class TrustAllManager implements X509TrustManager {
        public TrustAllManager() {
        }

        @SuppressLint({"TrustAllX509TrustManager"})
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @SuppressLint({"TrustAllX509TrustManager"})
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}

