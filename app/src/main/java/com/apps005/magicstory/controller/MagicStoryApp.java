package com.apps005.magicstory.controller;

import android.app.Application;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MagicStoryApp extends Application {

    private static MagicStoryApp instance;
    private RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        requestQueue = Volley.newRequestQueue(this);
    }

    public MagicStoryApp() {
    }

    public static synchronized MagicStoryApp getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MagicStoryApp instance is null");
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            throw new IllegalStateException("RequestQueue is not initialized");
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
        Log.d("Controller", "Request added to RequestQueue");
    }
}

