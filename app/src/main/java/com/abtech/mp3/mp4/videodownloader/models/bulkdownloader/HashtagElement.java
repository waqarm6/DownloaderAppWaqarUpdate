package com.abtech.mp3.mp4.videodownloader.models.bulkdownloader;


import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class HashtagElement {
    private long position;
    private HashtagHashtag hashtag;

    @SerializedName("position")
    public long getPosition() {
        return position;
    }

    @SerializedName("position")
    public void setPosition(long value) {
        this.position = value;
    }

    @SerializedName("hashtag")
    public HashtagHashtag getHashtag() {
        return hashtag;
    }

    @SerializedName("hashtag")
    public void setHashtag(HashtagHashtag value) {
        this.hashtag = value;
    }
}
