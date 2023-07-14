package com.example.magicstory2.model;

public interface StoryGenerationListener {
    void onDataReceived(String data);
    void onError(String error);
}
