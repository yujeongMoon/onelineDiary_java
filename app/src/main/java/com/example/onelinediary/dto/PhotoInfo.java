package com.example.onelinediary.dto;

import android.graphics.Bitmap;

public class PhotoInfo {
    private String path;
    private Bitmap bitmapImage;

    public PhotoInfo() {}

    public PhotoInfo(String path, Bitmap bitmapImage) {
        this.path = path;
        this.bitmapImage = bitmapImage;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Bitmap getBitmapImage() {
        return bitmapImage;
    }

    public void setBitmapImage(Bitmap bitmapImage) {
        this.bitmapImage = bitmapImage;
    }
}
