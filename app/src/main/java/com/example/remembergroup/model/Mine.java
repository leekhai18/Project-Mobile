package com.example.remembergroup.model;

import android.graphics.Bitmap;

/**
 * Created by chauvansang on 9/25/2017.
 */

public class Mine {
    private static Mine ourInstance;

    public static Mine getInstance() {
        if(ourInstance==null)
            ourInstance=new Mine();
        return ourInstance;
    }

    private String name;
    private String id;
    private Bitmap avatar;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    private Mine() {
    }
}
