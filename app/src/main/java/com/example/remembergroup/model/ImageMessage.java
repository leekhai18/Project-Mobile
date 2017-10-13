package com.example.remembergroup.model;

import android.graphics.Bitmap;


public class ImageMessage extends Message {
    private Bitmap image;

    public Bitmap getImage() {
        return image;
    }
    public void setImage(Bitmap image) {
        this.image = image;
    }

    public ImageMessage(Bitmap image, boolean isMine) {
        super(Constant.TYPE_IMAGE_MESSAGE, isMine);
        this.image = image;
    }
}
