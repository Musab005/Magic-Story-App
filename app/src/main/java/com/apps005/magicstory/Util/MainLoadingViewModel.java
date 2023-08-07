package com.apps005.magicstory.Util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainLoadingViewModel extends ViewModel {
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }
}

