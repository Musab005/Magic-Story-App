package com.apps005.magicstory.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.apps005.magicstory.R;
import com.apps005.magicstory.databinding.ActivityLandingPageBinding;

public class LandingPage extends AppCompatActivity {

    private ActivityLandingPageBinding bo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bo = DataBindingUtil.setContentView(this, R.layout.activity_landing_page);
    }
}