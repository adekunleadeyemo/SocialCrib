package com.logistics.Model;

import com.google.firebase.Timestamp;

public class Notification {
    private String Id;
    private String sender;
    private String reciever;
    private String message;
    private String summary;
    private Timestamp timestamp;

    public  Notification (){

    }

    public Notification(String id, String sender, String reciever, String message, Timestamp timestamp) {
        Id = id;
        this.sender = sender;
        this.reciever = reciever;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
