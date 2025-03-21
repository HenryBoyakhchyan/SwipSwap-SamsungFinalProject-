package com.example.swipswapsamsungfinalproject;

import com.google.firebase.Timestamp;

public class MessageItem {
    private String chatDocumentId;
    private String senderId;
    private String senderEmail;
    private String messageText;
    private Timestamp sendDate;
    private String messageStatus;

    // Required empty constructor for Firestore serialization
    public MessageItem() {
    }

    public MessageItem(String chatDocumentId, String senderId, String senderEmail, String messageText, Timestamp sendDate, String messageStatus) {
        this.chatDocumentId = chatDocumentId;
        this.senderId = senderId;
        this.senderEmail = senderEmail;
        this.messageText = messageText;
        this.sendDate = sendDate;
        this.messageStatus = messageStatus;
    }

    // Getters
    public String getChatDocumentId() {
        return chatDocumentId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getMessageText() {
        return messageText;
    }

    public Timestamp getSendDate() {
        return sendDate;
    }

    public String getMessageStatus() {
        return messageStatus;
    }

    // Setters
    public void setChatDocumentId(String chatDocumentId) {
        this.chatDocumentId = chatDocumentId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setSendDate(Timestamp sendDate) {
        this.sendDate = sendDate;
    }

    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }
}
