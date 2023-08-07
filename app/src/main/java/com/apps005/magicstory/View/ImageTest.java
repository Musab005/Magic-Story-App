package com.apps005.magicstory.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
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
import com.apps005.magicstory.Util.WritingAnimViewModel;
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
    private Intent intent;

    private WritingAnimViewModel writingAnimViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ImageActivity", "onCreate");
        bo = DataBindingUtil.setContentView(ImageTest.this, R.layout.activity_image_test);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        init();
        writingAnimViewModel = new ViewModelProvider(this).get(WritingAnimViewModel.class);
        writingAnimViewModel.isLoading().observe(this, isLoading -> {
            if (isLoading) {
                setUIvisibility(8);
                setAnimVisibility(0);
            } else {
                setUIvisibility(0);
                setAnimVisibility(8);
            }
        });
        proceed();
    }

    private void proceed() {
        displayImage(iv, intent);
        String word1 = intent.getStringExtra("word1");
        String word2 = intent.getStringExtra("word2");
        String word3 = intent.getStringExtra("word3");
        String category = intent.getStringExtra("category");
        arrow.setOnClickListener(view -> startStoryActivity(word1, word2, word3, category, ImageTest.this.getApplicationContext()));
    }

    private void displayImage(ImageView iv, Intent intent) {
        statement.setVisibility(View.GONE);
        arrow.setVisibility(View.GONE);
        RequestOptions requestOptions = new RequestOptions().override(Target.SIZE_ORIGINAL)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(ImageTest.this)
                .load(intent.getStringExtra("url"))
                .apply(requestOptions)
                .into(iv);
        handler.postDelayed(() -> {
            statement.setVisibility(View.VISIBLE);
            arrow.setVisibility(View.VISIBLE);
        }, 2000);
    }

    private void startStoryActivity(String word1, String word2, String word3, String category, Context context) {
        writingAnimViewModel.setLoading(true);

        CompletableFuture<String> future = new ImageNetworkRequest().
                generateStoryAsync(word1, word2, word3, category, context);

// Handling the result when it becomes available
        future.thenAccept(story -> {
            // Handle the image URL when the request is successful
            // This code will run in the main thread (UI thread)
            // Use imageUrl here to display the image or perform other actions
            Intent intent = new Intent(ImageTest.this, Story.class);
            intent.putExtra("story", story);
            startActivity(intent);
            handler.postDelayed(() -> writingAnimViewModel.setLoading(false), 2000);
            finish();
        }).exceptionally(exception -> {
            Toast.makeText(ImageTest.this,"Image fail",Toast.LENGTH_SHORT).show();
            writingAnimViewModel.setLoading(false);
            // This code will also run in the main thread (UI thread)
            exception.printStackTrace();
            return null;
        });
    }

    private void setUIvisibility(int value) {
        iv.setVisibility(value);
        arrow.setVisibility(value);
        statement.setVisibility(value);
    }

    private void setAnimVisibility(int value) {
        anim.setVisibility(value);
    }

    @Override
    public void onBackPressed() {
        // Go back to the previous activity when the back button is pressed
        finish();
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


    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    private void init() {
        arrow = bo.ReadStoryArrow;
        statement = bo.readStoryStatement;
        iv = bo.imageView;
        anim = bo.animationView;
        handler = new Handler();
        intent = getIntent();
    }

}