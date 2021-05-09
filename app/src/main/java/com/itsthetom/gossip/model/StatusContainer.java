package com.itsthetom.gossip.model;

import java.util.ArrayList;

public class StatusContainer {
    private ArrayList<Status> statusArrayList;
    private String userUid,userName;
    private long statusLastTime;

    public StatusContainer(){

    }

    public StatusContainer(ArrayList<Status> statusArrayList, String userUid, String userName, long statusLastTime) {
        this.statusArrayList = statusArrayList;
        this.userUid = userUid;
        this.userName = userName;
        this.statusLastTime = statusLastTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getStatusLastTime() {
        return statusLastTime;
    }

    public void setStatusLastTime(long statusLastTime) {
        this.statusLastTime = statusLastTime;
    }

    public ArrayList<Status> getStatusArrayList() {
        return statusArrayList;
    }

    public void setStatusArrayList(ArrayList<Status> statusArrayList) {
        this.statusArrayList = statusArrayList;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
}
