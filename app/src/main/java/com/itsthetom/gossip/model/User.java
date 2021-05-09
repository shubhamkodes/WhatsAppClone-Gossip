package com.itsthetom.gossip.model;

public class User {
    private String name,profileImgUrl,uid,phoneNumber;
    public User(){

    }

    public User(String name, String profileImgUrl, String uid,String number) {
        this.name = name;
        this.profileImgUrl = profileImgUrl;
        this.uid = uid;
        this.phoneNumber=number;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
