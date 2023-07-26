package com.apps005.magicstory.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.apps005.magicstory.R;
import com.apps005.magicstory.controller.StoryController;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

public class ImageTest extends AppCompatActivity {
    private ProgressBar progressBar;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.apps005.magicstory.databinding.ActivityImageTestBinding bo =
                DataBindingUtil.setContentView(this, R.layout.activity_image_test);
        Log.d("Image Activity:", "onCreate");
        Button btn = bo.ReadStoryButton;
        ImageView iv = bo.imageView;
        progressBar = bo.progressBar;
        handler = new Handler();
        Intent intent = getIntent();
        progressBar.setVisibility(View.VISIBLE);
        displayImage(iv, intent);
//        handler.postDelayed(() -> {
//            // Code to be executed after 2 seconds
//            progressBar.setVisibility(View.INVISIBLE);
//        }, 3000);

        String word1 = intent.getStringExtra("word1");
        String word2 = intent.getStringExtra("word2");
        String word3 = intent.getStringExtra("word3");
        String category = intent.getStringExtra("category");
        progressBar.setVisibility(View.VISIBLE);
        btn.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            startStoryActivity(word1, word2, word3, category, ImageTest.this.getApplicationContext());
        });


    }

    private void displayImage(ImageView iv, Intent intent) {
        RequestOptions requestOptions = new RequestOptions().override(Target.SIZE_ORIGINAL)
                .diskCacheStrategy(DiskCacheStrategy.ALL); // Cache the image for subsequent requests
        Glide.with(ImageTest.this)
                .load(intent.getStringExtra("url")) // Load the image URL
                .apply(requestOptions)
                .into(iv); // Display the image in the ImageView
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void startStoryActivity(String word1, String word2, String word3, String category, Context context) {
            progressBar.setVisibility(View.INVISIBLE);
            StoryController.getInstance(this.getApplicationContext()).generateStory(
                    word1, word2, word3, category, context,
                    result -> {
                        Intent intent = new Intent(ImageTest.this, Story.class);
                        intent.putExtra("PossibleStory", result);
                        startActivity(intent);
                        finish();
                        progressBar.setVisibility(View.INVISIBLE);
                    });

    }


    public interface startStory {
        void onResult(String result);
    }
}