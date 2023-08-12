package com.apps005.magicstory.controller;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.apps005.magicstory.View.ImageActivity;
import com.apps005.magicstory.View.MainActivity;
import com.apps005.magicstory.model.StoryModel;

public class StoryController extends Application {
    private static StoryController instance;
    private RequestQueue requestQueue;

    private StoryController(Context context) {
        this.requestQueue = Volley.newRequestQueue(context);
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
                              String category, Context context, final ImageActivity.startStory callback) {
        Log.d("controller", "calling generateStory from model");
        new StoryModel().generateStory(word1, word2, word3, category, context,
                callback::onResult);
    }

    public void generateImage(String word1, String word2, String word3,
                              String category, Context context, final MainActivity.startImage callback) {
        Log.d("controller", "calling generateImage from model");
        new StoryModel().generateImage(word1, word2, word3, category, context, new ImageGenerationListener() {
            @Override
            public void onSuccess(String url) {
                Log.d("controller", "on image url received. Starting image activity");
                callback.onSuccess(url);
            }
            @Override
            public void onError(String error) {
                Log.d("controller", "on error received.");
                callback.onError(error);
            }
        });


    }


    public interface StoryGenerationListener {
        void onResult(String result);
    }

    public interface ImageGenerationListener {
        void onSuccess(String url);
        void onError(String error);
    }
}