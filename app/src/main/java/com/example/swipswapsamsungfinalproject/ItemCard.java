package com.example.swipswapsamsungfinalproject;

// ItemCard.java - Represents an item card
public class ItemCard {
    private int id;
    private String imageUrl;
    private String title;
    private String description;

    public ItemCard(int id, String imageUrl, String title) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
