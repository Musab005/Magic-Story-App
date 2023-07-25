package com.apps005.magicstory.model;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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
//
//        //test(context);
//
//        //String prompt = "Generate a short " + category + "story about " + word1 + ", " + word2 + ", " + word3;
//        String prompt = "Hi";
//        try {
//            // Create a HashMap to represent the request payload
//            Map<String, Object> requestData = new HashMap<>();
//            requestData.put("model", "gpt-3.5-turbo");
//
//            // Create a HashMap for the user message
//            Map<String, String> userMessageMap = new HashMap<>();
//            userMessageMap.put("role", "user");
//            userMessageMap.put("content", prompt);
//
//            // Create an array to hold the messages
//            Object[] messagesArray = {userMessageMap};
//            requestData.put("messages", messagesArray);
//
//            // Convert the HashMap to a JSONObject using Gson
//            Gson gson = new Gson();
//            JSONObject requestJson = new JSONObject(gson.toJson(requestData));
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, CHATGPT_URL, requestJson,
//                    response -> Log.d("success",response.toString()),
//                    error -> Log.d("error", error.toString())
//            ) {
//                @Override
//                public Map<String, String> getHeaders() {
//                    Map<String, String> headers = new HashMap<>();
//                    headers.put("Authorization", "Bearer " + API_KEY);
//                    headers.put("Content-Type", "application/json");
//                    return headers;
//                }
//
//                @Override
//                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
//                    return super.parseNetworkResponse(response);
//                }
//            };
//
//            int intTimeoutPeriod = 60000; // 60 seconds timeout duration defined
//            RetryPolicy retryPolicy = new DefaultRetryPolicy(intTimeoutPeriod,
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//            request.setRetryPolicy(retryPolicy);
//
//
//            Log.d("model", "adding to RQ");
//            StoryController.getInstance(context).addToRequestQueue(request);
        }
    }


//    public static void test(Context context) {
//        JsonObjectRequest pingRequest = new JsonObjectRequest(Request.Method.GET, PING_ENDPOINT, null,
//                response -> {
//                    // Request successful, handle the response here
//                    Log.d("Ping Response: ", response.toString());
//                },
//                error -> {
//                    // Request failed, handle the error here
//                    Log.d("Ping Error: ", error.toString());
//                }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Authorization", "Bearer " + API_KEY);
////                headers.put("OpenAI-Organization", "Apps005");
////                headers.put("OpenAI-Organization-Id", "org-yx8MAeM3prp4oIAq6NOeCahC");
//                return headers;
//            }
//        };
//
//        // Add the request to the queue to execute it
//        StoryController.getInstance(context).addToRequestQueue(pingRequest);
//    }






//        StringRequest sr = new StringRequest(Request.Method.GET, URL,
//                response -> callback.onDataReceived(response.substring(0,5000)),
//                error -> callback.onError(error.getMessage()));
//        StoryController.getInstance(context).addToRequestQueue(sr);
//    }
//
//}








