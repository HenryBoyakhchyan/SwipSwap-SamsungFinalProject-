package com.example.swipswapsamsungfinalproject;

import com.google.firebase.Timestamp;
import java.util.List;

// Represents an item card for swapping
public class ItemCard {
    private long swapId;
    private String imageUrl;
    private String imageBlob;
    private String description;
    private String address;
    private String email;
    private String userId;
    private String status;
    private Timestamp publishedDate;
    private List<String> categories;
    private List<String> chosenByUserIds;

    // !!!No-argument constructor required by Firestore
    public ItemCard() {
    }

    public ItemCard(long swapId,
                    String imageBlob,
                    String description,
                    String address,
                    String email,
                    String userId,
                    String status,
                    Timestamp publishedDate,
                    List<String> categories,
                    List<String> chosenByUserIds) {
        this.swapId = swapId;
        this.imageUrl = imageUrl;
        this.imageBlob = imageBlob;
        this.description = description;
        this.address = address;
        this.email = email;
        this.userId = userId;
        this.status = status;
        this.publishedDate = publishedDate;
        this.categories = categories;
        this.chosenByUserIds = chosenByUserIds;
    }

    // Getters
    public long getSwapId() {
        return swapId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getImageBlob() {
        return imageBlob;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }

    public String getStatus() {
        return status;
    }

    public Timestamp getPublishedDate() {
        return publishedDate;
    }

    public List<String> getCategories() {
        return categories;
    }

    public List<String> getChosenByUserIds() {
        return chosenByUserIds;
    }

    // Setters
    public void setSwapId(long swapId) {
        this.swapId = swapId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setImageBlob(String imageBlob) {
        this.imageBlob = imageBlob;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPublishedDate(Timestamp publishedDate) {
        this.publishedDate = publishedDate;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public void setChosenByUserIds(List<String> chosenByUserIds) {
        this.chosenByUserIds = chosenByUserIds;
    }
}
