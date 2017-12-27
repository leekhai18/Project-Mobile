package com.example.remembergroup.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Khai Lee on 10/18/2017.
 */

public class Conversation implements Parcelable {

    private String id;
    private Friend friend;
    private String lastMess;
    private String timeCreated;
    private int isMe;

    public void setId(String id) {this.id = id;}
    public String getId() {return this.id;}
    public void addFriend(Friend fr) {this.friend = fr;}
    public Friend getFriend() {return this.friend;}
    public String getLastMess() {return lastMess;}
    public void setLastMess(String content) {lastMess = content;}
    public String getTimeCreated() {return timeCreated;}
    public void setTimeCreated(String time) {timeCreated = time;}
    public int isMine() {return isMe;}
    public void setIsMe(int me) {
        isMe = me;
        if (isMe != 0){
            lastMess = "You: " + lastMess;
        }
    }

    public Conversation(){}
    public Conversation(String id){this.id = id;}
    public Conversation(String id, Friend fr) {
        this.id = id;
        this.friend = fr;
    }
    public Conversation(String id, Friend fr, String latest, String time, int mine){
        this.id = id;
        this.friend = fr;
        this.isMe = mine;
        if (mine != 0){
            this.lastMess = "You: " + latest;
        } else {
            this.lastMess = latest;
        }

        this.timeCreated = time;
    }


    // Implement Parcelable, need to putExtra
    protected Conversation(Parcel in) {
        this.id = in.readString();
        this.friend = in.readParcelable(Friend.class.getClassLoader());
        this.lastMess = in.readString();
        this.timeCreated = in.readString();
        this.isMe = in.readInt();
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
        parcel.writeString(this.lastMess);
        parcel.writeString(this.timeCreated);
        parcel.writeInt(this.isMe);
    }
}
