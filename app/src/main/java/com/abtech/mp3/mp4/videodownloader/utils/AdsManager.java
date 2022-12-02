package com.abtech.mp3.mp4.videodownloader.utils;


import static com.abtech.mp3.mp4.videodownloader.utils.SharedPrefsForInstagram.context;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.abtech.mp3.mp4.videodownloader.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

public class AdsManager {

    //TODO change this if you want to limit interstishal ads
    public static int NUMBER_OF_INTERSTISHAL_ADS_PER_SESSION = 10;
    //TODO dont touch this
    public static int NUMBER_OF_INTERSTISHAL_ADS_SHOWN = 0;


    //TODO change this if you want to limit banner ads
    public static int NUMBER_OF_BANNER_ADS_PER_SESSION = 5;
    //TODO dont touch this
    public static int NUMBER_OF_BANNER_ADS_SHOWN = 0;


    public  static com.google.android.gms.ads.interstitial.InterstitialAd mInterstitialAd;

    public static MaxInterstitialAd interstitialAd;

    public static MaxNativeAdLoader nativeAdLoader;
    public static MaxAd nativeAd;




    public static void loadInterstitialAd(final Activity activity) {

        loadAdmobInterstitialAd(activity);


    }


    public static void loadBannerAd(final Activity activity, final ViewGroup adContainer) {


                com.google.android.gms.ads.AdView adView = new com.google.android.gms.ads.AdView(activity);
                adView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
                adView.setAdUnitId(activity.getResources().getString(R.string.AdmobBanner));
                adView.loadAd(new AdRequest.Builder().build());
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {


            MaxAdView adView = new MaxAdView( activity.getString(R.string.applovinbanner), activity );
            //adView.setListener( this );

            // Stretch to the width of the screen for banners to be fully functional
            int width = ViewGroup.LayoutParams.MATCH_PARENT;

            // Banner height on phones and tablets is 50 and 90, respectively
            int heightPx = activity.getResources().getDimensionPixelSize( R.dimen.banner_height );

            adView.setLayoutParams( new FrameLayout.LayoutParams( width, heightPx ) );

            // Set background or background color for banners to be fully functional
            //adView.setBackgroundColor( R.color.background_color );

           // ViewGroup rootView = findViewById( android.R.id.content );
            adContainer.addView( adView );

            // Load the ad
            adView.loadAd();

            }

            @Override
            public void onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                adContainer.addView(adView);

            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        });

    }


    public static void loadAdmobInterstitialAd(final Context context) {


        AdRequest adRequest = new AdRequest.Builder().build();

        com.google.android.gms.ads.interstitial.InterstitialAd.load(context, context.getString(R.string.AdmobInterstitial), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                    mInterstitialAd = interstitialAd;
//                    if (mInterstitialAd != null) {
//                        mInterstitialAd.show((Activity) context);
//                    }
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mInterstitialAd = null;
            }
        });


        interstitialAd = new MaxInterstitialAd(context.getString(R.string.applovininterstitial), (Activity) context);

        // Load the first ad
        interstitialAd.loadAd();

    }



    @SuppressLint("MissingPermission")
    public static void loadAdmobNativeAd(Context context, FrameLayout frameLayout) {

            /*AdLoader.Builder builder = new AdLoader.Builder(context, context.getString(R.string.AdmobNative));
            builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                @Override
                public void onNativeAdLoaded(NativeAd nativeAd) {
                    // Assumes you have a placeholder FrameLayout in your View layout
                    // (with id fl_adplaceholder) where the ad is to be placed.

                    // Assumes that your ad layout is in a file call native_ad_layout.xml
                    // in the res/layout folder
                    NativeAdView adView = (NativeAdView) ((Activity)context).getLayoutInflater()
                            .inflate(R.layout.layout_native_ads, null);
                    // This method sets the text, images and the native ad, etc into the ad
                    // view.
                    populateNativeAdView(nativeAd, adView);
                    frameLayout.removeAllViews();
                    frameLayout.addView(adView);
                }
            });


            VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();

            NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

            builder.withNativeAdOptions(adOptions);

            AdLoader adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    Log.e("adload", "adload faild $error");

                }
            }).build();

            adLoader.loadAd(new AdRequest.Builder().build());
        } catch (Exception e) {
            e.printStackTrace();
        }*/

         /*   nativeAdLoader = new MaxNativeAdLoader(context.getString(R.string.applovin_native), (Activity) context);
            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                @Override
                public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                    // Clean up any pre-existing native ad to prevent memory leaks.
                    if (nativeAd != null) {
                        nativeAdLoader.destroy(nativeAd);
                    }

                    // Save ad for cleanup.
                    nativeAd = ad;

                    // Add ad view to view.
                    frameLayout.removeAllViews();
                    frameLayout.addView(nativeAdView);
                }

                @Override
                public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                    // We recommend retrying with exponentially higher delays up to a maximum delay

                    Log.i("native",error.getMessage());
                }

                @Override
                public void onNativeAdClicked(final MaxAd ad) {
                    // Optional click callback
                }
            });


            nativeAdLoader.loadAd();*/

    }


}