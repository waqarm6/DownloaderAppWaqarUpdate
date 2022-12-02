package com.abtech.mp3.mp4.videodownloader.webservices;

import static com.abtech.mp3.mp4.videodownloader.webservices.DownloadVideosMain.fromService;
import static com.abtech.mp3.mp4.videodownloader.webservices.DownloadVideosMain.pd;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.abtech.mp3.mp4.videodownloader.R;
import com.abtech.mp3.mp4.videodownloader.interfaces.VideoDownloader;
import com.abtech.mp3.mp4.videodownloader.utils.DownloadFileMain;

import org.json.JSONObject;

import java.util.Date;

public class TwitterVideoDownloader implements VideoDownloader {

    private Context context;
    private String VideoURL;
    private String VideoTitle;

    public TwitterVideoDownloader(Context context, String videoURL) {
        this.context = context;
        VideoURL = videoURL;
    }

    private static int ordinalIndexOf(String str, String substr, int n) {
        int pos = -1;
        do {
            pos = str.indexOf(substr, pos + 1);
        } while (n-- > 0 && pos != -1);
        return pos;
    }


    @Override
    public String getVideoId(String link) {
        if (link.contains("?")) {
            link = link.split("\\?")[0];
            link = link.substring(link.indexOf("status"));
            link = link.substring(link.indexOf("/") + 1);
        } else {
            link = link.substring(link.indexOf("status"));
            link = link.substring(link.indexOf("/") + 1);
        }
        return link;
    }

    @Override
    public void DownloadVideo() {
        AndroidNetworking.post("https://twittervideodownloaderpro.com/twittervideodownloadv2/index.php")
                .addBodyParameter("id", getVideoId(VideoURL))
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Log.e("Hello", response.toString());
                            String URL = response.toString();
                            if (URL.contains("url")) {
                                URL = URL.substring(URL.indexOf("url"));
                                URL = URL.substring(ordinalIndexOf(URL, "\"", 1) + 1, ordinalIndexOf(URL, "\"", 2));
                                if (URL.contains("\\")) {
                                    URL = URL.replace("\\", "");
                                }
                                //Log.e("HelloURL",URL);
                                if (URLUtil.isValidUrl(URL)) {

                                    if (pd !=null && pd.isShowing() && !fromService && !((Activity)context).isFinishing()) {
                                        pd.dismiss();
                                    }

                                    if (VideoTitle == null || VideoTitle.equals("")) {
                                        if (URL.contains(".mp4")) {
                                            VideoTitle = "TwitterVideo" + new Date().toString() + ".mp4";
                                        } else {
                                            VideoTitle = "TwitterImage" + new Date().toString() + ".jpg";
                                        }


                                    } else {
                                        if (URL.contains(".mp4")) {
                                            VideoTitle = VideoTitle + ".mp4";
                                        } else {
                                            VideoTitle = VideoTitle + ".jpg";

                                        }
                                    }
                                    if (Looper.myLooper() == null)
                                        Looper.prepare();

                                    if (URL.contains(".mp4")) {

                                        DownloadFileMain.startDownloading(context, URL, VideoTitle, ".mp4");
                                    } else {
                                        DownloadFileMain.startDownloading(context, URL, VideoTitle, ".jpg");

                                    }
                                } else {

                                    if (pd !=null && pd.isShowing() && !fromService && !((Activity)context).isFinishing()) {
                                        pd.dismiss();
                                        Toast.makeText(context, context.getResources().getString(R.string.somthing), Toast.LENGTH_SHORT).show();

                                    }
                                    if (Looper.myLooper() == null)
                                        Looper.prepare();
                                    Toast.makeText(context, "No Video Found", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            } else {

                                if (pd !=null && pd.isShowing() && !fromService && !((Activity)context).isFinishing()) {
                                    pd.dismiss();
                                    Toast.makeText(context, context.getResources().getString(R.string.somthing), Toast.LENGTH_SHORT).show();

                                }
                                if (Looper.myLooper() == null)
                                    Looper.prepare();
                                Toast.makeText(context, "No Video Found", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            if (pd !=null && pd.isShowing() && !fromService && !((Activity)context).isFinishing()) {
                                pd.dismiss();
                                Toast.makeText(context, context.getResources().getString(R.string.somthing), Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                        if (Looper.myLooper() == null)
                            Looper.prepare();
                        Looper.loop();

                        if (pd !=null && pd.isShowing() && !fromService && !((Activity)context).isFinishing()) {
                            pd.dismiss();
                            Toast.makeText(context, context.getResources().getString(R.string.somthing), Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }
}
