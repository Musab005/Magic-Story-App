package com.apps005.magicstory.Util;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class WidgetStateManager {
    private static WidgetStateManager instance;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private WidgetStateManager() {
        // Private constructor to prevent instantiation
    }

    public static WidgetStateManager getInstance() {
        if (instance == null) {
            instance = new WidgetStateManager();
        }
        return instance;
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }
}
