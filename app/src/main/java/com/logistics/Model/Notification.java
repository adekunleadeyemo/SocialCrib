package com.logistics.Model;

import com.google.firebase.Timestamp;

public class Notification {
    private String id;
    private String sender;
    private String reciever;
    private String message;
    private String summary;
    private Timestamp timestamp;

    public  Notification (){

    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTimestamp() {
        return String.valueOf(timestamp.compareTo(Timestamp.now()));
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
