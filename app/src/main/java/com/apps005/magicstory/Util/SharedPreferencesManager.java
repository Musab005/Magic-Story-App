package com.apps005.magicstory.Util;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPreferencesManager {
    private SharedPreferences preferences;
    private static SharedPreferencesManager instance;


    private SharedPreferencesManager(Context context) {
        this.preferences = context.getSharedPreferences("AppPrefs",Context.MODE_PRIVATE);
    }

    public SharedPreferencesManager() {
    }

    public static synchronized SharedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManager(context);
            Log.d("SPManager","static instance SP created");
        }
        Log.d("SPManager","returning static instance SP");
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

    public void imageWasStarted(boolean bool) {
        preferences.edit().putBoolean("UIstate", bool).apply();
    }

    public boolean getImageWasStarted() {
        return preferences.getBoolean("UIstate",true);
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
