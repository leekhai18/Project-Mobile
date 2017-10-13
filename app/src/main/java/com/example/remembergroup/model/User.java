package com.example.remembergroup.model;


import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public abstract class User implements Parcelable {

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

/*    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.email);
        parcel.writeString(this.name);
        parcel.writeValue(this.avatar);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {return new User(in);}
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private User(Parcel in) {
        this.email = in.readString();
        this.name = in.readString();
        this.avatar = (Bitmap) in.readValue(ClassLoader.getSystemClassLoader());
    }*/
}
