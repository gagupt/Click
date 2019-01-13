package com.click.gaurav.app;

import android.graphics.Bitmap;

public class ImageAdapterPojo {
    private String url;
    private Bitmap bitmap;

    public String getUrl() {
        return url;
    }

    public ImageAdapterPojo(String url, Bitmap bitmap) {
        this.url = url;
        this.bitmap = bitmap;
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

    public ImageAdapterPojo() {
    }
}