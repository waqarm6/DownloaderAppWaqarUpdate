package com.abtech.mp3.mp4.videodownloader.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.abtech.mp3.mp4.videodownloader.services.ClipboardMonitor;

import static com.abtech.mp3.mp4.videodownloader.utils.Constants.PREF_CLIP;

public class Receiver extends BroadcastReceiver {
    SharedPreferences.Editor editor;
    SharedPreferences prefs;

    @Override
    public void onReceive(Context context, Intent intent) {

        String whichAction = intent.getAction();
        prefs = context.getSharedPreferences(PREF_CLIP, Context.MODE_PRIVATE);
        editor = prefs.edit();
        switch (whichAction) {

            case "quit_action":
                Log.e("loged", "quite");
                editor.putBoolean("csRunning", false);
                editor.commit();
                context.stopService(new Intent(context,
                        ClipboardMonitor.class));

                return;

        }

    }
}