package com.example.remembergroup.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class Me extends User {
    private static Me instance;

    // list add friend request
    public ArrayList<String> listEUserRequest = new ArrayList<>();
    public User accepted;

    public static Me getInstance() {
        if(instance == null)
            instance = new Me();

        return instance;
    }

    public Me() {
    }

    public Me(String name) {
        super(name);
    }

    public Me(String name, Bitmap avatar) {
        super(name, avatar);
    }

    public Me(String email, String name, Bitmap avatar){
        super(email, name, avatar);
    }

    public Me(String email, String name, Bitmap avatar, String phoneNum){
        super(email, name, avatar, phoneNum);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.email);
        parcel.writeString(this.name);
        parcel.writeValue(this.avatar);
        parcel.writeString(this.phoneNumber);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Me> CREATOR = new Parcelable.Creator<Me>() {
        public Me createFromParcel(Parcel in) {
            return new Me(in);
        }
        public Me[] newArray(int size) {
            return new Me[size];
        }
    };

    private Me(Parcel in) {
        this.email = in.readString();
        this.name = in.readString();
        this.avatar = (Bitmap) in.readValue(ClassLoader.getSystemClassLoader());
        this.phoneNumber = in.readString();
    }
}
