package com.itsthetom.gossip.model;

public class Status {
    private String statusId, statusImgUrl;
    private long timeStamp;

    public Status(){

    }
    public Status(String statusId, String statusImgUrl, long timeStamp) {
        this.statusId = statusId;
        this.statusImgUrl = statusImgUrl;
        this.timeStamp = timeStamp;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getStatusImgUrl() {
        return statusImgUrl;
    }

    public void setStatusImgUrl(String statusImgUrl) {
        this.statusImgUrl = statusImgUrl;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
