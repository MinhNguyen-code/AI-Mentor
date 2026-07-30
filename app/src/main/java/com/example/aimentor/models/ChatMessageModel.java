package com.example.aimentor.models;

public class ChatMessageModel {
    private long id;
    private int userId;
    private String message;
    private boolean isUser;
    private long timestamp;
    private boolean isTyping;
    private String modelUsed;
    private boolean isBookmarked;
    private String imageUri; // Uri string if user attached an image

    public ChatMessageModel() {
        this.timestamp = System.currentTimeMillis();
    }

    public ChatMessageModel(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
        this.timestamp = System.currentTimeMillis();
        this.isTyping = false;
        this.isBookmarked = false;
    }

    public ChatMessageModel(String message, boolean isUser, boolean isTyping) {
        this.message = message;
        this.isUser = isUser;
        this.timestamp = System.currentTimeMillis();
        this.isTyping = isTyping;
        this.isBookmarked = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isTyping() {
        return isTyping;
    }

    public void setTyping(boolean typing) {
        isTyping = typing;
    }

    public String getModelUsed() {
        return modelUsed;
    }

    public void setModelUsed(String modelUsed) {
        this.modelUsed = modelUsed;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
