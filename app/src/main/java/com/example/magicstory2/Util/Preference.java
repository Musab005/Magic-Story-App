package com.example.magicstory2.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.magicstory2.databinding.ActivityStoryBinding;

public class Preference {
    private SharedPreferences preferences;

    public Preference(Activity context) {
        this.preferences = context.getPreferences(Context.MODE_PRIVATE);
    }

    public void saveData(String word1, String word2, String word3, String category) {
        preferences.edit().putString("word1", word1);
        preferences.edit().putString("word2", word2);
        preferences.edit().putString("word3", word3);
        preferences.edit().putString("category", category);
    }

    public String getWord1() {
        return preferences.getString("word1","");
    }
    public String getWord2() {
        return preferences.getString("word2","");
    }
    public String getWord3() {
        return preferences.getString("word3","");
    }
    public String getCategory() {
        return preferences.getString("category","");
    }
}
