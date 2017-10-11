package com.example.remembergroup.model;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by chauvansang on 9/21/2017.
 */

public class Friend {
    //name of your friend
    private String name = "";
    //your friend is online set true or false
    private boolean isOnline = false;
    //text you and your friend chat in the last time
    private String textLast = "";
    //time you and your friend chat
    private Date dateTime = new Date();
    //id to recognize who are they
    private String id = "";


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    private Bitmap avatar;

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public Friend() {
    }

    public Friend(String name, Bitmap avatar) {
        this.name = name;
        this.avatar = avatar;
    }

    public Friend(String name) {

        this.name = name;
    }

    public Friend(String id, String name, Bitmap avt, Boolean isOn){
        this.id = id;
        this.isOnline = isOn;
        this.name = name;
        this.avatar = avt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getTextLast() {
        return textLast;
    }

    public void setTextLast(String textLast) {
        this.textLast = textLast;
    }
}
