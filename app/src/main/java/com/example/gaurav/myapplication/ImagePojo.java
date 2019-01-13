package com.example.gaurav.myapplication;

import android.graphics.Bitmap;

public class ImagePojo {
    private int id;
    private String key;
    private String url;
    private Bitmap bitmap;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public ImagePojo(int id, String key, String url, Bitmap bitmap) {
        this.id = id;
        this.key = key;
        this.url = url;
        this.bitmap = bitmap;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public ImagePojo() {
    }
}