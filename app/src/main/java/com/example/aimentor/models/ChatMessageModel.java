package com.example.aimentor.models;

public class ChatMessageModel {
    private String message;
    private boolean isUser;
    private long timestamp;
    private boolean isTyping;

    public ChatMessageModel() {
        this.timestamp = System.currentTimeMillis();
    }

    public ChatMessageModel(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
        this.timestamp = System.currentTimeMillis();
        this.isTyping = false;
    }

    public ChatMessageModel(String message, boolean isUser, boolean isTyping) {
        this.message = message;
        this.isUser = isUser;
        this.timestamp = System.currentTimeMillis();
        this.isTyping = isTyping;
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
}
