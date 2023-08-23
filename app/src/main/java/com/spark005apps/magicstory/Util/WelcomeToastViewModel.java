package com.spark005apps.magicstory.Util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WelcomeToastViewModel extends ViewModel {
    private MutableLiveData<Boolean> welcomeToastShown = new MutableLiveData<>();

    public LiveData<Boolean> isWelcomeToastShown() {
        return welcomeToastShown;
    }

    public void setWelcomeToastShown(boolean shown) {
        welcomeToastShown.setValue(shown);
    }
}
