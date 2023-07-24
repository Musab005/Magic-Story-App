package com.apps005.magicstory.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.apps005.magicstory.R;
import com.apps005.magicstory.model.GPT;

import org.json.JSONException;
import org.json.JSONObject;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.apps005.magicstory.databinding.ActivityTestBinding bo = DataBindingUtil.setContentView(this, R.layout.activity_test);

        Button bt = bo.button;
//        String userMessage = "Generate a short comedy story about beggar, apple, and house";
        String userMessage = "write 20 words";
        TextView tv = bo.textView;

        bt.setOnClickListener(view ->
                GPT.getChatGptResponse(userMessage, new GPT.ChatGptResponseListener() {

            @Override
            public void onSuccess(String assistantReply) {
                try {
                    JSONObject jsonResponse = new JSONObject(assistantReply);
                    String assistantReplyText = jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
                    Log.d("ChatGptResponse", "Assistant: " + assistantReplyText);
                    tv.setText(assistantReplyText);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.d("ChatGptResponse", errorMessage);
            }
        }));

    }
}