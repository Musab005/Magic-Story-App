package com.apps005.magicstory.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.apps005.magicstory.Util.SharedPreferencesManager;
import com.apps005.magicstory.View.LandingPage;
import com.apps005.magicstory.View.MainActivity;

public class InitialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(InitialActivity.this, "initial onCreate", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(InitialActivity.this, "initial onResume", Toast.LENGTH_SHORT).show();
        SharedPreferencesManager instance_SP = SharedPreferencesManager.getInstance(this.getApplicationContext());
        if (instance_SP.isFirstLaunch()) {
            startActivity(new Intent(this, LandingPage.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        this.finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(InitialActivity.this, "initial onDestroy", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(InitialActivity.this, "Initial onPause", Toast.LENGTH_SHORT).show();
    }
}