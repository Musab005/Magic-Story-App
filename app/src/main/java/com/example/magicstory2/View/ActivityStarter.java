package com.example.magicstory2.View;

import android.content.Intent;

public interface ActivityStarter {
    void startActivity(String story);
    void showError(String error);
}