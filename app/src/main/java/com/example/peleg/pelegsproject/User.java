package com.example.peleg.pelegsproject;

public class User {
    String uid;
    String displayName;
    String urlPhoto;


    public User() {
    }

    public User(String uid, String displayName, String urlPhoto) {
        this.uid = uid;
        this.displayName = displayName;
        this.urlPhoto = urlPhoto;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

}
