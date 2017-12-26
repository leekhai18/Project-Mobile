package com.example.remembergroup.model;

import android.graphics.Bitmap;

import com.github.bassaer.chatmessageview.model.IChatUser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Khai Lee on 12/22/2017.
 */

public class IUser implements IChatUser {
    private String id;
    private String name;
    private Bitmap icon;

    public IUser(String id, String name, Bitmap icon){
        this.id = id;
        this.name = name;
        this.icon = icon;
    }


    @NotNull
    @Override
    public String getId() {
        return id;
    }

    @Nullable
    @Override
    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public Bitmap getIcon() {
        return icon;
    }

    @Override
    public void setIcon(Bitmap bitmap) {
        icon = bitmap;
    }
}
