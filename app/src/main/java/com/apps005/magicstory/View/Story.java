package com.apps005.magicstory.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.apps005.magicstory.R;
import com.apps005.magicstory.databinding.ActivityStoryBinding;



public class Story extends AppCompatActivity {
    private ScrollView scrollView;
    private LinearLayout buttonLayout;
    private TextView storyText;
    private Button done_button;
    private ActivityStoryBinding bo;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("StoryActivity", "onCreate");
        bo = DataBindingUtil.setContentView(this, R.layout.activity_story);
        init_portrait();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        proceed();

    }
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private void proceed() {
        storyText.setText(intent.getStringExtra("story"));
        done_button.setOnClickListener(view -> finish());
    }

    private void init_portrait() {
        done_button = bo.DoneButton;
        scrollView = bo.scrollView;
        buttonLayout = bo.buttonLayout;
        storyText = bo.storyText;
        widgets_init();
        intent = getIntent();
    }


    private void widgets_init() {
        scrollView.setOnScrollChangeListener((view, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            // Get the height of the ScrollView
            int scrollViewHeight = scrollView.getHeight();

            // Get the height of the content inside the ScrollView
            int contentHeight = storyText.getHeight();

            // Get the current scroll position
            int currentScrollPosition = scrollY + scrollViewHeight;

            //Check if the scroll position has reached the bottom
            if (currentScrollPosition >= contentHeight) {
                buttonLayout.setVisibility(View.VISIBLE);
            } else {
                buttonLayout.setVisibility(View.INVISIBLE);
            }
        });
    }
    @Override
    public void onBackPressed() {
        // Go back to the previous activity when the back button is pressed
        finish();
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d("StoryActivity", "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("StoryActivity", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("StoryActivity", "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("StoryActivity", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("StoryActivity", "onResume");
    }
}