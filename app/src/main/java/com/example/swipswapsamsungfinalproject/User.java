package com.example.swipswapsamsungfinalproject;

public class User {
    public String userId, username, email;

    public User() {} // Required for Firebase

    public User(String userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }
}
