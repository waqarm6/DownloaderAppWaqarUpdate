package com.abtech.mp3.mp4.videodownloader.models.bulkdownloader;

import androidx.annotation.Keep;

@Keep
public class DataInfoInsta {
    UserInfoInstaProfile user;

    public UserInfoInstaProfile getUser() {
        return this.user;
    }

    public void setUser(UserInfoInstaProfile user) {
        this.user = user;
    }
}
