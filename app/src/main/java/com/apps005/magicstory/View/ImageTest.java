package com.apps005.magicstory.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.apps005.magicstory.R;
import com.apps005.magicstory.Util.ImageNetworkRequest;
import com.apps005.magicstory.databinding.ActivityImageLandTestBinding;
import com.apps005.magicstory.databinding.ActivityImageTestBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import java.util.concurrent.CompletableFuture;

public class ImageTest extends AppCompatActivity {
    private Handler handler;
    private LottieAnimationView anim;
    private ImageView iv;
    private ImageView arrow;
    private TextView statement;
    private ActivityImageTestBinding bo;
    private ActivityImageLandTestBinding bo_land;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ImageActivity", "onCreate");
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bo_land = DataBindingUtil.setContentView(this, R.layout.activity_image_land_test);
            init_landscape();
            Log.d("ImageActivity", "landscape config set");
        } else {
            bo = DataBindingUtil.setContentView(ImageTest.this, R.layout.activity_image_test);
            init_portrait();
            Log.d("ImageActivity", "portrait config set");
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        proceed();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }
    private void proceed() {
        displayImage(iv, intent);
        String word1 = intent.getStringExtra("word1");
        String word2 = intent.getStringExtra("word2");
        String word3 = intent.getStringExtra("word3");
        String category = intent.getStringExtra("category");
        arrow.setOnClickListener(view -> startStoryActivity(word1, word2, word3, category, ImageTest.this.getApplicationContext()));
    }

    private void proceed_onConfig() {
        displayImage_onConfig(iv, intent);
        String word1 = intent.getStringExtra("word1");
        String word2 = intent.getStringExtra("word2");
        String word3 = intent.getStringExtra("word3");
        String category = intent.getStringExtra("category");
        arrow.setOnClickListener(view -> startStoryActivity(word1, word2, word3, category, ImageTest.this.getApplicationContext()));
    }

    private void init_landscape() {
        arrow = bo_land.ReadStoryArrow;
        statement = bo_land.readStoryStatement;
        iv = bo_land.imageView;
        anim = bo_land.animationView;
        anim.setVisibility(View.GONE);
        handler = new Handler();
        intent = getIntent();
    }

    private void init_portrait() {
        arrow = bo.ReadStoryArrow;
        statement = bo.readStoryStatement;
        iv = bo.imageView;
        anim = bo.animationView;
        anim.setVisibility(View.GONE);
        handler = new Handler();
        intent = getIntent();
    }


    private void displayImage(ImageView iv, Intent intent) {
        iv.setVisibility(View.GONE);
        arrow.setVisibility(View.GONE);
        statement.setVisibility(View.GONE);

        RequestOptions requestOptions = new RequestOptions().override(Target.SIZE_ORIGINAL)
                .diskCacheStrategy(DiskCacheStrategy.ALL); // Cache the image for subsequent requests
        Glide.with(ImageTest.this)
                .load(intent.getStringExtra("url")) // Load the image URL
                .apply(requestOptions)
                .into(iv); // Display the image in the ImageView
        iv.setVisibility(View.VISIBLE);
        handler.postDelayed(() -> {
            // Code to be executed after t seconds
            arrow.setVisibility(View.VISIBLE);
            statement.setVisibility(View.VISIBLE);
        }, 5000);
    }

    private void displayImage_onConfig(ImageView iv, Intent intent) {
        iv.setVisibility(View.GONE);
        arrow.setVisibility(View.GONE);
        statement.setVisibility(View.GONE);

        RequestOptions requestOptions = new RequestOptions().override(Target.SIZE_ORIGINAL)
                .diskCacheStrategy(DiskCacheStrategy.ALL); // Cache the image for subsequent requests
        Glide.with(ImageTest.this)
                .load(intent.getStringExtra("url")) // Load the image URL
                .apply(requestOptions)
                .into(iv); // Display the image in the ImageView
        iv.setVisibility(View.VISIBLE);
        arrow.setVisibility(View.VISIBLE);
        statement.setVisibility(View.VISIBLE);
    }

    private void startStoryActivity(String word1, String word2, String word3, String category, Context context) {
        iv.setVisibility(View.GONE);
        arrow.setVisibility(View.GONE);
        statement.setVisibility(View.GONE);
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
    @Override
    public void onBackPressed() {
        // Go back to the previous activity when the back button is pressed
        finish();
    }
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bo_land = DataBindingUtil.setContentView(this, R.layout.activity_image_land_test);
            Log.d("MainActivity onConfig", "setting landscape");
            init_landscape();
            proceed_onConfig();
        } else {
            bo = DataBindingUtil.setContentView(this, R.layout.activity_image_test);
            Log.d("MainActivity onConfig", "setting portrait");
            init_portrait();
            proceed_onConfig();
        }
    }

    public interface startStory {
        void onResult(String result);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("ImageActivity", "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("ImageActivity", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("ImageActivity", "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ImageActivity", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ImageActivity", "onResume");
    }


}