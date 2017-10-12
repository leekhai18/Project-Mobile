package com.example.remembergroup.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;
import java.io.NotSerializableException;

/**
 * Created by chauvansang on 9/21/2017.
 */

public class Friend implements Parcelable {
    //name of your friend
    private String name = "";
    //your friend is online set true or false
    private boolean isOnline = false;
    //id to recognize who are they
    private String id = "";

    private Bitmap avatar;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.name);
        parcel.writeValue(this.isOnline);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Friend> CREATOR = new Parcelable.Creator<Friend>() {
        public Friend createFromParcel(Parcel in) {
            return new Friend(in);
        }

        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };

    private Friend(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.isOnline = (boolean) in.readValue(ClassLoader.getSystemClassLoader());
    }
}
