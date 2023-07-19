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
    private ActivityStoryBinding bo;
    private ScrollView scrollView;
    private ConstraintLayout buttonLayout;
    private TextView storyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Story Activity:", "onCreate");
        super.onCreate(savedInstanceState);
        Log.d("Story Activity:", "onCreate2");
        bo = DataBindingUtil.setContentView(this, R.layout.activity_story);
        Log.d("Story Activity:", "onCreate3");
        //intent3 from mainActivity
        Intent intent = getIntent();
        Button done_button = bo.DoneButton;
        Button reg_button = bo.RegButton;
        scrollView = bo.scrollView;
        buttonLayout = bo.buttonLayout;
        storyText = bo.storyText;
        storyText.setText(intent.getStringExtra("story"));
        Log.d("Story Activity:", "text set");
        buttonLayout.setVisibility(View.GONE);



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

        done_button.setOnClickListener(view -> {
            intent.putExtra("message_back", "Done");
            //setResult(RESULT_OK,intent);
            finish();
        });

        reg_button.setOnClickListener(view -> {
            intent.putExtra("message_back", "Regenerate");
            //onActivityResult method ??
            //setResult(RESULT_OK,intent);
            finish();
        });
    }

}