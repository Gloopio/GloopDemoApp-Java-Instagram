package io.gloop.demo.instagram.model;

import android.graphics.Bitmap;

import io.gloop.GloopObject;
import io.gloop.annotations.GloopClass;

/**
 * Created by Alex Untertrifaller on 21.10.16.
 */
@GloopClass(lazyLoading = true)
public class Post extends GloopObject {

    private String message;

    // Bitmap has a default serializer to save images Base64 encoded to the database
    private Bitmap picture;

    public Post() {

    }

    public Post(String message, Bitmap picture) {
        this.message = message;
        this.picture = picture;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }
}
