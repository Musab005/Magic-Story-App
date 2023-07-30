package com.apps005.magicstory.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.apps005.magicstory.R;
import com.apps005.magicstory.Util.ImageNetworkRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ImageTest extends AppCompatActivity {
    private ProgressBar progressBar;
    private Handler handler;
    private LottieAnimationView anim;
    private Button btn;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.apps005.magicstory.databinding.ActivityImageTestBinding bo = DataBindingUtil.setContentView(ImageTest.this, R.layout.activity_image_test);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Log.d("Image Activity:", "onCreate");
        btn = bo.ReadStoryButton;
        iv = bo.imageView;
        progressBar = bo.progressBar;
        anim = bo.animationView;
        anim.setVisibility(View.GONE);
        handler = new Handler();
        Intent intent = getIntent();
        displayImage(iv, intent);

        String word1 = intent.getStringExtra("word1");
        String word2 = intent.getStringExtra("word2");
        String word3 = intent.getStringExtra("word3");
        String category = intent.getStringExtra("category");
        btn.setOnClickListener(view -> startStoryActivity(word1, word2, word3, category, ImageTest.this.getApplicationContext()));


    }

    @SuppressLint("ResourceAsColor")
    private void displayImage(ImageView iv, Intent intent) {
        iv.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        btn.setVisibility(View.GONE);

        RequestOptions requestOptions = new RequestOptions().override(Target.SIZE_ORIGINAL)
                .diskCacheStrategy(DiskCacheStrategy.ALL); // Cache the image for subsequent requests
        Glide.with(ImageTest.this)
                .load(intent.getStringExtra("url")) // Load the image URL
                .apply(requestOptions)
                .into(iv); // Display the image in the ImageView
        iv.setVisibility(View.VISIBLE);
        handler.postDelayed(() -> {
            // Code to be executed after t seconds
            btn.setVisibility(View.VISIBLE);
        }, 5000);
    }

    private void startStoryActivity(String word1, String word2, String word3, String category, Context context) {
        iv.setVisibility(View.GONE);
        btn.setVisibility(View.GONE);
        anim.setVisibility(View.VISIBLE);
        anim.playAnimation();
        CompletableFuture<String> future = new ImageNetworkRequest().
                generateStoryAsync(word1, word2, word3, category, context);

// Handling the result when it becomes available
        future.thenAccept(story -> {
            // Handle the image URL when the request is successful
            // This code will run in the main thread (UI thread)
            // Use imageUrl here to display the image or perform other actions
            Toast.makeText(ImageTest.this,"Image success",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ImageTest.this, Story.class);
            intent.putExtra("story", story);
            startActivity(intent);
            finish();
        }).exceptionally(exception -> {
            Toast.makeText(ImageTest.this,"Image fail",Toast.LENGTH_SHORT).show();
            // Handle exceptions here, if any
            // This code will also run in the main thread (UI thread)
            exception.printStackTrace();
            return null;
        });
    }


    public interface startStory {
        void onResult(String result);
    }
}