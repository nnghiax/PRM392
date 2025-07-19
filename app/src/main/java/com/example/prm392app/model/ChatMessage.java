package com.example.prm392app.model;

public class ChatMessage {
    private String sender_id;
    private String content;
    private long timestamp;

    public ChatMessage() {}

    public ChatMessage(String sender_id, String content, long timestamp) {
        this.sender_id = sender_id;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getSender_id() { return sender_id; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
    public String getSenderId() {
        return sender_id;
    }
    public void setSender_id(String sender_id) { this.sender_id = sender_id; }
    public void setContent(String content) { this.content = content; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
