package com.example.swipswapsamsungfinalproject;

import com.google.firebase.Timestamp;

public class UserSelection {
    private long swapId;
    private String userId;
    private String userEmail;
    private Timestamp selectedDate;
    private String status; // (chosen, accepted, rejected, or cancelled )

    // Required empty constructor for Firestore
    public UserSelection() {
    }

    // Constructor with all fields
    public UserSelection(long swapId, String userId, String userEmail, Timestamp selectedDate, String status) {
        this.swapId = swapId;
        this.userId = userId;
        this.userEmail = userEmail;
        this.selectedDate = selectedDate;
        this.status = status;
    }

    // Getters
    public long getSwapId() {
        return swapId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Timestamp getSelectedDate() {
        return selectedDate;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setSwapId(long swapId) {
        this.swapId = swapId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setSelectedDate(Timestamp selectedDate) {
        this.selectedDate = selectedDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
