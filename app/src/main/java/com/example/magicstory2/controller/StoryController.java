package com.example.magicstory2.controller;

import android.app.Application;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.magicstory2.MainActivity;
import com.example.magicstory2.model.ActivityStarter;
import com.example.magicstory2.model.StoryGenerationListener;
import com.example.magicstory2.model.StoryModel;

public class StoryController extends Application {

    private MainActivity mainActivity;
    private ActivityStarter activityStarter;
    private static StoryController instance;
    private RequestQueue requestQueue;
    private StoryModel model;
    public void setActivityStarter(ActivityStarter activityStarter) {
        this.activityStarter = activityStarter;
    }
    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
    public void setModel(StoryModel model) {
        this.model = model;
    }

    public StoryModel getModel() {
        return model;
    }
    public static synchronized StoryController getInstance() {
        if (instance==null) {
            Log.d("controller", "new self instance of controller made in the getInstance method");
            instance = new StoryController();
        }
        Log.d("controller", "returning static instance of controller");
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            Log.d("controller", "new RQ made");
            requestQueue = Volley.newRequestQueue(getInstance().mainActivity);
        }
        Log.d("controller", "returning RQ");
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        this.getRequestQueue().add(req);
        Log.d("controller", "request added to RQ");
    }

    @Override
    public void onCreate() {
        Log.d("controller", "controller created");
        super.onCreate();
        instance = new StoryController();
        Log.d("controller", "new controller self instance made onCreate");
        getInstance().setModel(new StoryModel(getInstance()));
        Log.d("controller", "model set to the static controller instance");
    }

    public void generateStory(String word1, String word2, String word3,
                              String category) {
        Log.d("controller", "calling generateStory from model");
        getInstance().getModel().generateStory(word1, word2, word3, category, new StoryGenerationListener() {
            @Override
            public void onDataReceived(String data) {
                Log.d("controller", "on data received. Starting new activity");
                getInstance().activityStarter.startActivity(data);
            }
            @Override
            public void onError(String error) {
                Log.d("controller", "on error received. Showing error");
                getInstance().activityStarter.showError(error);
            }
        });
    }


}
