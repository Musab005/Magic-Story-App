package com.example.magicstory2.model;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.magicstory2.controller.StoryController;



public class StoryModel {
    private StoryController controller;

    public StoryModel(StoryController controller) {
        this.controller = controller;
    }

    private final static String URL = "https://www.google.com";

    public void generateStory(String word1, String word2, String word3,
                              String category,
                              StoryGenerationListener callback) {

        Log.d("model", "API request being made");
        StringRequest sr = new StringRequest(Request.Method.GET, URL,
                response -> callback.onDataReceived(response.substring(0,5000)),
                error -> callback.onError(error.getMessage()));
        controller.addToRequestQueue(sr);
    }

}


