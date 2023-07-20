package com.apps005.magicstory.Util;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class SharedPreferencesManager {
    private SharedPreferences preferences;
    private static SharedPreferencesManager instance;


//    // Inside an activity
//    SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(this);
//    String userName = sharedPreferencesManager.getUserName();
//// Use userName as needed

    public SharedPreferencesManager(Activity context) {
        this.preferences = context.getPreferences(Context.MODE_PRIVATE);
    }

    public static synchronized SharedPreferencesManager getInstance(Activity context) {
        if (instance == null) {
            instance = new SharedPreferencesManager(context);
        }
        return instance;
    }

    public void saveData(String word1, String word2, String word3, String category) {
        preferences.edit().putString("word1", word1).apply();
        preferences.edit().putString("word2", word2).apply();
        preferences.edit().putString("word3", word3).apply();
        preferences.edit().putString("category", category).apply();
    }

    public void saveUsername(String username) {
        preferences.edit().putString("username", username).apply();
    }

    public String getUsername() {
        return preferences.getString("username", "");
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
