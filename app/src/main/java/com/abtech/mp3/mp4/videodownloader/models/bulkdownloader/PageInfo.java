package com.abtech.mp3.mp4.videodownloader.models.bulkdownloader;

import androidx.annotation.Keep;

@Keep
public class PageInfo {
    boolean has_next_page;
    String end_cursor;

    public boolean getHas_next_page() {
        return this.has_next_page;
    }

    public void setHas_next_page(boolean has_next_page) {
        this.has_next_page = has_next_page;
    }

    public String getEnd_cursor() {
        return this.end_cursor;
    }

    public void setEnd_cursor(String end_cursor) {
        this.end_cursor = end_cursor;
    }
}
