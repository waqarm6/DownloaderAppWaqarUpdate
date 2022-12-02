package com.abtech.mp3.mp4.videodownloader.models.bulkdownloader;


import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

@Keep
public class EdgeMutualFollowedBy implements Serializable {
    int count;
    List<Object> edges;

    @SerializedName("count")
    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @SerializedName("edges")
    public List<Object> getEdges() {
        return this.edges;
    }

    public void setEdges(List<Object> edges) {
        this.edges = edges;
    }
}
