package io.gloop.demo.instagram.model;

import android.graphics.Bitmap;

import io.gloop.GloopObject;
import io.gloop.annotations.Serializer;
import io.gloop.demo.instagram.BitmapSerializer;

/**
 * Created by Alex Untertrifaller on 21.10.16.
 */
public class Post extends GloopObject {

    private String title;
    @Serializer(BitmapSerializer.class)
    private Bitmap picture;

    public Post() {

    }

    public Post(String title, Bitmap picture) {
        this.title = title;
        this.picture = picture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }
}
