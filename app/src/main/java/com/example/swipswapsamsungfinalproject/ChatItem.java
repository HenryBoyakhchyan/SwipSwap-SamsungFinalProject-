package com.example.swipswapsamsungfinalproject;

import com.google.firebase.Timestamp;

public class ChatItem {
    private String chatId;
    private long swapId;
    private String swapItemImageBlob; // Base64 image blob
    private String swapItemDescription;
    private String swapItemAddress;
    private String swapOwnerId;
    private String swapOwnerEmail;
    private String clientUserId;
    private String clientUserEmail;
    private Timestamp creationDate;
    private String status; // chosen, accepted, rejected
    private int unreadMessageCount;
    private Timestamp lastMessageTimestamp;

    // !!!Firestore no-arg constructor
    public ChatItem() {
    }

    public ChatItem(String chatId, long swapId, String swapItemImageBlob, String swapItemDescription,
                    String swapItemAddress, String swapOwnerId, String swapOwnerEmail,
                    String clientUserId, String clientUserEmail, Timestamp creationDate,
                    String status, int unreadMessageCount, Timestamp lastMessageTimestamp) {
        this.chatId = chatId;
        this.swapId = swapId;
        this.swapItemImageBlob = swapItemImageBlob;
        this.swapItemDescription = swapItemDescription;
        this.swapItemAddress = swapItemAddress;
        this.swapOwnerId = swapOwnerId;
        this.swapOwnerEmail = swapOwnerEmail;
        this.clientUserId = clientUserId;
        this.clientUserEmail = clientUserEmail;
        this.creationDate = creationDate;
        this.status = status;
        this.unreadMessageCount = unreadMessageCount;
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    // Getter methods
    public String getChatId() {
        return chatId;
    }

    public long getSwapId() {
        return swapId;
    }

    public String getSwapItemImageBlob() {
        return swapItemImageBlob;
    }

    public String getSwapItemDescription() {
        return swapItemDescription;
    }

    public String getSwapItemAddress() {
        return swapItemAddress;
    }

    public String getSwapOwnerId() {
        return swapOwnerId;
    }

    public String getSwapOwnerEmail() {
        return swapOwnerEmail;
    }

    public String getClientUserId() {
        return clientUserId;
    }

    public String getClientUserEmail() {
        return clientUserEmail;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public String getStatus() {
        return status;
    }

    public int getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public Timestamp getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    // Setter methods
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public void setSwapId(long swapId) {
        this.swapId = swapId;
    }

    public void setSwapItemImageBlob(String swapItemImageBlob) {
        this.swapItemImageBlob = swapItemImageBlob;
    }

    public void setSwapItemDescription(String swapItemDescription) {
        this.swapItemDescription = swapItemDescription;
    }

    public void setSwapItemAddress(String swapItemAddress) {
        this.swapItemAddress = swapItemAddress;
    }

    public void setSwapOwnerId(String swapOwnerId) {
        this.swapOwnerId = swapOwnerId;
    }

    public void setSwapOwnerEmail(String swapOwnerEmail) {
        this.swapOwnerEmail = swapOwnerEmail;
    }

    public void setClientUserId(String clientUserId) {
        this.clientUserId = clientUserId;
    }

    public void setClientUserEmail(String clientUserEmail) {
        this.clientUserEmail = clientUserEmail;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUnreadMessageCount(int unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }
}
