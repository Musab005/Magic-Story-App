package com.example.magicstory2.controller;

public interface StoryGenerationListener {
    void onDataReceived(String data);
    void onError(String error);
}
