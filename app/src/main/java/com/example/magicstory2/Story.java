package com.example.magicstory2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.magicstory2.databinding.ActivityStoryBinding;


public class Story extends AppCompatActivity {

    private ActivityStoryBinding bo;
    private ScrollView scrollView;
    private ConstraintLayout buttonLayout;
    private TextView storyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bo = DataBindingUtil.setContentView(this, R.layout.activity_story);
        Log.d("Success second activity ", "Second Act launched");
        Button done_button = bo.DoneButton;
        Button reg_button = bo.RegButton;
        scrollView = bo.scrollView;
        buttonLayout = bo.buttonLayout;
        storyText = bo.storyText;


        scrollView.setOnScrollChangeListener((view, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            // Get the height of the ScrollView
            int scrollViewHeight = scrollView.getHeight();
            Log.d("Scroll View Height: ", Integer.toString(scrollViewHeight));

            // Get the height of the content inside the ScrollView
            int contentHeight = storyText.getHeight();
            Log.d("Content Height: ", Integer.toString(contentHeight));

            // Get the current scroll position
            int currentScrollPosition = scrollY + scrollViewHeight;
            Log.d("current scroll position: ", Integer.toString(currentScrollPosition));

            // Check if the scroll position has reached the bottom
            if (currentScrollPosition >= contentHeight) {
                Log.d("Success ", "success");
                buttonLayout.setVisibility(View.VISIBLE);

            } else {
                Log.d("Failure ", "Failure");
                buttonLayout.setVisibility(View.INVISIBLE);
            }
        });


        done_button.setOnClickListener(view -> {
            Intent intent = getIntent();
            intent.putExtra("message_back", "Done");
            setResult(RESULT_OK,intent);
            finish();
        });

        reg_button.setOnClickListener(view -> {
            Intent intent = getIntent();
            intent.putExtra("message_back", "Regenerate");
            setResult(RESULT_OK,intent);
            finish();
        });
    }
}