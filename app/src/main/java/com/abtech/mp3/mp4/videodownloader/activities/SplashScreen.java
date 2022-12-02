package com.abtech.mp3.mp4.videodownloader.activities;


import static com.abtech.mp3.mp4.videodownloader.utils.AdsManager.loadAdmobInterstitialAd;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.abtech.mp3.mp4.videodownloader.R;
import com.abtech.mp3.mp4.videodownloader.databinding.ActivitySplashScreenBinding;
import com.abtech.mp3.mp4.videodownloader.utils.Constants;

import com.abtech.mp3.mp4.videodownloader.utils.LocaleHelper;
import com.github.appintro.AppIntroCustomLayoutFragment;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SplashScreen extends AppCompatActivity {

    private ActivitySplashScreenBinding binding;
    CardView start;
    ProgressBar loading;
    private InterstitialAd minterad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        SharedPreferences prefs = getSharedPreferences(
                "whatsapp_pref",
                Context.MODE_PRIVATE);

        String nn = prefs.getString("inappads", "nnn");

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                for (String adapterClass : statusMap.keySet()) {
                    AdapterStatus status = statusMap.get(adapterClass);
                    Log.d("MyApp", String.format(
                            "Adapter name: %s, Description: %s, Latency: %d",
                            adapterClass, status.getDescription(), status.getLatency()));
                }

                // Start loading ads here...
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,getString(R.string.splash_interstitial), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The minterad reference will be null until
                        // an ad is loaded.
                        minterad = interstitialAd;
                        //Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                       // Log.d(TAG, loadAdError.toString());
                        minterad = null;
                    }
                });



        //AppIntroCustomLayoutFragment.newInstance(R.layout.intro1);

        start = findViewById(R.id.start);
        loading = findViewById(R.id.loading);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                loading.setVisibility(View.GONE);
                start.setVisibility(View.VISIBLE);

            }
        }, 5000);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (minterad != null) {

                    if (nn.equals("nnn")) {

                        minterad.show(SplashScreen.this);
                    }

                    minterad.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdClicked() {
                            // Called when a click is recorded for an ad.
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Called when ad is dismissed.
                            // Set the ad reference to null so you don't show the ad a second time.
                            minterad = null;

                            SharedPreferences prefs = getSharedPreferences("intro", MODE_PRIVATE);
                            Boolean name = prefs.getBoolean("intro", false);
                            if (name) {
                                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                            } else {
                                startActivity(new Intent(SplashScreen.this, AppIntro.class));
                            }
                            finish();

                            loadAdmobInterstitialAd(SplashScreen.this);
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            // Called when ad fails to show.
                            minterad = null;
                        }

                        @Override
                        public void onAdImpression() {
                            // Called when an impression is recorded for an ad.
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            // Called when ad is shown.
                        }
                    });

                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");

                    SharedPreferences prefs = getSharedPreferences("intro", MODE_PRIVATE);
                    Boolean name = prefs.getBoolean("intro", false);
                    if (name) {
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    } else {
                        startActivity(new Intent(SplashScreen.this, AppIntro.class));
                    }
                    finish();

                }
            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = LocaleHelper.onAttach(newBase);
        super.attachBaseContext(newBase);
    }
}




