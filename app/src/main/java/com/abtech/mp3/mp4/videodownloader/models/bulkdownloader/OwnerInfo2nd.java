package com.abtech.mp3.mp4.videodownloader.models.bulkdownloader;


import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep
public class OwnerInfo2nd implements Serializable {
    String id;

    @SerializedName("id")
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
