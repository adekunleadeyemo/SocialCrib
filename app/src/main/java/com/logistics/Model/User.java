package com.logistics.Model;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String Id;
    private String phone;
    private  String firstName;
    private String lastName;
    private String username;
    private Timestamp createdTimestamp;
    private int age;
    private String bio;

    private String imgUrl;

    private String fcmToken;

    private List<String> following;
    private List<String> followers;

    private List<String> topics;

    public List<String> notifications;

    public List<String> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<String> notifications) {
        this.notifications = notifications;
    }

    public void addNotification(String Id){
        if(notifications == null){
            notifications = new ArrayList<>();
        }

        notifications.add(Id);
    }

    public void resetNotification(String Id){
        notifications = null;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public List<String> getFollowing() {
        return following;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }

    public void addFollowing(String Id) {
        if(following == null){
            following = new ArrayList<>();
            following.add(Id);
        }
        else {
            following.add(Id);
        }

    }

    public void removeFollowing(String Id) {
        if(following != null){
            following.remove(Id);
        }

    }

    public void addFollower(String Id) {
        if(followers == null){
            followers = new ArrayList<>();
            followers.add(Id);
        }
        else {
            followers.add(Id);
        }

    }

    public void removeFollower(String Id) {
        if(followers != null){
            followers.remove(Id);
        }

    }
    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public User() {
    }
    public User(String Id, String phone, Timestamp createdTimestamp) {
        this.Id = Id;
        this.phone = phone;
        this.createdTimestamp = createdTimestamp;
    }
    public String getUserId() {
        return Id;
    }

    public void setUserId(String userId) {
        this.Id = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
