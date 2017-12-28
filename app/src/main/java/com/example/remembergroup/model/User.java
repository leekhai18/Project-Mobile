package com.example.remembergroup.model;


import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    protected String name = "";
    protected String email = "";
    protected Bitmap avatar;
    protected String phoneNumber = "";

    public String getPhoneNumber() {return this.phoneNumber;}
    public void setPhoneNumber(String num) {this.phoneNumber = num;}
    public String getEmail() {return this.email;}
    public void setEmail(String email) {this.email = email;}
    public Bitmap getAvatar() {return avatar;}
    public void setAvatar(Bitmap avatar) {this.avatar = avatar;}
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    public User(String name, Bitmap avatar) {
        this.name = name;
        this.avatar = avatar;
    }

    public User(String email, String name, Bitmap avt){
        this.email = email;
        this.name = name;
        this.avatar = avt;
    }

    public User(String email, String name, Bitmap avt, String phoneNumber){
        this.email = email;
        this.name = name;
        this.avatar = avt;
        this.phoneNumber = phoneNumber;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeParcelable(avatar, i);
        parcel.writeString(phoneNumber);
    }

    protected User(Parcel in) {
        name = in.readString();
        email = in.readString();
        avatar = in.readParcelable(Bitmap.class.getClassLoader());
        phoneNumber = in.readString();
    }
}
