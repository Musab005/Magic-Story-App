package com.apps005.magicstory.controller;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.apps005.magicstory.View.MainActivity;
import com.apps005.magicstory.model.StoryModel;

public class StoryController extends Application {
    private static StoryController instance;
    private RequestQueue requestQueue;

    private StoryController(Context context) {
        this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        Log.d("Controller","RQ created");
    }

    public StoryController() {
    }

    public static synchronized StoryController getInstance(Context context) {
        if (instance == null) {
            instance = new StoryController(context);
            Log.d("Controller","static instance controller created");
        }
        Log.d("Controller","returning static instance controller");
        return instance;
    }
    public RequestQueue getRequestQueue() {
        Log.d("controller", "returning RQ");
        return requestQueue;
    }
    public <T> void addToRequestQueue(Request<T> req) {
        this.getRequestQueue().add(req);
        Log.d("controller", "request added to RQ");
    }

    public void generateStory(String word1, String word2, String word3,
                              String category, Context context, final MainActivity.startActivity activity) {
        Log.d("controller", "calling generateStory from model");
        new StoryModel().generateStory(word1, word2, word3, category,
                StoryController.getInstance(context),
                new StoryGenerationListener() {
            @Override
            public void onDataReceived(String data) {
                Log.d("controller", "on data received. Starting new activity");
                activity.startActivity2(data);
            }
            @Override
            public void onError(String error) {
                Log.d("controller", "on error received. Showing error");
                activity.showError2(error);
            }
        });
    }


    public interface StoryGenerationListener {
        void onDataReceived(String data);
        void onError(String error);
    }
}