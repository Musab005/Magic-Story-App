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

import com.airbnb.lottie.LottieAnimationView;
import com.apps005.magicstory.R;
import com.apps005.magicstory.controller.StoryController;
import com.apps005.magicstory.databinding.ActivityImageTestBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

public class ImageTest extends AppCompatActivity {
    private ProgressBar progressBar;
    private Handler handler;
    private LottieAnimationView anim;
    private Button btn;
    private ActivityImageTestBinding bo;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bo = DataBindingUtil.setContentView(ImageTest.this, R.layout.activity_image_test);
        Log.d("Image Activity:", "onCreate");
        btn = bo.ReadStoryButton;
        iv = bo.imageView;
        progressBar = bo.progressBar;
        anim = bo.animationView;
        handler = new Handler();
        Intent intent = getIntent();
        displayImage(iv, intent);
//        handler.postDelayed(() -> {
//            // Code to be executed after 2 seconds
//
//        }, 5000);

        String word1 = intent.getStringExtra("word1");
        String word2 = intent.getStringExtra("word2");
        String word3 = intent.getStringExtra("word3");
        String category = intent.getStringExtra("category");
        btn.setOnClickListener(view -> {
            startStoryActivity(word1, word2, word3, category, ImageTest.this.getApplicationContext());
        });


    }

    @SuppressLint("ResourceAsColor")
    private void displayImage(ImageView iv, Intent intent) {
        //progressBar.setVisibility(View.VISIBLE);
        iv.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        btn.setVisibility(View.GONE);
        bo.constraintLayout.setBackgroundColor(R.color.white);
        anim.setRepeatCount(3);
        anim.playAnimation();
                handler.postDelayed(() -> {
            // Code to be executed after t seconds
                    anim.setVisibility(View.GONE);
                    bo.constraintLayout.setBackgroundColor(R.color.light_white);
        RequestOptions requestOptions = new RequestOptions().override(Target.SIZE_ORIGINAL)
                .diskCacheStrategy(DiskCacheStrategy.ALL); // Cache the image for subsequent requests
        Glide.with(ImageTest.this)
                .load(intent.getStringExtra("url")) // Load the image URL
                .apply(requestOptions)
                .into(iv); // Display the image in the ImageView
                    iv.setVisibility(View.VISIBLE);
                    btn.setVisibility(View.VISIBLE);
        }, 5000);

        //progressBar.setVisibility(View.GONE);
    }

    private void startStoryActivity(String word1, String word2, String word3, String category, Context context) {
        anim.setRepeatCount(5);
        iv.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        btn.setVisibility(View.GONE);
        anim.setVisibility(View.VISIBLE);
        anim.playAnimation();
            StoryController.getInstance(this.getApplicationContext()).generateStory(
                    word1, word2, word3, category, context,
                    result -> {
                        Intent intent = new Intent(ImageTest.this, Story.class);
                        intent.putExtra("PossibleStory", result);
                            startActivity(intent);
                            finish();
                    });

    }


    public interface startStory {
        void onResult(String result);
    }
}