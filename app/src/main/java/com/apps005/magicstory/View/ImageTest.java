package com.apps005.magicstory.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.apps005.magicstory.R;
import com.apps005.magicstory.model.StoryModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

public class ImageTest extends AppCompatActivity {

    public interface loadImage {
        void startImageActivity(String url);
    }


    private String IMAGE_URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.apps005.magicstory.databinding.ActivityImageTestBinding bo =
                DataBindingUtil.setContentView(this, R.layout.activity_image_test);
        Button btn = bo.button2;
        ImageView iv = bo.imageView;

        btn.setOnClickListener(
                view -> new StoryModel().image(ImageTest.this.getApplicationContext(),
                        url ->
                        {
            IMAGE_URL = url;
            // Load the image using Glide
            RequestOptions requestOptions = new RequestOptions().override(Target.SIZE_ORIGINAL)
                    .diskCacheStrategy(DiskCacheStrategy.ALL); // Cache the image for subsequent requests
            Glide.with(ImageTest.this)
                    .load(IMAGE_URL) // Load the image URL
                    .apply(requestOptions)
                    .into(iv); // Display the image in the ImageView
        }
        )
        );
    }
}