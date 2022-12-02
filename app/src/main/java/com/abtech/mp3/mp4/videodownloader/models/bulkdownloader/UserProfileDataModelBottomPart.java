package com.abtech.mp3.mp4.videodownloader.models.bulkdownloader;

import androidx.annotation.Keep;

@Keep
public class UserProfileDataModelBottomPart {

    String status;
    DataInfoInsta data;

    public DataInfoInsta getData() {
        return this.data;
    }

    public void setData(DataInfoInsta data) {
        this.data = data;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}










