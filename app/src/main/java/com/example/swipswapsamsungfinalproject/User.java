package com.example.swipswapsamsungfinalproject;

import com.google.firebase.Timestamp;

public class User {
    private String userId;
    private String name;
    private String email;
    private String address;
    private String profileImageBlob;
    private Timestamp joinedDate;

    // Empty constructor required by Firestore
    public User() {
    }

    // Full constructor
    public User(String userId, String name, String email, String address, String profileImageBlob, Timestamp joinedDate) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.address = address;
        this.profileImageBlob = profileImageBlob;
        this.joinedDate = joinedDate;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getProfileImageBlob() {
        return profileImageBlob;
    }

    public Timestamp getJoinedDate() {
        return joinedDate;
    }

    // Setters
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setProfileImageBlob(String profileImageBlob) {
        this.profileImageBlob = profileImageBlob;
    }

    public void setJoinedDate(Timestamp joinedDate) {
        this.joinedDate = joinedDate;
    }
}
