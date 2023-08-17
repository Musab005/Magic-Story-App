package com.apps005.magicstory.controller;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.apps005.magicstory.Util.SharedPreferencesManager;
import com.apps005.magicstory.View.LoginActivity;
import com.apps005.magicstory.View.MainActivity;

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
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        this.finish();

    }
}
