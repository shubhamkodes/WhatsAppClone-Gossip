package com.itsthetom.gossip.model;

public class Message {
    private String msg,msgId,senderUid;
    private int reactions;
    private long timeStamp;
    public Message(){

    }

    public Message(String msg, String msgId,String senderUid, int reactions, long timeStamp) {
        this.msg = msg;
        this.senderUid = senderUid;
        this.reactions = reactions;
        this.timeStamp = timeStamp;
        this.msgId=msgId;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public int getReactions() {
        return reactions;
    }

    public void setReactions(int reactions) {
        this.reactions = reactions;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
