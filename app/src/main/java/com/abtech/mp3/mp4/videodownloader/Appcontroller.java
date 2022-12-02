package com.abtech.mp3.mp4.videodownloader;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.multidex.MultiDexApplication;

import com.abtech.mp3.mp4.videodownloader.utils.LocaleHelper;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OneSignal;


import java.util.Locale;

public class Appcontroller extends MultiDexApplication {

    public static boolean show_social = true;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }


    @Override
    public void onCreate() {
        super.onCreate();

        MobileAds.initialize(
                this,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {
                    }
                });


        //appOpenManager = new AppOpenManager(this);

        AppLovinSdk.getInstance( this ).setMediationProvider( "max" );
        AppLovinSdk.initializeSdk( this, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration)
            {
                // AppLovin SDK is initialized, start loading ads
            }
        } );

        //firebase
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        // OneSignal Initialization
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        OneSignal.initWithContext(this);
        OneSignal.setAppId(getString(R.string.onsignalappid));

        AudienceNetworkAds.initialize(this);

        SharedPreferences prefs = getSharedPreferences("lang_pref", MODE_PRIVATE);
        // System.out.println("qqqqqqqqqqqqqqqqq = "+Locale.getDefault().getLanguage());

        String lang = prefs.getString("lang", Locale.getDefault().getLanguage());//"No name defined" is the default value.


        LocaleHelper.setLocale(getApplicationContext(), lang);

    }
}
