package com.spark005apps.magicstory.model;

public class User {

    private String first_name;
    private String last_name;
    private String date_joined;
    private int count_image;
    private String username;

    public int getRead_story() {
        return read_story;
    }

    public void setRead_story(int read_story) {
        this.read_story = read_story;
    }

    public int getDone_story() {
        return done_story;
    }

    public void setDone_story(int done_story) {
        this.done_story = done_story;
    }

    private String report_words;
    private int read_story;
    private int done_story;

    public User(String first_name, String last_name, String date_joined, int count_image, String username, int read_story, int done_story, String report_words) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.date_joined = date_joined;
        this.count_image = count_image;
        this.username = username;
        this.read_story = read_story;
        this.done_story = done_story;
        this.report_words = report_words;
    }

    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public User() {
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getDate_joined() {
        return date_joined;
    }

    public void setDate_joined(String date_joined) {
        this.date_joined = date_joined;
    }

    public int getCount_image() {
        return count_image;
    }

    public void setCount_image(int count_image) {
        this.count_image = count_image;
    }

    public String getReport_words() {
        return report_words;
    }

    public void setReport_words(String report_words) {
        this.report_words = report_words;
    }
}
