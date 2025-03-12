package com.example.swipswapsamsungfinalproject;

import com.google.firebase.Timestamp;
import java.util.List;

// ItemCard.java - Represents an item card
public class ItemCard {
    private long id;
    private String imageUrl;
    private String imageBlob;
    private String description;
    private String address;
    private String email;
    private String status;
    private Timestamp publishedDate; // Firestore uses Timestamp, NOT google.type.DateTime
    private String tag;

    // **🔥 Firestore needs a no-argument constructor 🔥**
    public ItemCard() {
    }

    public ItemCard(long id,
                    String imageUrl,
                    String imageBlob,
                    String description,
                    String address,
                    String email,
                    String status,
                    Timestamp publishedDate,
                    List<String> tags) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.address = address;
        this.imageBlob = imageBlob;
        this.email = email;
        this.status = status;
        this.publishedDate = publishedDate;
        this.description = description;
        this.tag = tag;
    }

    public long getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getImageBlob() {
        return imageBlob;
    }

    public Timestamp getPublishedDate() {
        return publishedDate;
    }

    public String getTag() {
        return tag;
    }
}
