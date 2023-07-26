package com.apps005.magicstory.View;

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

import com.apps005.magicstory.R;
import com.apps005.magicstory.databinding.ActivityStoryBinding;


public class Story extends AppCompatActivity {
    private ScrollView scrollView;
    private ConstraintLayout buttonLayout;
    private TextView storyText;
    private Button done_button;
    private Button reg_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Story Activity:", "onCreate");
        com.apps005.magicstory.databinding.ActivityStoryBinding bo = DataBindingUtil.setContentView(this, R.layout.activity_story);
        //intent from ImageActivity
        Intent intent = getIntent();
        widgets_init(bo);
        storyText.setText(intent.getStringExtra("PossibleStory"));

        done_button.setOnClickListener(view -> finish());

        reg_button.setOnClickListener(view -> finish());
    }

    private void widgets_init(ActivityStoryBinding bo) {
        done_button = bo.DoneButton;
        reg_button = bo.RegButton;
        scrollView = bo.scrollView;
        buttonLayout = bo.buttonLayout;
        storyText = bo.storyText;
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
                buttonLayout.setVisibility(View.GONE);
            }
        });
    }

}