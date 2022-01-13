package com.example.college_students_communication_app.models;

public class Chat {

    public String message, senderId, messageId;
    public long time;

    public Chat(String message, String senderId, String messageId, long time) {
        this.message = message;
        this.senderId = senderId;
        this.messageId = messageId;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

}
