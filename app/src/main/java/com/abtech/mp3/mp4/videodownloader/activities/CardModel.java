package com.abtech.mp3.mp4.videodownloader.activities;

public class CardModel {

    // string course_name for storing course_name
    // and imgid for storing image id.
    private String logo_name;
    private int logo;

    public CardModel(String course_name, int imgid) {
        this.logo_name = course_name;
        this.logo = imgid;
    }

    public String getLogo_name() {
        return logo_name;
    }

    public void setLogo_name(String course_name) {
        this.logo_name = course_name;
    }

    public int getLogo() {
        return logo;
    }

    public void setImgid(int imgid) {
        this.logo = imgid;
    }
}

