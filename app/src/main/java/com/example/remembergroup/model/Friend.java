package com.example.remembergroup.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;


public class Friend extends User {
    private boolean isOnline = false;

    public boolean isOnline() {
        return isOnline;
    }
    public void setOnline(boolean online) {
        isOnline = online;
    }

    public Friend() {
    }

    public Friend(String name) {
        super(name);
    }

    public Friend(String email, String name){
        super(email, name);
    }

    public Friend(String email, String name, String phoneNum){
        super(email, name, phoneNum);
    }

    public Friend(String email, String name, Boolean isOn){
        super(email, name);
        this.isOnline = isOn;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.email);
        parcel.writeString(this.name);
        parcel.writeValue(this.isOnline);
        parcel.writeString(this.phoneNumber);
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
        this.email = in.readString();
        this.name = in.readString();
        this.isOnline = (boolean) in.readValue(ClassLoader.getSystemClassLoader());
        this.phoneNumber = in.readString();
    }
}
