package com.spark005apps.magicstory.Util;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.spark005apps.magicstory.controller.MagicStoryApp;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class NetworkRequest {

    private static final String API_KEY = "";
    private static final String DALLE_URL = "https://api.openai.com/v1/images/generations";
    private static final String CHATGPT_URL = "https://api.openai.com/v1/chat/completions";


    public CompletableFuture<String> generateImageAsync(String word1, String word2, String word3,
                                                        String category) {
        String prompt = category + " picture of " + word1 + ", " + word2 + ", " + word3;
        // Make the POST request asynchronously using CompletableFuture
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("prompt", prompt);
            jsonRequest.put("size", "256x256");
        } catch (JSONException e) {
            e.printStackTrace();
            completableFuture.completeExceptionally(e);
        }

        JsonObjectRequest image_request = new JsonObjectRequest(Request.Method.POST, DALLE_URL, jsonRequest,
                response -> {
                    try {
                        JSONArray dataArray = response.getJSONArray("data");
                        JSONObject imageData = dataArray.getJSONObject(0);
                        String imageUrl = imageData.getString("url");
                        completableFuture.complete(imageUrl);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        completableFuture.completeExceptionally(e);
                    }
                }, error -> {
            error.printStackTrace();
            completableFuture.completeExceptionally(new RuntimeException(error.getMessage()));
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + API_KEY);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        int socketTimeout = 30000; // 30 seconds
        image_request.setRetryPolicy(
                new DefaultRetryPolicy(
                        socketTimeout,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MagicStoryApp.getInstance().addToRequestQueue(image_request);

        return completableFuture;
    }

    public CompletableFuture<String> generateStoryAsync(String word1, String word2, String word3,
                                                        String category) {
        //String prompt = "Write a short " + category + " about " + word1 + ", " + word2 + ", " + word3;
        String prompt = "Write a very short " + category + " scene/script about " + word1 + ", " + word2 + ", and " + word3 + ". " +
                "For example, create a very short scene that includes these elements.";
        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        try {
            // Create a HashMap to represent the request payload
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("model", "gpt-3.5-turbo-16k");

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

                    JsonObjectRequest story_request = new JsonObjectRequest(Request.Method.POST, CHATGPT_URL, requestJson,
                    response -> {
                        // Replace this with the actual API response JSON string
                        String apiResponse = response.toString();
                        String assistantReply;
                        try {
                            JSONObject parsedResponse = new JSONObject(apiResponse);
                            JSONArray choicesArray = parsedResponse.getJSONArray("choices");
                            JSONObject choice = choicesArray.getJSONObject(0);
                            JSONObject message = choice.getJSONObject("message");
                            assistantReply = message.getString("content");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        completableFuture.complete(assistantReply);
                    },
                    error -> {
                        error.printStackTrace();
                        completableFuture.completeExceptionally(new RuntimeException(error.getMessage()));
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + API_KEY);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };
            int socketTimeout = 30000; // 30 seconds
            story_request.setRetryPolicy(
                    new DefaultRetryPolicy(
                            socketTimeout,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            MagicStoryApp.getInstance().addToRequestQueue(story_request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return completableFuture;


    }
}