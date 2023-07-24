package com.apps005.magicstory.model;

import android.os.AsyncTask;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class GPT {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String API_KEY = "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";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public interface ChatGptResponseListener {
        void onSuccess(String assistantReply);

        void onError(String errorMessage);
    }

    public static void getChatGptResponse(String message, ChatGptResponseListener listener) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();

                String requestData = "{\"model\": \"gpt-3.5-turbo-16k-0613\", \"messages\": [{\"role\": \"system\", \"content\": \"You are a helpful assistant.\"}, {\"role\": \"user\", \"content\": \"" + message + "\"}]}";

                RequestBody body = RequestBody.create(requestData, JSON);

                Request request = new Request.Builder()
                        .url(API_URL)
                        .addHeader("Authorization", "Bearer " + API_KEY)
                        .post(body)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        return response.body().string();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    listener.onSuccess(result);
                } else {
                    listener.onError("API request failed.");
                }
            }
        }.execute();
    }
}
