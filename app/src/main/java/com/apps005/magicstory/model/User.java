package com.apps005.magicstory.model;

public class User {

    private String first_name;
    private String last_name;
    private String date_joined;
    private int count_usage;
    private String username;

    public User(String first_name, String last_name, String date_joined, int count_usage, String username) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.date_joined = date_joined;
        this.count_usage = count_usage;
        this.username = username;
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

    public int getCount_usage() {
        return count_usage;
    }

    public void setCount_usage(int count_usage) {
        this.count_usage = count_usage;
    }
}
