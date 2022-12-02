package com.abtech.mp3.mp4.videodownloader.interfaces;

public interface VideoDownloader {

    String getVideoId(String link);

    void DownloadVideo();
}
