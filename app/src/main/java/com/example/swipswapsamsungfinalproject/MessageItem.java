package com.example.swipswapsamsungfinalproject;

import com.google.firebase.Timestamp;

public class MessageItem {
    private String chatDocumentId;
    private String senderId;
    private String text;
    private long timestamp;

    // Required empty constructor for Firestore serialization
    public MessageItem() {
    }

    public MessageItem(String chatDocumentId, String senderId, String messageText, long timestamp) {
        this.chatDocumentId = chatDocumentId;
        this.senderId = senderId;
        this.text = messageText;
        this.timestamp = timestamp;
    }

    // Getters
    public String getChatDocumentId() {
        return chatDocumentId;
    }

    public String getSenderId() {
        return senderId;
    }


    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }


    // Setters
    public void setChatDocumentId(String chatDocumentId) {
        this.chatDocumentId = chatDocumentId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
