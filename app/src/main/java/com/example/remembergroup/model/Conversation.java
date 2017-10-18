package com.example.remembergroup.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Khai Lee on 10/18/2017.
 */

public class Conversation implements Parcelable {

    private String id;
    private Friend friend;

    public void setId(String id) {this.id = id;}
    public String getId() {return this.id;}
    public void addFriend(Friend fr) {this.friend = fr;}
    public Friend getFriend() {return this.friend;}

    public Conversation(){}
    public Conversation(String id){this.id = id;}
    public Conversation(String id, Friend fr) {
        this.id = id;
        this.friend = fr;
    }


    // Implement Parcelable, need to putExtra
    protected Conversation(Parcel in) {
        this.id = in.readString();
        this.friend = in.readParcelable(Friend.class.getClassLoader());
    }

    public static final Creator<Conversation> CREATOR = new Creator<Conversation>() {
        @Override
        public Conversation createFromParcel(Parcel in) {
            return new Conversation(in);
        }

        @Override
        public Conversation[] newArray(int size) {
            return new Conversation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeParcelable(this.friend, i);
    }
}
