package com.spark005apps.magicstory.controller;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.spark005apps.magicstory.Util.SharedPreferencesManager;
import com.spark005apps.magicstory.View.CreateAccountActivity;
import com.spark005apps.magicstory.View.MainActivity;

public class InitialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferencesManager instance_SP = SharedPreferencesManager.getInstance(this.getApplicationContext());
        if (instance_SP.isFirstLaunch()) {
            startActivity(new Intent(this, CreateAccountActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        this.finish();

    }
}
