package com.apps005.magicstory.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.apps005.magicstory.View.ImageTest;
import com.apps005.magicstory.controller.StoryController;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;



public class StoryModel {

    private static final String API_KEY = "";
    private static final String CHATGPT_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_URL = "https://api.openai.com/v1/images/generations"; // API endpoint


    public void generateStory(String word1, String word2, String word3,
                              String category, Context context, final StoryController.StoryGenerationListener callback) {

        String prompt = "Write 100 words";
        try {
            // Create a HashMap to represent the request payload
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("model", "gpt-3.5-turbo-16k");
            //use 16k model for more tokens!

            // Create a HashMap for the user message
            Map<String, String> userMessageMap = new HashMap<>();
            userMessageMap.put("role", "user");
            userMessageMap.put("content", prompt);

            // Create an array to hold the messages
            Object[] messagesArray = {userMessageMap};
            requestData.put("messages", messagesArray);

            // Convert the HashMap to a JSONObject using Gson
            Gson gson = new Gson();
            JSONObject requestJson = new JSONObject(gson.toJson(requestData));

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, CHATGPT_URL, requestJson,
                    response -> callback.onDataReceived(response.toString()),
                    error -> callback.onError(error.getMessage())
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + API_KEY);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            StoryController.getInstance(context).addToRequestQueue(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

        public void image(Context context, final ImageTest.loadImage image) {

        String prompt = "comedy picture of apple, car, monkey";
            // Make the POST request
            JSONObject jsonRequest = new JSONObject();
            try {
                jsonRequest.put("prompt", prompt);
                jsonRequest.put("size", "256x256");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API_URL, jsonRequest,
                    response -> {
                        try {
                            JSONArray dataArray = response.getJSONArray("data");
                                JSONObject imageData = dataArray.getJSONObject(0);
                                String imageUrl = imageData.getString("url");
                                image.startImageActivity(imageUrl);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    Throwable::printStackTrace) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + API_KEY);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            int socketTimeout = 30000; // 30 seconds
            request.setRetryPolicy(
                    new DefaultRetryPolicy(
                            socketTimeout,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            StoryController.getInstance(context).addToRequestQueue(request);
        }
    }







