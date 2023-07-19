package com.apps005.magicstory.Util;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class Preference {
    private SharedPreferences preferences;

    public Preference(Activity context) {
        this.preferences = context.getPreferences(Context.MODE_PRIVATE);
    }

    public void saveData(String word1, String word2, String word3, String category) {
        preferences.edit().putString("word1", word1).apply();
        preferences.edit().putString("word2", word2).apply();
        preferences.edit().putString("word3", word3).apply();
        preferences.edit().putString("category", category).apply();
    }

    public void saveUserInfo(String firstName, String lastName, String username, Date dateJoined) {
        preferences.edit().putString("firstName", firstName).apply();
        preferences.edit().putString("lastName", lastName).apply();
        preferences.edit().putString("username", username).apply();
        preferences.edit().putString("dateJoined", dateJoined.toString()).apply();
    }

    public String getUsername() {
        return preferences.getString("username", "<username>");
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
