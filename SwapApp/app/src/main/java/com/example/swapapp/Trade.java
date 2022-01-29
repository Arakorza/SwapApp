package com.example.swapapp;

public class Trade {

    public Boolean tradeStatus, user1Status, user2Status;
    public String user1UID, user1Post, user2UID, user2Post;
    public Long timeStamp;

    public Trade() { }

    public Trade(String user1UID, String user1Post, String user2UID) {
        tradeStatus = false;
        user1Status = false;
        user2Status = false;

        this.user1UID = user1UID;
        this.user1Post = user1Post;

        this.user2UID = user2UID;
        user2Post = "";

        timeStamp = System.currentTimeMillis();
    }
}
