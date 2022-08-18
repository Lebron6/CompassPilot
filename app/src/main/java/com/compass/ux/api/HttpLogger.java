package com.compass.ux.api;

import android.util.Log;

import dji.thirdparty.okhttp3.logging.HttpLoggingInterceptor;


public class HttpLogger implements HttpLoggingInterceptor.Logger {
    private String TAG;

    public HttpLogger(String TAG) {
        this.TAG = TAG;
    }

    @Override
    public void log(String message) {
        Log.d(TAG, message);
    }
}