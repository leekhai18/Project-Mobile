package com.example.remembergroup.model;

import android.graphics.Bitmap;

/**
 * Created by chauvansang on 10/5/2017.
 */

public class ImageMessage extends Message {
    private Bitmap image;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public ImageMessage(int type, boolean isMine, Bitmap image) {
        super(type, isMine);
        this.image = image;
    }
}
