@file:Suppress("DEPRECATION")

package com.abtech.mp3.mp4.videodownloader.activities

import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.abtech.mp3.mp4.videodownloader.R
import com.abtech.mp3.mp4.videodownloader.models.instawithlogin.CarouselMedia
import com.abtech.mp3.mp4.videodownloader.models.instawithlogin.ModelInstaWithLogin
import com.abtech.mp3.mp4.videodownloader.models.storymodels.ModelEdNode
import com.abtech.mp3.mp4.videodownloader.models.storymodels.ModelGetEdgetoNode
import com.abtech.mp3.mp4.videodownloader.models.storymodels.ModelInstagramResponse
import com.abtech.mp3.mp4.videodownloader.services.ClipboardMonitor
import com.abtech.mp3.mp4.videodownloader.snapchatstorysaver.SnapChatBulkStoryDownloader
import com.abtech.mp3.mp4.videodownloader.utils.*
import com.abtech.mp3.mp4.videodownloader.utils.AdsManager.*
import com.abtech.mp3.mp4.videodownloader.utils.Constants.counter
import com.abtech.mp3.mp4.videodownloader.utils.iUtils.ShowToast
import com.abtech.mp3.mp4.videodownloader.webservices.DownloadVideosMain
import com.abtech.mp3.mp4.videodownloader.webservices.api.RetrofitApiInterface
import com.abtech.mp3.mp4.videodownloader.webservices.api.RetrofitClient
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError

import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.tabs.TabLayout
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.InstallStatus
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.suddenh4x.ratingdialog.AppRating
import com.suddenh4x.ratingdialog.preferences.RatingThreshold
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.apache.commons.lang3.StringEscapeUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.lang.reflect.Type
import java.net.URI
import java.util.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    val REQUEST_PERMISSION_CODE = 1001
    val REQUEST_PERMISSION = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    private var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    var fbAdView: com.facebook.ads.AdView? = null
    lateinit var chkAutoDownload: SwitchCompat
    lateinit var instaprivatefbprivate: CardView
    lateinit var fbprivatefbprivate: CardView
    lateinit var darkmode   : CardView
    lateinit var socialcard : CardView
    lateinit var ad_frame : FrameLayout
    var myString: String? = ""

    lateinit var progressDralogGenaratinglink: ProgressDialog

    private val APP_UPDATE_REQUEST_CODE = 261

    private var billingClient: BillingClient? = null
    private var skuDetails: SkuDetails? = null

    var fragment: Fragment? = null
    lateinit var adLoader: AdLoader
    lateinit var template: TemplateView


    lateinit var prefEditor: SharedPreferences.Editor
    lateinit var pref: SharedPreferences
    private var nn: String? = "nnn"
    //  private var mRewardedAd: RewardedAd? = null
    private var csRunning = false
    var myVideoUrlIs: String? = null
    var myInstaUsername: String? = ""

    private val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private val appUpdatedListener: InstallStateUpdatedListener by lazy {
        object : InstallStateUpdatedListener {
            override fun onStateUpdate(installState: InstallState) {
                when {
                    //installState.installStatus() == InstallStatus.DOWNLOADED -> popupSnackbarForCompleteUpdate()
                    installState.installStatus() == InstallStatus.INSTALLED -> appUpdateManager.unregisterListener(
                        this
                    )
                    else -> Log.d(
                        "pdatedListener: %s",
                        installState.installStatus().toString()
                    )
                }
            }
        }
    }

    var myPhotoUrlIs: String? = null
    //private var rewardedInterstitialAd: RewardedInterstitialAd? = null

    private var retryAttempt = 0.0

    lateinit var noads: ImageView


    @SuppressLint("MissingPermission", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val prefs1 = getSharedPreferences("lang_pref", Context.MODE_PRIVATE)
        // System.out.println("qqqqqqqqqqqqqqqqq = "+Locale.getDefault().getLanguage());

        // System.out.println("qqqqqqqqqqqqqqqqq = "+Locale.getDefault().getLanguage());
        val lang = prefs1.getString(
            "lang",
            Locale.getDefault().language
        ) //"No name defined" is the default value.



        LocaleHelper.setLocale(applicationContext, lang)

        setContentView(R.layout.activity_main_latest_design)


        adLoader = AdLoader.Builder(this@MainActivity, getString(R.string.admob_native))
            .forNativeAd(NativeAd.OnNativeAdLoadedListener { nativeAd ->

              //  val ad_text = findViewById<TextView>(R.id.ad_text);
              //  ad_text.visibility = View.VISIBLE
                template = findViewById(R.id.my_template)
                template.visibility = View.VISIBLE
                template.setNativeAd(nativeAd)
            })
            .build()

         adLoader.loadAd(AdRequest.Builder().build())

        loadAdmobInterstitialAd(this)

// Write a message to the database
        // Write a message to the database
        // Write a message to the database
        //val database = FirebaseDatabase.getInstance()
        //       val database = Firebase.database

//        val myRef = database.getReference("downloader")
//
//        // Read from the database
//        myRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                show_fb = dataSnapshot.child("show_social").getValue<Boolean>()
//                if(show_fb == true){
//                    socialcard.visibility = View.VISIBLE
//                }
//                Log.d("show_fb", "Value is: $show_fb")
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//               // Log.w(TAG, "Failed to read value.", error.toException())
//            }
//        })
        // setupBillingClient()


        // setSupportActionBar(toolbar)
        // supportActionBar?.setDisplayHomeAsUpEnabled(false)
        try {
            setUpBillingClient1()
//0,2
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val prefs = getSharedPreferences(
            "whatsapp_pref",
            Context.MODE_PRIVATE
        )
        nn = prefs!!.getString("inappads", "nnn")


        //fb add

        progressDralogGenaratinglink = ProgressDialog(this@MainActivity)
        progressDralogGenaratinglink.setMessage(resources.getString(R.string.genarating_download_link))
        progressDralogGenaratinglink.setCancelable(false)
        progressDralogGenaratinglink.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
            DialogInterface.OnClickListener { dialog, which ->
                progressDralogGenaratinglink.dismiss() //dismiss dialog
            })

        //  addFbAd()

        pref = getSharedPreferences(Constants.PREF_CLIP, 0) // 0 - for private mode
        prefEditor = pref.edit()
        csRunning = pref.getBoolean("csRunning", false)

        createNotificationChannel(
            NotificationManagerCompat.IMPORTANCE_LOW,
            true,
            getString(R.string.app_name),
            getString(R.string.aio_auto)
        )
//TODO

        val nativeadContainer = findViewById<FrameLayout>(R.id.fl_adplaceholder)


        val action = intent.action
        val type = intent.type

        if (Intent.ACTION_SEND == action && type != null) {
            if ("text/plain" == type) {
                handleSendText(intent) // Handle text being sent


            }
        }


        //chkAutoDownload = findViewById<SwitchCompat>(R.id.switchddService)

        //  val pinterest = findViewById<CardView>(R.id.pinterestcard)
        //  val autodownload    = findViewById<CardView>(R.id.autodownload)
        val copylinktext    = findViewById<TextView>(R.id.copylinktext)
        //  val autodownloadtext    = findViewById<TextView>(R.id.autodownloadtext)
        val mainlayout    = findViewById<RelativeLayout>(R.id.main_layout)
        val homeimage     = findViewById<ImageView>(R.id.homeimage)
        val dashboardimage     = findViewById<ImageView>(R.id.dashboardimage)
        val galleryimage     = findViewById<ImageView>(R.id.galleryimage)
        val settingimage     = findViewById<ImageView>(R.id.settingimage)
        val barbackground    = findViewById<LinearLayout>(R.id.ly)
//        val facebookcard    = findViewById<CardView>(R.id.card_facebook)
//        val whatsappcard = findViewById<CardView>(R.id.card_whatsapp)
//        val instcard = findViewById<CardView>(R.id.card_insta)
//        val snapcard = findViewById<CardView>(R.id.card_snap)
//        val tiktokcard = findViewById<CardView>(R.id.card_tiktok)
//        val twittercard = findViewById<CardView>(R.id.card_twitter)
      //    val gridview   =findViewById<GridView>(R.id.logogrid)


//        val courseModelArrayList: ArrayList<CardModel> = ArrayList<CardModel>()
//
//        courseModelArrayList.add(CardModel("Facebook", getResources().getIdentifier("fb_icon", "drawable", getPackageName()) ))
//        courseModelArrayList.add(CardModel("WhatsApp", getResources().getIdentifier("whatsapp_icon", "drawable", getPackageName()) ))
//        courseModelArrayList.add(CardModel("Instagram", getResources().getIdentifier("insta_icon", "drawable", getPackageName()) ))
//        courseModelArrayList.add(CardModel("SnapChat", getResources().getIdentifier("snapchat_icon1", "drawable", getPackageName()) ))
//        courseModelArrayList.add(CardModel("TikTok", getResources().getIdentifier("tiktok_icon", "drawable", getPackageName()) ))
//        courseModelArrayList.add(CardModel("Twitter", getResources().getIdentifier("twitter_icon", "drawable", getPackageName()) ))


//        courseModelArrayList.add(CardModel("WhatsApp", android.R.drawable.whatsapp_icon))
//        courseModelArrayList.add(CardModel("Instagram", android.R.drawable.insta_icon))
//        courseModelArrayList.add(CardModel("SnapChat", android.R.drawable.snapchat_icon1))
//        courseModelArrayList.add(CardModel("TikTok", android.R.drawable.tiktok_icon))
//        courseModelArrayList.add(CardModel("Twitter", android.R.drawable.twitter_icon))

//        val adapter = CardAdapter(this, courseModelArrayList)
//        gridview.setAdapter(adapter)
//
//
//        gridview.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
//            // inside on click method we are simply displaying
//            // a toast message with course name.
//            when (position) {
//                0 -> {
//                    if (mInterstitialAd != null) {
//                    if (nn == "nnn") {
//
//                        mInterstitialAd.show(this@MainActivity)
//
//                    }
//
//                    mInterstitialAd.fullScreenContentCallback =
//                        object : FullScreenContentCallback() {
//                            override fun onAdClicked() {
//                                // Called when a click is recorded for an ad.
//                            }
//
//                            override fun onAdDismissedFullScreenContent() {
//                                // Called when ad is dismissed.
//                                // Set the ad reference to null so you don't show the ad a second time.
//                                mInterstitialAd = null
//
//                                val apppackage = "com.facebook.katana"
//                                val cx: Context = this@MainActivity
//                                try {
//                                    val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
//                                    cx.startActivity(i)
//                                } catch (e: java.lang.Exception) {
//                                    Toast.makeText(this@MainActivity, "Sorry, Facebook App Not Found", Toast.LENGTH_LONG).show()
//                                }
//
//                                loadInterstitialAd(this@MainActivity)
//
//                            }
//
//                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
//                                // Called when ad fails to show.
//                                mInterstitialAd = null
//                            }
//
//                            override fun onAdImpression() {
//                                // Called when an impression is recorded for an ad.
//                            }
//
//                            override fun onAdShowedFullScreenContent() {
//                                // Called when ad is shown.
//                            }
//                        }
//
//                } else {
//                    val apppackage = "com.facebook.katana"
//                    val cx: Context = this
//                    try {
//                        val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
//                        cx.startActivity(i)
//                    } catch (e: java.lang.Exception) {
//                        Toast.makeText(this, "Sorry, Facebook App Not Found", Toast.LENGTH_LONG).show()
//                    }
//
//                }
//
//                }
//                1 -> {
//
//
//                    if (mInterstitialAd != null) {
//                        if (nn == "nnn") {
//
//                            mInterstitialAd.show(this@MainActivity)
//
//                        }
//
//                        mInterstitialAd.fullScreenContentCallback =
//                            object : FullScreenContentCallback() {
//                                override fun onAdClicked() {
//                                    // Called when a click is recorded for an ad.
//                                }
//
//                                override fun onAdDismissedFullScreenContent() {
//                                    // Called when ad is dismissed.
//                                    // Set the ad reference to null so you don't show the ad a second time.
//                                    mInterstitialAd = null
//
//                                    val intent =
//                                        Intent(this@MainActivity, StatusSaverActivity::class.java)
//                                    intent.putExtra("frag", "status")
//
//                                    startActivity(intent)
//
//                                    loadInterstitialAd(this@MainActivity)
//
//                                }
//
//                                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
//                                    // Called when ad fails to show.
//                                    mInterstitialAd = null
//                                }
//
//                                override fun onAdImpression() {
//                                    // Called when an impression is recorded for an ad.
//                                }
//
//                                override fun onAdShowedFullScreenContent() {
//                                    // Called when ad is shown.
//                                }
//                            }
//
//                    } else {
//                        val intent = Intent(this, StatusSaverActivity::class.java)
//                        intent.putExtra("frag", "status")
//
//                        startActivity(intent)
//                    }
//            }
//
//                2->{
//
//                    if (mInterstitialAd != null) {
//                    if (nn == "nnn") {
//
//                        mInterstitialAd.show(this@MainActivity)
//
//                    }
//
//                    mInterstitialAd.fullScreenContentCallback =
//                        object : FullScreenContentCallback() {
//                            override fun onAdClicked() {
//                                // Called when a click is recorded for an ad.
//                            }
//
//                            override fun onAdDismissedFullScreenContent() {
//                                // Called when ad is dismissed.
//                                // Set the ad reference to null so you don't show the ad a second time.
//                                mInterstitialAd = null
//
//                                val apppackage = "com.instagram.android"
//                                val cx: Context = this@MainActivity
//                                try {
//                                    val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
//                                    cx.startActivity(i)
//                                } catch (e: java.lang.Exception) {
//                                    Toast.makeText(this@MainActivity, "Sorry, Instagram App Not Found", Toast.LENGTH_LONG).show()
//                                }
//
//                                loadInterstitialAd(this@MainActivity)
//
//                            }
//
//                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
//                                // Called when ad fails to show.
//                                mInterstitialAd = null
//                            }
//
//                            override fun onAdImpression() {
//                                // Called when an impression is recorded for an ad.
//                            }
//
//                            override fun onAdShowedFullScreenContent() {
//                                // Called when ad is shown.
//                            }
//                        }
//
//                } else {
//                    val apppackage = "com.instagram.android"
//                    val cx: Context = this
//                    try {
//                        val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
//                        cx.startActivity(i)
//                    } catch (e: java.lang.Exception) {
//                        Toast.makeText(this, "Sorry, Instagram App Not Found", Toast.LENGTH_LONG).show()
//                    }
//
//                }
//
//                }
//
//                3->{
//
//                    if (mInterstitialAd != null) {
//                    if (nn == "nnn") {
//
//                        mInterstitialAd.show(this@MainActivity)
//
//                    }
//
//                    mInterstitialAd.fullScreenContentCallback =
//                        object : FullScreenContentCallback() {
//                            override fun onAdClicked() {
//                                // Called when a click is recorded for an ad.
//                            }
//
//                            override fun onAdDismissedFullScreenContent() {
//                                // Called when ad is dismissed.
//                                // Set the ad reference to null so you don't show the ad a second time.
//                                mInterstitialAd = null
//
//                                val apppackage = "com.snapchat.android"
//                                val cx: Context = this@MainActivity
//                                try {
//                                    val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
//                                    cx.startActivity(i)
//                                } catch (e: java.lang.Exception) {
//                                    Toast.makeText(this@MainActivity, "Sorry, SnapChat App Not Found", Toast.LENGTH_LONG).show()
//                                }
//
//                                loadInterstitialAd(this@MainActivity)
//
//                            }
//
//                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
//                                // Called when ad fails to show.
//                                mInterstitialAd = null
//                            }
//
//                            override fun onAdImpression() {
//                                // Called when an impression is recorded for an ad.
//                            }
//
//                            override fun onAdShowedFullScreenContent() {
//                                // Called when ad is shown.
//                            }
//                        }
//
//                } else {
//                    val apppackage = "com.snapchat.android"
//                    val cx: Context = this
//                    try {
//                        val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
//                        cx.startActivity(i)
//                    } catch (e: java.lang.Exception) {
//                        Toast.makeText(this, "Sorry, SnapChat App Not Found", Toast.LENGTH_LONG).show()
//                    }
//
//                }
//
//                }
//
//                4->{
//
//                    if (mInterstitialAd != null) {
//                    if (nn == "nnn") {
//
//                        mInterstitialAd.show(this@MainActivity)
//
//                    }
//
//                    mInterstitialAd.fullScreenContentCallback =
//                        object : FullScreenContentCallback() {
//                            override fun onAdClicked() {
//                                // Called when a click is recorded for an ad.
//                            }
//
//                            override fun onAdDismissedFullScreenContent() {
//                                // Called when ad is dismissed.
//                                // Set the ad reference to null so you don't show the ad a second time.
//                                mInterstitialAd = null
//
//                                val apppackage = "com.zhiliaoapp.musically"
//                                val cx: Context = this@MainActivity
//                                try {
//                                    val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
//                                    cx.startActivity(i)
//                                } catch (e: java.lang.Exception) {
//                                    Toast.makeText(this@MainActivity, "Sorry, TikTok App Not Found", Toast.LENGTH_LONG).show()
//                                }
//
//                                loadInterstitialAd(this@MainActivity)
//
//                            }
//
//                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
//                                // Called when ad fails to show.
//                                mInterstitialAd = null
//                            }
//
//                            override fun onAdImpression() {
//                                // Called when an impression is recorded for an ad.
//                            }
//
//                            override fun onAdShowedFullScreenContent() {
//                                // Called when ad is shown.
//                            }
//                        }
//
//                } else {
//                    val apppackage = "com.zhiliaoapp.musically"
//                    val cx: Context = this
//                    try {
//                        val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
//                        cx.startActivity(i)
//                    } catch (e: java.lang.Exception) {
//                        Toast.makeText(this, "Sorry, TikTok App Not Found", Toast.LENGTH_LONG).show()
//                    }
//                }
//                }
//                5->{
//                    if (mInterstitialAd != null) {
//                    if (nn == "nnn") {
//
//                        mInterstitialAd.show(this@MainActivity)
//
//                    }
//
//                    mInterstitialAd.fullScreenContentCallback =
//                        object : FullScreenContentCallback() {
//                            override fun onAdClicked() {
//                                // Called when a click is recorded for an ad.
//                            }
//
//                            override fun onAdDismissedFullScreenContent() {
//                                // Called when ad is dismissed.
//                                // Set the ad reference to null so you don't show the ad a second time.
//                                mInterstitialAd = null
//
//                                val apppackage = "com.twitter.android"
//                                val cx: Context = this@MainActivity
//                                try {
//                                    val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
//                                    cx.startActivity(i)
//                                } catch (e: java.lang.Exception) {
//                                    Toast.makeText(this@MainActivity, "Sorry, Twitter App Not Found", Toast.LENGTH_LONG).show()
//                                }
//
//                                loadInterstitialAd(this@MainActivity)
//
//                            }
//
//                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
//                                // Called when ad fails to show.
//                                mInterstitialAd = null
//                            }
//
//                            override fun onAdImpression() {
//                                // Called when an impression is recorded for an ad.
//                            }
//
//                            override fun onAdShowedFullScreenContent() {
//                                // Called when ad is shown.
//                            }
//                        }
//
//                } else {
//                    val apppackage = "com.twitter.android"
//                    val cx: Context = this
//                    try {
//                        val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
//                        cx.startActivity(i)
//                    } catch (e: java.lang.Exception) {
//                        Toast.makeText(this, "Sorry, Twitter App Not Found", Toast.LENGTH_LONG).show()
//                    }
//                }
//
//                }
//
//            }
//        }

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q){
//
//            autodownload.setVisibility(View.GONE)
//        }

        when (resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                copylinktext.setTextColor(getResources().getColor(R.color.white))
                //  autodownloadtext.setTextColor(getResources().getColor(R.color.white))
                //socialtext.setTextColor(getResources().getColor(R.color.white))
                mainlayout.setBackgroundColor(getResources().getColor(R.color.black))
                homeimage.setColorFilter(Color.argb(255, 255, 255, 255));
                dashboardimage.setColorFilter(Color.argb(255, 255, 255, 255));
                galleryimage.setColorFilter(Color.argb(255, 255, 255, 255));
                settingimage.setColorFilter(Color.argb(255, 255, 255, 255));
                barbackground.setBackgroundColor(getResources().getColor(R.color.black))

            }
            Configuration.UI_MODE_NIGHT_NO -> {}
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {}
        }
        val copylinkanddownloadcard = findViewById<LinearLayout>(R.id.copylinkanddownloadcard)

        val videwGllery = findViewById<LinearLayout>(R.id.videwGllery)


        videwGllery.setOnClickListener {
            counter++

            if(counter%2 == 0) {

                if (mInterstitialAd != null) {

                    if (nn == "nnn") {
                        mInterstitialAd.show(this@MainActivity)
                    }

                    mInterstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdClicked() {
                            // Called when a click is recorded for an ad.
                        }

                        override fun onAdDismissedFullScreenContent() {
                            // Called when ad is dismissed.
                            // Set the ad reference to null so you don't show the ad a second time.
                            mInterstitialAd = null

                            val intent = Intent(this@MainActivity, GalleryActivity::class.java)
                            startActivity(intent)
                            loadInterstitialAd(this@MainActivity)

                        }

                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                            // Called when ad fails to show.
                            mInterstitialAd = null
                        }

                        override fun onAdImpression() {
                            // Called when an impression is recorded for an ad.
                        }

                        override fun onAdShowedFullScreenContent() {
                            // Called when ad is shown.
                        }
                    }

                } else if  ( interstitialAd.isReady )
                {
                    interstitialAd.showAd()

                    interstitialAd.setListener(object : MaxAdListener {
                        override fun onAdLoaded(ad: MaxAd) {
                            if (interstitialAd.isReady) {
                                // interstitialAd.showAd();
                            }
                        }

                        override fun onAdDisplayed(ad: MaxAd) {

                        }
                        override fun onAdHidden(ad: MaxAd) {

                            interstitialAd.loadAd();

                            val intent = Intent(this@MainActivity, GalleryActivity::class.java)
                            startActivity(intent)

                        }

                        override fun onAdClicked(ad: MaxAd) {}
                        override fun onAdLoadFailed(adUnitId: String, error: MaxError) {}
                        override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {}
                    })
                } else {

                    val intent = Intent(this, GalleryActivity::class.java)
                    startActivity(intent)

                }

            }
            else
            {
                val intent = Intent(this, GalleryActivity::class.java)
                startActivity(intent)


            }
        }


        val settingpage = findViewById<LinearLayout>(R.id.settingpage)

        settingpage.setOnClickListener {
            counter++

            if(counter%2 == 0) {

                if (mInterstitialAd != null) {
                    if (nn == "nnn") {

                        mInterstitialAd.show(this@MainActivity)

                    }

                    mInterstitialAd.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdClicked() {
                                // Called when a click is recorded for an ad.
                            }

                            override fun onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                mInterstitialAd = null

                                val intent = Intent(this@MainActivity, SettingActivity::class.java)
                                startActivity(intent)
                                loadInterstitialAd(this@MainActivity)

                            }

                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                // Called when ad fails to show.
                                mInterstitialAd = null
                            }

                            override fun onAdImpression() {
                                // Called when an impression is recorded for an ad.
                            }

                            override fun onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                            }
                        }

                } else if  ( interstitialAd.isReady )
                {
                    interstitialAd.showAd()

                    interstitialAd.setListener(object : MaxAdListener {
                        override fun onAdLoaded(ad: MaxAd) {
                            if (interstitialAd.isReady) {
                                // interstitialAd.showAd();
                            }
                        }

                        override fun onAdDisplayed(ad: MaxAd) {

                        }
                        override fun onAdHidden(ad: MaxAd) {

                            interstitialAd.loadAd();

                            val intent = Intent(this@MainActivity, SettingActivity::class.java)
                            startActivity(intent)

                        }

                        override fun onAdClicked(ad: MaxAd) {}
                        override fun onAdLoadFailed(adUnitId: String, error: MaxError) {}
                        override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {}
                    })
                } else {
                    val intent = Intent(this, SettingActivity::class.java)
                    startActivity(intent)

                }
            }
            else{
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
            }

        }

        noads = findViewById<ImageView>(R.id.noads)

        noads.setOnClickListener{

            //removemyadshere()
        }

        val language    = findViewById<ImageView>(R.id.language)

        language.setOnClickListener{

            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.dialog_change_language)

            val l_english = dialog.findViewById(R.id.l_english) as TextView
            l_english.setOnClickListener {

                LocaleHelper.setLocale(application, "en")
                window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL


                val editor: SharedPreferences.Editor = getSharedPreferences(
                    "lang_pref",
                    Context.MODE_PRIVATE
                ).edit()
                editor.putString("lang", "en")

                editor.apply()


                recreate()
                dialog.dismiss()
            }


            val l_arabic = dialog.findViewById(R.id.l_arabic) as TextView
            l_arabic.setOnClickListener {
                LocaleHelper.setLocale(application, "ar")
                window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL

                val editor: SharedPreferences.Editor = getSharedPreferences(
                    "lang_pref",
                    Context.MODE_PRIVATE
                ).edit()
                editor.putString("lang", "ar")

                editor.apply()


                recreate()
                dialog.dismiss()

            }

            val l_hindi = dialog.findViewById(R.id.l_hindi) as TextView
            l_hindi.setOnClickListener {
                LocaleHelper.setLocale(application, "hi")
                window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
                val editor: SharedPreferences.Editor = getSharedPreferences(
                    "lang_pref",
                    Context.MODE_PRIVATE
                ).edit()
                editor.putString("lang", "hi")

                editor.apply()

                recreate()
                dialog.dismiss()
            }
            /*  val l_urdu = dialog.findViewById(R.id.l_urdu) as TextView
              l_urdu.setOnClickListener {
                  LocaleHelper.setLocale(application, "ur")
                  window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR


                  val editor: SharedPreferences.Editor = getSharedPreferences(
                      "lang_pref",
                      Context.MODE_PRIVATE
                  ).edit()
                  editor.putString("lang", "ur")

                  editor.apply()


                  recreate()
                  dialog.dismiss()
              }

              val l_french = dialog.findViewById(R.id.l_french) as TextView
              l_french.setOnClickListener {
                  LocaleHelper.setLocale(application, "fr")
                  window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
                  val editor: SharedPreferences.Editor = getSharedPreferences(
                      "lang_pref",
                      Context.MODE_PRIVATE
                  ).edit()
                  editor.putString("lang", "fr")

                  editor.apply()


                  recreate()
                  dialog.dismiss()
              }

              val l_german = dialog.findViewById(R.id.l_german) as TextView
              l_german.setOnClickListener {
                  LocaleHelper.setLocale(application, "de")
                  window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
                  val editor: SharedPreferences.Editor = getSharedPreferences(
                      "lang_pref",
                      Context.MODE_PRIVATE
                  ).edit()
                  editor.putString("lang", "de")

                  editor.apply()


                  recreate()
                  dialog.dismiss()
              }


              val l_turkey = dialog.findViewById(R.id.l_turkey) as TextView
              l_turkey.setOnClickListener {
                  LocaleHelper.setLocale(application, "tr")
                  window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
                  val editor: SharedPreferences.Editor = getSharedPreferences(
                      "lang_pref",
                      Context.MODE_PRIVATE
                  ).edit()
                  editor.putString("lang", "tr")

                  editor.apply()


                  recreate()
                  dialog.dismiss()
              }


              val l_portougese = dialog.findViewById(R.id.l_portougese) as TextView
              l_portougese.setOnClickListener {
                  LocaleHelper.setLocale(application, "pt")
                  window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
                  val editor: SharedPreferences.Editor = getSharedPreferences(
                      "lang_pref",
                      Context.MODE_PRIVATE
                  ).edit()
                  editor.putString("lang", "pt")

                  editor.apply()


                  recreate()
                  dialog.dismiss()
              }


              val l_chinese = dialog.findViewById(R.id.l_chinese) as TextView
              l_chinese.setOnClickListener {
                  LocaleHelper.setLocale(application, "zh")
                  window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
                  val editor: SharedPreferences.Editor = getSharedPreferences(
                      "lang_pref",
                      Context.MODE_PRIVATE
                  ).edit()
                  editor.putString("lang", "zh")

                  editor.apply()


                  recreate()
                  dialog.dismiss()
              }*/

            dialog.show()

            true

        }

        val apps    = findViewById<LinearLayout>(R.id.apps)

        apps.setOnClickListener {

            counter++

            if(counter%2 == 0) {

                if (mInterstitialAd != null) {
                    if (nn == "nnn") {

                        mInterstitialAd.show(this@MainActivity)

                    }

                    mInterstitialAd.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdClicked() {
                                // Called when a click is recorded for an ad.
                            }

                            override fun onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                mInterstitialAd = null

                                val intent = Intent(this@MainActivity, SupportedApps::class.java)
                                startActivity(intent)
                                loadInterstitialAd(this@MainActivity)

                            }

                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                // Called when ad fails to show.
                                mInterstitialAd = null
                            }

                            override fun onAdImpression() {
                                // Called when an impression is recorded for an ad.
                            }

                            override fun onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                            }
                        }

                }  else if  ( interstitialAd.isReady )
                {
                    interstitialAd.showAd()

                    interstitialAd.setListener(object : MaxAdListener {
                        override fun onAdLoaded(ad: MaxAd) {
                            if (interstitialAd.isReady) {
                                // interstitialAd.showAd();
                            }
                        }

                        override fun onAdDisplayed(ad: MaxAd) {

                        }
                        override fun onAdHidden(ad: MaxAd) {

                            interstitialAd.loadAd();

                            val intent = Intent(this@MainActivity, SupportedApps::class.java)
                            startActivity(intent)

                        }

                        override fun onAdClicked(ad: MaxAd) {}
                        override fun onAdLoadFailed(adUnitId: String, error: MaxError) {}
                        override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {}
                    })
                } else {
                    val intent = Intent(this, SupportedApps::class.java)
                    startActivity(intent)
                }
            }
            else {
                val intent = Intent(this, SupportedApps::class.java)
                startActivity(intent)
            }
        }

//        whatsappcard.setOnClickListener {
//
//            counter++
//
//            if(counter%3 == 0) {
//
//                if (mInterstitialAd != null) {
//                    if (nn == "nnn") {
//
//                        mInterstitialAd.show(this@MainActivity)
//
//                    }
//
//                    mInterstitialAd.fullScreenContentCallback =
//                        object : FullScreenContentCallback() {
//                            override fun onAdClicked() {
//                                // Called when a click is recorded for an ad.
//                            }
//
//                            override fun onAdDismissedFullScreenContent() {
//                                // Called when ad is dismissed.
//                                // Set the ad reference to null so you don't show the ad a second time.
//                                mInterstitialAd = null
//
//                                val intent = Intent(this@MainActivity, StatusSaverActivity::class.java)
//                                intent.putExtra("frag", "status")
//
//                                startActivity(intent)
//
//                                loadInterstitialAd(this@MainActivity)
//
//                            }
//
//                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
//                                // Called when ad fails to show.
//                                mInterstitialAd = null
//                            }
//
//                            override fun onAdImpression() {
//                                // Called when an impression is recorded for an ad.
//                            }
//
//                            override fun onAdShowedFullScreenContent() {
//                                // Called when ad is shown.
//                            }
//                        }
//
//                } else {
//                    val intent = Intent(this, StatusSaverActivity::class.java)
//                    intent.putExtra("frag", "status")
//
//                    startActivity(intent)
//                }
//            }
//            else {
//                val intent = Intent(this, StatusSaverActivity::class.java)
//                intent.putExtra("frag", "status")
//
//                startActivity(intent)
//            }
//
//        }
//        instcard.setOnClickListener {
//            counter++
//
//            if(counter%3 == 0) {
//
//                if (mInterstitialAd != null) {
//                    if (nn == "nnn") {
//
//                        mInterstitialAd.show(this@MainActivity)
//
//                    }
//
//                    mInterstitialAd.fullScreenContentCallback =
//                        object : FullScreenContentCallback() {
//                            override fun onAdClicked() {
//                                // Called when a click is recorded for an ad.
//                            }
//
//                            override fun onAdDismissedFullScreenContent() {
//                                // Called when ad is dismissed.
//                                // Set the ad reference to null so you don't show the ad a second time.
//                                mInterstitialAd = null
//
//                                val apppackage = "com.instagram.android"
//                                val cx: Context = this@MainActivity
//                                try {
//                                    val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
//                                    cx.startActivity(i)
//                                } catch (e: java.lang.Exception) {
//                                    Toast.makeText(this@MainActivity, "Sorry, Instagram App Not Found", Toast.LENGTH_LONG).show()
//                                }
//
//                                loadInterstitialAd(this@MainActivity)
//
//                            }
//
//                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
//                                // Called when ad fails to show.
//                                mInterstitialAd = null
//                            }
//
//                            override fun onAdImpression() {
//                                // Called when an impression is recorded for an ad.
//                            }
//
//                            override fun onAdShowedFullScreenContent() {
//                                // Called when ad is shown.
//                            }
//                        }
//
//                } else {
//                    val apppackage = "com.instagram.android"
//                    val cx: Context = this
//                    try {
//                        val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
//                        cx.startActivity(i)
//                    } catch (e: java.lang.Exception) {
//                        Toast.makeText(this, "Sorry, Instagram App Not Found", Toast.LENGTH_LONG).show()
//                    }
//
//                }
//            }
//            else {
//
//                val apppackage = "com.instagram.android"
//                val cx: Context = this
//                try {
//                    val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
//                    cx.startActivity(i)
//                } catch (e: java.lang.Exception) {
//                    Toast.makeText(this, "Sorry, Instagram App Not Found", Toast.LENGTH_LONG).show()
//                }
//
//            }
//
//            //val intent = Intent(Intent.ACTION_VIEW, Uri.parse("fb://root"))
//
//            // startActivity(intent)
//
//        }
//
//        tiktokcard.setOnClickListener {
//            counter++
//
//            if(counter%3 == 0) {
//
//                if (mInterstitialAd != null) {
//                    if (nn == "nnn") {
//
//                        mInterstitialAd.show(this@MainActivity)
//
//                    }
//
//                    mInterstitialAd.fullScreenContentCallback =
//                        object : FullScreenContentCallback() {
//                            override fun onAdClicked() {
//                                // Called when a click is recorded for an ad.
//                            }
//
//                            override fun onAdDismissedFullScreenContent() {
//                                // Called when ad is dismissed.
//                                // Set the ad reference to null so you don't show the ad a second time.
//                                mInterstitialAd = null
//
//                                val apppackage = "com.zhiliaoapp.musically"
//                                val cx: Context = this@MainActivity
//                                try {
//                                    val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
//                                    cx.startActivity(i)
//                                } catch (e: java.lang.Exception) {
//                                    Toast.makeText(this@MainActivity, "Sorry, TikTok App Not Found", Toast.LENGTH_LONG).show()
//                                }
//
//                                loadInterstitialAd(this@MainActivity)
//
//                            }
//
//                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
//                                // Called when ad fails to show.
//                                mInterstitialAd = null
//                            }
//
//                            override fun onAdImpression() {
//                                // Called when an impression is recorded for an ad.
//                            }
//
//                            override fun onAdShowedFullScreenContent() {
//                                // Called when ad is shown.
//                            }
//                        }
//
//                } else {
//                    val apppackage = "com.zhiliaoapp.musically"
//                    val cx: Context = this
//                    try {
//                        val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
//                        cx.startActivity(i)
//                    } catch (e: java.lang.Exception) {
//                        Toast.makeText(this, "Sorry, TikTok App Not Found", Toast.LENGTH_LONG).show()
//                    }
//                }
//            } else {
//
//                val apppackage = "com.zhiliaoapp.musically"
//                val cx: Context = this
//                try {
//                    val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
//                    cx.startActivity(i)
//                } catch (e: java.lang.Exception) {
//                    Toast.makeText(this, "Sorry, TikTok App Not Found", Toast.LENGTH_LONG).show()
//                }
//
//            }
//
//        }
//
//        twittercard.setOnClickListener {
//            counter++
//
//            if(counter%3 == 0) {
//
//                if (mInterstitialAd != null) {
//                    if (nn == "nnn") {
//
//                        mInterstitialAd.show(this@MainActivity)
//
//                    }
//
//                    mInterstitialAd.fullScreenContentCallback =
//                        object : FullScreenContentCallback() {
//                            override fun onAdClicked() {
//                                // Called when a click is recorded for an ad.
//                            }
//
//                            override fun onAdDismissedFullScreenContent() {
//                                // Called when ad is dismissed.
//                                // Set the ad reference to null so you don't show the ad a second time.
//                                mInterstitialAd = null
//
//                                val apppackage = "com.twitter.android"
//                                val cx: Context = this@MainActivity
//                                try {
//                                    val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
//                                    cx.startActivity(i)
//                                } catch (e: java.lang.Exception) {
//                                    Toast.makeText(this@MainActivity, "Sorry, Twitter App Not Found", Toast.LENGTH_LONG).show()
//                                }
//
//                                loadInterstitialAd(this@MainActivity)
//
//                            }
//
//                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
//                                // Called when ad fails to show.
//                                mInterstitialAd = null
//                            }
//
//                            override fun onAdImpression() {
//                                // Called when an impression is recorded for an ad.
//                            }
//
//                            override fun onAdShowedFullScreenContent() {
//                                // Called when ad is shown.
//                            }
//                        }
//
//                } else {
//                    val apppackage = "com.twitter.android"
//                    val cx: Context = this
//                    try {
//                        val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
//                        cx.startActivity(i)
//                    } catch (e: java.lang.Exception) {
//                        Toast.makeText(this, "Sorry, Twitter App Not Found", Toast.LENGTH_LONG).show()
//                    }
//                }
//            } else {
//
//                val apppackage = "com.twitter.android"
//                val cx: Context = this
//                try {
//                    val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
//                    cx.startActivity(i)
//                } catch (e: java.lang.Exception) {
//                    Toast.makeText(this, "Sorry, Twitter App Not Found", Toast.LENGTH_LONG).show()
//                }
//
//            }
//
//        }
//
//        snapcard.setOnClickListener {
//            counter++
//
//            if(counter%3 == 0) {
//
//                if (mInterstitialAd != null) {
//                    if (nn == "nnn") {
//
//                        mInterstitialAd.show(this@MainActivity)
//
//                    }
//
//                    mInterstitialAd.fullScreenContentCallback =
//                        object : FullScreenContentCallback() {
//                            override fun onAdClicked() {
//                                // Called when a click is recorded for an ad.
//                            }
//
//                            override fun onAdDismissedFullScreenContent() {
//                                // Called when ad is dismissed.
//                                // Set the ad reference to null so you don't show the ad a second time.
//                                mInterstitialAd = null
//
//                                val apppackage = "com.snapchat.android"
//                                val cx: Context = this@MainActivity
//                                try {
//                                    val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
//                                    cx.startActivity(i)
//                                } catch (e: java.lang.Exception) {
//                                    Toast.makeText(this@MainActivity, "Sorry, SnapChat App Not Found", Toast.LENGTH_LONG).show()
//                                }
//
//                                loadInterstitialAd(this@MainActivity)
//
//                            }
//
//                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
//                                // Called when ad fails to show.
//                                mInterstitialAd = null
//                            }
//
//                            override fun onAdImpression() {
//                                // Called when an impression is recorded for an ad.
//                            }
//
//                            override fun onAdShowedFullScreenContent() {
//                                // Called when ad is shown.
//                            }
//                        }
//
//                } else {
//                    val apppackage = "com.snapchat.android"
//                    val cx: Context = this
//                    try {
//                        val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
//                        cx.startActivity(i)
//                    } catch (e: java.lang.Exception) {
//                        Toast.makeText(this, "Sorry, SnapChat App Not Found", Toast.LENGTH_LONG).show()
//                    }
//
//                }
//            }  else {
//
//                val apppackage = "com.snapchat.android"
//                val cx: Context = this
//                try {
//                    val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
//                    cx.startActivity(i)
//                } catch (e: java.lang.Exception) {
//                    Toast.makeText(this, "Sorry, SnapChat App Not Found", Toast.LENGTH_LONG).show()
//                }
//
//            }
//
//        }
//
////        pinterest.setOnClickListener {
////            counter++
////
////            if(counter%3 == 0) {
////
////                if (mInterstitialAd != null) {
////                    if (nn == "nnn") {
////
////                        mInterstitialAd.show(this@MainActivity)
////
////                    }
////
////                    mInterstitialAd.fullScreenContentCallback =
////                        object : FullScreenContentCallback() {
////                            override fun onAdClicked() {
////                                // Called when a click is recorded for an ad.
////                            }
////
////                            override fun onAdDismissedFullScreenContent() {
////                                // Called when ad is dismissed.
////                                // Set the ad reference to null so you don't show the ad a second time.
////                                mInterstitialAd = null
////
////                                val apppackage = "com.pinterest"
////                                val cx: Context = this@MainActivity
////                                try {
////                                    val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
////                                    cx.startActivity(i)
////                                } catch (e: java.lang.Exception) {
////                                    Toast.makeText(this@MainActivity, "Sorry, Pinterest App Not Found", Toast.LENGTH_LONG).show()
////                                }
////
////                                loadInterstitialAd(this@MainActivity)
////
////                            }
////
////                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
////                                // Called when ad fails to show.
////                                mInterstitialAd = null
////                            }
////
////                            override fun onAdImpression() {
////                                // Called when an impression is recorded for an ad.
////                            }
////
////                            override fun onAdShowedFullScreenContent() {
////                                // Called when ad is shown.
////                            }
////                        }
////
////                } else {
////                    val apppackage = "com.pinterest"
////                    val cx: Context = this
////                    try {
////                        val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
////                        cx.startActivity(i)
////                    } catch (e: java.lang.Exception) {
////                        Toast.makeText(this, "Sorry, Pinterest App Not Found", Toast.LENGTH_LONG).show()
////                    }
////
////
////                }
////            } else {
////
////                val apppackage = "com.pinterest"
////                val cx: Context = this
////                try {
////                    val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
////                    cx.startActivity(i)
////                } catch (e: java.lang.Exception) {
////                    Toast.makeText(this, "Sorry, Pinterest App Not Found", Toast.LENGTH_LONG).show()
////                }
////
////            }
////
////        }
//
//        facebookcard.setOnClickListener {
//            counter++
//
//            if(counter%3 == 0) {
//
//                if (mInterstitialAd != null) {
//                    if (nn == "nnn") {
//
//                        mInterstitialAd.show(this@MainActivity)
//
//                    }
//
//                    mInterstitialAd.fullScreenContentCallback =
//                        object : FullScreenContentCallback() {
//                            override fun onAdClicked() {
//                                // Called when a click is recorded for an ad.
//                            }
//
//                            override fun onAdDismissedFullScreenContent() {
//                                // Called when ad is dismissed.
//                                // Set the ad reference to null so you don't show the ad a second time.
//                                mInterstitialAd = null
//
//                                val apppackage = "com.facebook.katana"
//                                val cx: Context = this@MainActivity
//                                try {
//                                    val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
//                                    cx.startActivity(i)
//                                } catch (e: java.lang.Exception) {
//                                    Toast.makeText(this@MainActivity, "Sorry, Facebook App Not Found", Toast.LENGTH_LONG).show()
//                                }
//
//                                loadInterstitialAd(this@MainActivity)
//
//                            }
//
//                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
//                                // Called when ad fails to show.
//                                mInterstitialAd = null
//                            }
//
//                            override fun onAdImpression() {
//                                // Called when an impression is recorded for an ad.
//                            }
//
//                            override fun onAdShowedFullScreenContent() {
//                                // Called when ad is shown.
//                            }
//                        }
//
//                } else {
//                    val apppackage = "com.facebook.katana"
//                    val cx: Context = this
//                    try {
//                        val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
//                        cx.startActivity(i)
//                    } catch (e: java.lang.Exception) {
//                        Toast.makeText(this, "Sorry, Facebook App Not Found", Toast.LENGTH_LONG).show()
//                    }
//
//                }
//            } else {
//
//                val apppackage = "com.facebook.katana"
//                val cx: Context = this
//                try {
//                    val i = cx.packageManager.getLaunchIntentForPackage(apppackage)
//                    cx.startActivity(i)
//                } catch (e: java.lang.Exception) {
//                    Toast.makeText(this, "Sorry, Facebook App Not Found", Toast.LENGTH_LONG).show()
//                }
//
//            }
//
//        }


        copylinkanddownloadcard.setOnClickListener {
            counter++

            if(counter%2 == 0) {

                if (mInterstitialAd != null) {
                    if (nn == "nnn") {

                        mInterstitialAd.show(this@MainActivity)

                    }

                    mInterstitialAd.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdClicked() {
                                // Called when a click is recorded for an ad.
                            }

                            override fun onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                mInterstitialAd = null

                                val clipBoardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                                val primaryClipData = clipBoardManager.primaryClip
                                val clip = primaryClipData?.getItemAt(0)?.text.toString()
                                //copylinkanddownloadcard.setText(clip)

                                DownloadVideo(clip)

                                loadInterstitialAd(this@MainActivity)

                            }

                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                // Called when ad fails to show.
                                mInterstitialAd = null
                            }

                            override fun onAdImpression() {
                                // Called when an impression is recorded for an ad.
                            }

                            override fun onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                            }
                        }

                }  else if  ( interstitialAd.isReady )
                {
                    interstitialAd.showAd()

                    interstitialAd.setListener(object : MaxAdListener {
                        override fun onAdLoaded(ad: MaxAd) {
                            if (interstitialAd.isReady) {
                                // interstitialAd.showAd();
                            }
                        }

                        override fun onAdDisplayed(ad: MaxAd) {

                        }
                        override fun onAdHidden(ad: MaxAd) {

                            interstitialAd.loadAd();

                            val clipBoardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                            val primaryClipData = clipBoardManager.primaryClip
                            val clip = primaryClipData?.getItemAt(0)?.text.toString()
                            //copylinkanddownloadcard.setText(clip)

                            DownloadVideo(clip)

                        }

                        override fun onAdClicked(ad: MaxAd) {}
                        override fun onAdLoadFailed(adUnitId: String, error: MaxError) {}
                        override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {}
                    })
                } else {
                    val clipBoardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                    val primaryClipData = clipBoardManager.primaryClip
                    val clip = primaryClipData?.getItemAt(0)?.text.toString()
                    //copylinkanddownloadcard.setText(clip)

                    DownloadVideo(clip)

                }
            } else {

                val clipBoardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                val primaryClipData = clipBoardManager.primaryClip
                val clip = primaryClipData?.getItemAt(0)?.text.toString()
                //copylinkanddownloadcard.setText(clip)

                DownloadVideo(clip)

            }


        }

        isNeedGrantPermission()


//        if (Build.VERSION.SDK_INT >= 23) {
//            if (!Settings.canDrawOverlays(this@MainActivity)) {
//                val intent = Intent(
//                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                    Uri.parse("package:" + packageName)
//                )
//                startActivityForResult(intent, 1234)
//            }
//        }


//
//        if (csRunning) {
//            chkAutoDownload.isChecked = true
//            startClipboardMonitor()
//        } else {
//            chkAutoDownload.isChecked = false
//            stopClipboardMonitor()
//        }



//
//
//        chkAutoDownload.setOnClickListener {
//
//            counter++
//
//            if(counter%2 == 0) {
//
//                if (mInterstitialAd != null) {
//                    if (nn == "nnn") {
//
//                        mInterstitialAd.show(this@MainActivity)
//
//                    }
//
//                    mInterstitialAd.fullScreenContentCallback =
//                        object : FullScreenContentCallback() {
//                            override fun onAdClicked() {
//                                // Called when a click is recorded for an ad.
//                            }
//
//                            override fun onAdDismissedFullScreenContent() {
//                                // Called when ad is dismissed.
//                                // Set the ad reference to null so you don't show the ad a second time.
//                                mInterstitialAd = null
//
//                                val checked = chkAutoDownload.isChecked
//                                if (!checked) {
//                                    chkAutoDownload.isChecked = false
//                                    stopClipboardMonitor()
//                                } else {
//                                    showAdDialog()
//
//                                }
//                                loadInterstitialAd(this@MainActivity)
//
//                            }
//
//                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
//                                // Called when ad fails to show.
//                                mInterstitialAd = null
//                            }
//
//                            override fun onAdImpression() {
//                                // Called when an impression is recorded for an ad.
//                            }
//
//                            override fun onAdShowedFullScreenContent() {
//                                // Called when ad is shown.
//                            }
//                        }
//
//                } else if  ( interstitialAd.isReady )
//                {
//                    interstitialAd.showAd()
//
//                    interstitialAd.setListener(object : MaxAdListener {
//                        override fun onAdLoaded(ad: MaxAd) {
//                            if (interstitialAd.isReady) {
//                                // interstitialAd.showAd();
//                            }
//                        }
//
//                        override fun onAdDisplayed(ad: MaxAd) {
//
//                        }
//                        override fun onAdHidden(ad: MaxAd) {
//
//                            interstitialAd.loadAd();
//
//                            val checked = chkAutoDownload.isChecked
//                            if (!checked) {
//                                chkAutoDownload.isChecked = false
//                                stopClipboardMonitor()
//                            } else {
//                                showAdDialog()
//
//                            }
//
//                        }
//
//                        override fun onAdClicked(ad: MaxAd) {}
//                        override fun onAdLoadFailed(adUnitId: String, error: MaxError) {}
//                        override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {}
//                    })
//                } else {
//                    val checked = chkAutoDownload.isChecked
//                    if (!checked) {
//                        chkAutoDownload.isChecked = false
//                        stopClipboardMonitor()
//                    } else {
//                        showAdDialog()
//
//                    }
//                }
//            } else {
//
//                val checked = chkAutoDownload.isChecked
//                if (!checked) {
//                    chkAutoDownload.isChecked = false
//                    stopClipboardMonitor()
//                } else {
//                    showAdDialog()
//
//                }
//
//            }
//
//
//        }


        if (intent != null) {
            val action1 = intent!!.action
            val type1 = intent!!.type

            if (Intent.ACTION_SEND == action1 && type1 != null) {
                if ("text/plain" == type1) {
                    handleSendText(intent) // Handle text being sent


                }
            }
        }



        if (Constants.show_Ads) {

            val prefs: SharedPreferences = getSharedPreferences(
                "whatsapp_pref",
                Context.MODE_PRIVATE
            )

            val pp =
                prefs.getString("inappads", "nnn") //"No name defined" is the default value.


            if (pp.equals("nnn")) {
                Log.d("AdsManager:app ","working")



                loadAdmobNativeAd(this, nativeadContainer)


            } else {
                //nativeadContainer.visibility = View.GONE
            }

        }



        AppRating.Builder(this@MainActivity)
            .setMinimumLaunchTimes(5)
            .setMinimumDays(7)
            .setMinimumLaunchTimesToShowAgain(5)
            .setMinimumDaysToShowAgain(7)
            .setRatingThreshold(RatingThreshold.FOUR)
            .showIfMeetsConditions()

    }



    fun getMyData(): String? {
        return myString
    }


    fun setmydata(mysa: String) {
        myString = mysa
    }


    override fun onStart() {
        super.onStart()

//        try {
//
//
//            if (iUtils.showDialogUpdateTimesShown < iUtils.showDialogUpdateTimesPerSession) {
//
//                iUtils.showDialogUpdateTimesShown++
//
//                if (iUtils.isNetworkConnected(this@MainActivity)) {
//
//                    val appUpdaterUtils: AppUpdaterUtils = AppUpdaterUtils(this)
//                        .withListener(object : AppUpdaterUtils.UpdateListener {
//                            override fun onSuccess(
//                                update: com.github.javiersantos.appupdater.objects.Update?,
//                                isUpdateAvailable: Boolean?
//                            ) {
//
//                                println("appupdater error bb " + isUpdateAvailable)
//                                println("appupdater error uuu " + update!!.latestVersion)
//
//                                if (isUpdateAvailable!!) {
//
//                                    launchUpdateDialog(update.latestVersion)
//
//
//                                }
//
//
//                            }
//
//                            override fun onFailed(error: AppUpdaterError?) {
//
//
//                            }
//
//
//                        })
//                    appUpdaterUtils.start()
//
//
//                }
//            }
//        } catch (e: Exception) {
//
//        }


    }


    private fun launchUpdateDialog(onlineVersion: String) {
        try {
            AlertDialog.Builder(this@MainActivity)
                .setTitle(getString(R.string.updqteavaliable))
                .setCancelable(false)
                .setMessage(
                    getString(R.string.update) + " " + onlineVersion + " " + getString(R.string.updateisavaliabledownload) + getString(
                        R.string.app_name
                    )
                )
                .setPositiveButton(
                    resources.getString(R.string.update_now)
                )
                { dialog, _ ->
                    dialog.dismiss()
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                        )
                    )

                }

                .setNegativeButton(
                    resources.getString(R.string.cancel)
                )
                { dialog, _ ->
                    dialog.dismiss()

                }
                .setIcon(R.drawable.ic_appicon)
                .show()

        } catch (e: java.lang.Exception) {

            println("appupdater error rrrr " + e)
            e.printStackTrace()
        }
    }



    fun handleSendText(intent: Intent) {



        try {

            this.intent = null
            var url = intent.getStringExtra(Intent.EXTRA_TEXT)



            if (url.equals("") && iUtils.checkURL(url)) {
                iUtils.ShowToast(
                    this@MainActivity,
                    this@MainActivity.resources?.getString(R.string.enter_valid)
                )
                return

            }

            if (url?.contains("myjosh.in")!!) {


                try{
                    url = iUtils.extractUrls(url)[0]
                }catch (e:Exception){

                }

                DownloadVideosMain.Start(this@MainActivity, url?.trim(), false)
                if (url != null) {
                    Log.e("downloadFileName12", url.trim())
                }


            } else if (url.contains("chingari")) {


                try{
                    url = iUtils.extractUrls(url)[0]
                }catch (e:Exception){

                }

                DownloadVideosMain.Start(this@MainActivity, url?.trim(), false)
                if (url != null) {
                    Log.e("downloadFileName12", url.trim())
                }
            } else if (url.contains("bemate")) {

                var mynewval = url
                try{
                    url = iUtils.extractUrls(url)[0]
                }catch (e:Exception){

                }

                DownloadVideosMain.Start(this@MainActivity, url?.trim(), false)
                if (url != null) {
                    Log.e("downloadFileName12", url.trim())
                }
            }
            else if (url.contains("story.snapchat.com")) {
                try {
                    url = iUtils.extractUrls(url)[0]
                } catch (e: Exception) {

                }

                val i: Intent = Intent(
                    this@MainActivity,
                    SnapChatBulkStoryDownloader::class.java
                )
                i.putExtra("urlsnap", url?.trim())
                startActivity(i)

                if (url != null) {
                    Log.e("downloadF" , url.trim())
                }
            }
            else if (url.contains("instagram.com")) {

                DownloadVideo(url.trim())

                Log.e("downloadFileName12", url)
            } else if (url.contains("sck.io") || url.contains("snackvideo")) {
                var mynewval = url
                try{
                    url = iUtils.extractUrls(url)[0]
                }catch (e:Exception){

                }

                DownloadVideosMain.Start(this@MainActivity, url?.trim(), false)
                if (url != null) {
                    Log.e("downloadFileName12", url.trim())
                }
            } else {
                var mynewval = url
                try{
                    mynewval = iUtils.extractUrls(mynewval)[0]
                }catch (e:Exception){

                }

                DownloadVideo(mynewval!!.trim())

            }
        } catch (e: Exception) {

        }
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        Log.e("myhdasbdhf newintent ", intent?.getStringExtra(Intent.EXTRA_TEXT) + "_46237478234")

        intent?.let { newIntent ->

            handleSendText(newIntent)
            Log.e("myhdasbdhf notdownload ", newIntent.getStringExtra(Intent.EXTRA_TEXT) + "")

        }
    }


    private fun isNeedGrantPermission(): Boolean {
        try {
            if (iUtils.hasMarsallow()) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        REQUEST_PERMISSION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this@MainActivity,
                            REQUEST_PERMISSION
                        )
                    ) {
                        val msg =
                            String.format(
                                getString(R.string.format_request_permision),
                                getString(R.string.app_name)
                            )

                        val localBuilder = AlertDialog.Builder(this@MainActivity)
                        localBuilder.setTitle(getString(R.string.permission_title))
                        localBuilder
                            .setMessage(msg).setNeutralButton(
                                getString(R.string.grant_option)
                            ) { _, _ ->
                                ActivityCompat.requestPermissions(
                                    this@MainActivity,
                                    arrayOf(REQUEST_PERMISSION),
                                    REQUEST_PERMISSION_CODE
                                )
                            }
                            .setNegativeButton(
                                getString(R.string.cancel_option)
                            ) { paramAnonymousDialogInterface, _ ->
                                paramAnonymousDialogInterface.dismiss()
                                finish()
                            }
                        localBuilder.show()

                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(REQUEST_PERMISSION),
                            REQUEST_PERMISSION_CODE
                        )
                    }
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false

    }


    fun displayNativeAd(parent: ViewGroup, ad: NativeAd) {

        // Inflate a layout and add it to the parent ViewGroup.
        val inflater = parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)
                as LayoutInflater
        val adView = inflater.inflate(R.layout.native_ad_layout, parent) as NativeAdView

        // Locate the view that will hold the headline, set its text, and use the
        // NativeAdView's headlineView property to register it.
        val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
        headlineView.text = ad.headline
        adView.headlineView = headlineView

        val mediaView = adView.findViewById<MediaView>(R.id.ad_media)
        adView.mediaView = mediaView

        // Call the NativeAdView's setNativeAd method to register the
        // NativeAdObject.
        adView.setNativeAd(ad)

        // Ensure that the parent view doesn't already contain an ad view.
        parent.removeAllViews()

        // Place the AdView into the parent.
        parent.addView(adView)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            if (requestCode == REQUEST_PERMISSION_CODE) {
                if (grantResults != null && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    iUtils.ShowToast(this@MainActivity, getString(R.string.info_permission_denied))

                    finish()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            iUtils.ShowToast(this@MainActivity, getString(R.string.info_permission_denied))
            finish()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.


        menuInflater.inflate(R.menu.main, menu)

        val prefs: SharedPreferences = getSharedPreferences(
            "whatsapp_pref",
            Context.MODE_PRIVATE
        )
        val name = prefs.getString("whatsapp", "main") //"No name defined" is the default value.
        val pp = prefs.getString("inappads", "nnn") //"No name defined" is the default value.

//
//        if (name == "main") {
//            menu.findItem(R.id.action_shwbusinesswhatsapp).isVisible = true
//
//            menu.findItem(R.id.action_shwmainwhatsapp).isVisible = false
//
//        } else if (name == "bus") {
//            menu.findItem(R.id.action_shwbusinesswhatsapp).isVisible = false
//
//            menu.findItem(R.id.action_shwmainwhatsapp).isVisible = true
//
//        }




        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.action_privacy -> {

//                AlertDialog.Builder(this)
//                    .setTitle(getString(R.string.privacy))
//                    .setMessage(R.string.privacy_message)
//                    .setPositiveButton(
//                        android.R.string.yes
//                    ) { dialog, _ -> dialog.dismiss() }
//                    .setIcon(R.drawable.ic_info_black_24dp)
//                    .show()

                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_url)))
                startActivity(browserIntent)

                true
            }

            /* R.id.action_downloadtiktok -> {

                 val intent = Intent(this, TikTokDownloadWebview::class.java)
                 startActivity(intent)


                 true
             }*/


            R.id.action_rate -> {


                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.RateAppTitle))
                    .setMessage(getString(R.string.RateApp))
                    .setCancelable(false)
                    .setPositiveButton(
                        getString(R.string.rate_dialog)
                    ) { _, _ ->
                        val appPackageName = packageName
                        try {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=$appPackageName")
                                )
                            )
                        } catch (anfe: android.content.ActivityNotFoundException) {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                                )
                            )
                        }
                    }
                    .setNegativeButton(getString(R.string.later_btn), null).show()

                true
            }


            /* R.id.ic_whatapp -> {

                 val launchIntent = packageManager.getLaunchIntentForPackage("com.whatsapp")
                 if (launchIntent != null) {

                     startActivity(launchIntent)
                   //  finish()
                 } else {

                     iUtils.ShowToast(
                         this,
                         this.resources.getString(R.string.appnotinstalled)
                     )
                 }
                 true
             }*/


            R.id.action_language -> {

                val dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.setContentView(R.layout.dialog_change_language)

                val l_english = dialog.findViewById(R.id.l_english) as TextView
                l_english.setOnClickListener {

                    LocaleHelper.setLocale(application, "en")
                    window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL


                    val editor: SharedPreferences.Editor = getSharedPreferences(
                        "lang_pref",
                        Context.MODE_PRIVATE
                    ).edit()
                    editor.putString("lang", "en")

                    editor.apply()


                    recreate()
                    dialog.dismiss()
                }


                /* val l_french = dialog.findViewById(R.id.l_french) as TextView
                 l_french.setOnClickListener {
                     LocaleHelper.setLocale(application, "fr")
                     window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
                     val editor: SharedPreferences.Editor = getSharedPreferences(
                         "lang_pref",
                         Context.MODE_PRIVATE
                     ).edit()
                     editor.putString("lang", "fr")

                     editor.apply()


                     recreate()
                     dialog.dismiss()
                 }*/


                val l_arabic = dialog.findViewById(R.id.l_arabic) as TextView
                l_arabic.setOnClickListener {
                    LocaleHelper.setLocale(application, "ar")
                    window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL

                    val editor: SharedPreferences.Editor = getSharedPreferences(
                        "lang_pref",
                        Context.MODE_PRIVATE
                    ).edit()
                    editor.putString("lang", "ar")

                    editor.apply()


                    recreate()
                    dialog.dismiss()

                }
                /*   val l_urdu = dialog.findViewById(R.id.l_urdu) as TextView
                   l_urdu.setOnClickListener {
                       LocaleHelper.setLocale(application, "ur")
                       window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR


                       val editor: SharedPreferences.Editor = getSharedPreferences(
                           "lang_pref",
                           Context.MODE_PRIVATE
                       ).edit()
                       editor.putString("lang", "ur")

                       editor.apply()


                       recreate()
                       dialog.dismiss()
                   }


                   val l_german = dialog.findViewById(R.id.l_german) as TextView
                   l_german.setOnClickListener {
                       LocaleHelper.setLocale(application, "de")
                       window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
                       val editor: SharedPreferences.Editor = getSharedPreferences(
                           "lang_pref",
                           Context.MODE_PRIVATE
                       ).edit()
                       editor.putString("lang", "de")

                       editor.apply()


                       recreate()
                       dialog.dismiss()
                   }


                   val l_turkey = dialog.findViewById(R.id.l_turkey) as TextView
                   l_turkey.setOnClickListener {
                       LocaleHelper.setLocale(application, "tr")
                       window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
                       val editor: SharedPreferences.Editor = getSharedPreferences(
                           "lang_pref",
                           Context.MODE_PRIVATE
                       ).edit()
                       editor.putString("lang", "tr")

                       editor.apply()


                       recreate()
                       dialog.dismiss()
                   }


                   val l_portougese = dialog.findViewById(R.id.l_portougese) as TextView
                   l_portougese.setOnClickListener {
                       LocaleHelper.setLocale(application, "pt")
                       window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
                       val editor: SharedPreferences.Editor = getSharedPreferences(
                           "lang_pref",
                           Context.MODE_PRIVATE
                       ).edit()
                       editor.putString("lang", "pt")

                       editor.apply()


                       recreate()
                       dialog.dismiss()
                   }


                   val l_chinese = dialog.findViewById(R.id.l_chinese) as TextView
                   l_chinese.setOnClickListener {
                       LocaleHelper.setLocale(application, "zh")
                       window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
                       val editor: SharedPreferences.Editor = getSharedPreferences(
                           "lang_pref",
                           Context.MODE_PRIVATE
                       ).edit()
                       editor.putString("lang", "zh")

                       editor.apply()


                       recreate()
                       dialog.dismiss()
                   }*/





                val l_hindi = dialog.findViewById(R.id.l_hindi) as TextView
                l_hindi.setOnClickListener {
                    LocaleHelper.setLocale(application, "hi")
                    window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
                    val editor: SharedPreferences.Editor = getSharedPreferences(
                        "lang_pref",
                        Context.MODE_PRIVATE
                    ).edit()
                    editor.putString("lang", "hi")

                    editor.apply()


                    recreate()
                    dialog.dismiss()
                }




                dialog.show()

                true
            }

//            R.id.action_shwbusinesswhatsapp -> {
//
//
//                val editor: SharedPreferences.Editor = getSharedPreferences(
//                    "whatsapp_pref",
//                    Context.MODE_PRIVATE
//                ).edit()
//                editor.putString("whatsapp", "bus")
//
//                editor.apply()
//
//                if (Build.VERSION.SDK_INT >= 11) {
//                    recreate()
//                } else {
//                    val intent = intent
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//                    finish()
//                    overridePendingTransition(0, 0)
//                    startActivity(intent)
//                    overridePendingTransition(0, 0)
//                }
//
//
//
//                true
//            }
//
//
//            R.id.action_shwmainwhatsapp -> {
//
//
//                val editor: SharedPreferences.Editor = getSharedPreferences(
//                    "whatsapp_pref",
//                    Context.MODE_PRIVATE
//                ).edit()
//                editor.putString("whatsapp", "main")
//
//                editor.apply()
//
//
//                if (Build.VERSION.SDK_INT >= 11) {
//                    recreate()
//                } else {
//                    val intent = intent
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//                    finish()
//                    overridePendingTransition(0, 0)
//                    startActivity(intent)
//                    overridePendingTransition(0, 0)
//                }
//
//
//
//                true
//            }



            else -> super.onOptionsItemSelected(item)
        }
    }


    fun removemyadshere() {
        try {
            skuDetails?.let {
                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(it)
                    .build()
                billingClient?.launchBillingFlow(this@MainActivity, billingFlowParams)?.responseCode
            } ?: noSKUMessage()

        } catch (e: Exception) {

            iUtils.ShowToastError(this@MainActivity, "close the app and restart");

        }
    }



    override fun onBackPressed() {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog_ad_exit)
        val yesBtn = dialog.findViewById(R.id.btn_exitdialog_yes) as Button
        val noBtn = dialog.findViewById(R.id.btn_exitdialog_no) as Button

//TODO ENABLE EXIT DIALOG AD BY UNCOMMENTING IT
//        val adviewnew = dialog.findViewById(R.id.adView_dia) as AdView
//        val adRequest = AdRequest.Builder().build()
//        adviewnew.loadAd(adRequest)


        yesBtn.setOnClickListener {
            System.exit(0)
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("proddddd11111222 $resultCode __" + data)

        if (requestCode == 200 && resultCode == RESULT_OK) {

            println("proddddd11111 $resultCode __" + data)

        }
//
//
//
//        if (!bp!!.handleActivityResult(requestCode, resultCode, data)) {
//            super.onActivityResult(requestCode, resultCode, data)
//        }


        try {
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    private fun noSKUMessage() {
        Toast.makeText(
            this@MainActivity, "No SKU Found", Toast.LENGTH_SHORT
        ).show()

    }

    private fun setUpBillingClient1() {
        billingClient = BillingClient.newBuilder(this)
            .setListener(purchaseUpdateListener)
            .enablePendingPurchases()
            .build()
        startConnection()
    }

    private fun startConnection() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.v("TAG_INAPP", "Setup Billing Done")
                    queryAvaliableProducts()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    private fun queryAvaliableProducts() {
        val skuList = ArrayList<String>()
        skuList.add(getString(R.string.productidcode))
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

        billingClient?.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            // Process the result.
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !skuDetailsList.isNullOrEmpty()) {
                for (skuDetails in skuDetailsList) {
                    Log.v("TAG_INAPP", "skuDetailsList : ${skuDetailsList}")
                    //This list should contain the products added above
                    updateUI(skuDetails)
                }
            }
        }
    }

    private fun updateUI(skuDetails: SkuDetails?) {
        skuDetails?.let {
            this.skuDetails = it
//            txt_product_name?.text = skuDetails.title
//            txt_product_description?.text = skuDetails.description
            showUIElements()
        }
    }

    private fun showUIElements() {
//        txt_product_name?.visibility = View.VISIBLE
//        txt_product_description?.visibility = View.VISIBLE
//        txt_product_buy?.visibility = View.VISIBLE
    }

    private val purchaseUpdateListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            Log.v("TAG_INAPP", "billingResult responseCode : ${billingResult.responseCode}")
            try {
                if (billingResult.responseCode == BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        //                        handlePurchase(purchase)


                        handleConsumedPurchases(purchase)

                        //handleNonConcumablePurchase(purchase)
                    }
                } else if (billingResult.responseCode == BillingResponseCode.USER_CANCELED) {
                    // Handle an error caused by a user cancelling the purchase flow.
                } else if (billingResult.responseCode == BillingResponseCode.ITEM_UNAVAILABLE) {
                    Toast.makeText(
                        this@MainActivity, "ITEM_UNAVAILABLE", Toast.LENGTH_SHORT
                    ).show()
                } else if (billingResult.responseCode == BillingResponseCode.ITEM_ALREADY_OWNED) {


                    AlertDialog.Builder(this@MainActivity)
                        .setTitle(getString(R.string.itemowned))
                        .setMessage(getString(R.string.adsareremoved))
                        .setIcon(R.drawable.ic_appicon)
                        .show()


                    Toast.makeText(
                        this@MainActivity, getString(R.string.itemowned), Toast.LENGTH_SHORT
                    ).show()

                    val editor: SharedPreferences.Editor = getSharedPreferences(
                        "whatsapp_pref",
                        Context.MODE_PRIVATE
                    ).edit()
                    editor.putString("inappads", "ppp")

                    editor.apply()
                } else {

                    Toast.makeText(
                        this@MainActivity, "Error has occured", Toast.LENGTH_SHORT
                    ).show()

                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    private fun handleConsumedPurchases(purchase: Purchase) {
        Log.d("TAG_INAPP", "handleConsumablePurchasesAsync foreach it is $purchase")


        if (purchase.skus.equals(getString(R.string.productidcode))) {

            val params =
                ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
            billingClient?.consumeAsync(params) { billingResult, _ ->
                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {

                        AlertDialog.Builder(this@MainActivity)
                            .setTitle(getString(R.string.purchasedone))
                            .setMessage(getString(R.string.adsareremoved))
                            .setIcon(R.drawable.ic_appicon)
                            .show()

                        Toast.makeText(
                            this@MainActivity, getString(R.string.purchasedone), Toast.LENGTH_SHORT
                        ).show()

                        val editor: SharedPreferences.Editor = getSharedPreferences(
                            "whatsapp_pref",
                            Context.MODE_PRIVATE
                        ).edit()
                        editor.putString("inappads", "ppp")

                        editor.apply()

                        // Update the appropriate tables/databases to grant user the items
                        Log.d(
                            "TAG_INAPP",
                            " Update the appropriate tables/databases to grant user the items"
                        )
                    }
                    else -> {
                        Log.w("TAG_INAPP", billingResult.debugMessage)
                    }
                }
            }


        }


    }

    private fun handleNonConcumablePurchase(purchase: Purchase) {
        Log.v("TAG_INAPP", "handlePurchase : ${purchase}")

        if (purchase.skus.equals(getString(R.string.productidcode))) {


            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (!purchase.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken).build()
                    billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                        val billingResponseCode = billingResult.responseCode
                        val billingDebugMessage = billingResult.debugMessage

                        Log.v("TAG_INAPP", "response code: $billingResponseCode")
                        Log.v("TAG_INAPP", "debugMessage : $billingDebugMessage")
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle(getString(R.string.purchasedone))
                            .setMessage(getString(R.string.adsareremoved))
                            .setIcon(R.drawable.ic_appicon)
                            .show()

                        Toast.makeText(
                            this@MainActivity, getString(R.string.purchasedone), Toast.LENGTH_SHORT
                        ).show()

                        val editor: SharedPreferences.Editor = getSharedPreferences(
                            "whatsapp_pref",
                            Context.MODE_PRIVATE
                        ).edit()
                        editor.putString("inappads", "ppp")

                        editor.apply()

                    }
                }
            }
        }
    }

    fun createNotificationChannel(
        importance: Int,
        showBadge: Boolean,
        name: String,
        description: String
    ) {
        // 1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // 2
            val channelId = "${packageName}-$name"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.setShowBadge(showBadge)

            // 3
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.e("loged112211", "Notificaion Channel Created!")
        }
    }

    fun startClipboardMonitor() {
        prefEditor.putBoolean("csRunning", true)
        prefEditor.commit()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(
                Intent(
                    this@MainActivity,
                    ClipboardMonitor::class.java
                ).setAction(Constants.STARTFOREGROUND_ACTION)
            )
        } else {
            startService(
                Intent(
                    this@MainActivity,
                    ClipboardMonitor::class.java
                )
            )
        }

    }

    fun stopClipboardMonitor() {
        prefEditor.putBoolean("csRunning", false)
        prefEditor.commit()

        stopService(
            Intent(
                this@MainActivity,
                ClipboardMonitor::class.java
            ).setAction(Constants.STOPFOREGROUND_ACTION)
        )


    }

    fun DownloadVideo(url: String) {
        Log.e("myhdasbdhf urlis  ", url)


        if (url.equals("") && iUtils.checkURL(url)) {
            iUtils.ShowToast(this@MainActivity, resources?.getString(R.string.enter_valid))


        } else {


            val rand = Random()
            val rand_int1 = rand.nextInt(2)
            println("randonvalueis = $rand_int1")

            showAdmobAds()



            Log.d("mylogissssss", "The interstitial wasn't loaded yet.")



            if (url.contains("instagram.com")) {
                progressDralogGenaratinglink.show()
                startInstaDownload(url)

            } else if (url.contains("myjosh.in")) {

                var mynewval = url
                try{
                    mynewval = iUtils.extractUrls(mynewval)[0]
                }catch (e:Exception){

                }

                DownloadVideosMain.Start(this, mynewval.trim(), false)


            } else if (url.contains("audiomack")) {
                if (progressDralogGenaratinglink != null) {
                    progressDralogGenaratinglink.dismiss()
                }

                val intent = Intent(this@MainActivity, GetLinkThroughWebview::class.java)
                intent.putExtra("myurlis", url)
                startActivityForResult(intent, 2)

            } else if (url.contains("zili")) {
                if (progressDralogGenaratinglink != null) {
                    progressDralogGenaratinglink.dismiss()
                }

                val intent = Intent(this@MainActivity, GetLinkThroughWebview::class.java)
                intent.putExtra("myurlis", url)
                startActivityForResult(intent, 2)

            } else if (url.contains("xhamster")) {
                if (progressDralogGenaratinglink != null) {
                    progressDralogGenaratinglink.dismiss()
                }

                val intent = Intent(this@MainActivity, GetLinkThroughWebview::class.java)
                intent.putExtra("myurlis", url)
                startActivityForResult(intent, 2)

            }


            else if (url.contains("zingmp3")) {
                if (progressDralogGenaratinglink != null) {
                    progressDralogGenaratinglink.dismiss()
                }

                val intent = Intent(this@MainActivity, GetLinkThroughWebview::class.java)
                intent.putExtra("myurlis", url)
                startActivityForResult(intent, 2)

            } else if (url.contains("vidlit")) {
                if (progressDralogGenaratinglink != null) {
                    progressDralogGenaratinglink.dismiss()
                }

                val intent = Intent(this@MainActivity, GetLinkThroughWebview::class.java)
                intent.putExtra("myurlis", url)
                startActivityForResult(intent, 2)

            } else if (url.contains("byte.co")) {
                if (progressDralogGenaratinglink != null) {
                    progressDralogGenaratinglink.dismiss()
                }

                val intent = Intent(this@MainActivity, GetLinkThroughWebview::class.java)
                intent.putExtra("myurlis", url)
                startActivityForResult(intent, 2)

            } else if (url.contains("fthis.gr")) {
                if (progressDralogGenaratinglink != null) {
                    progressDralogGenaratinglink.dismiss()
                }

                val intent = Intent(this@MainActivity, GetLinkThroughWebview::class.java)
                intent.putExtra("myurlis", url)
                startActivityForResult(intent, 2)

            } else if (url.contains("fw.tv") || url.contains("firework.tv")) {
                if (progressDralogGenaratinglink != null) {
                    progressDralogGenaratinglink.dismiss()
                }

                val intent = Intent(this@MainActivity, GetLinkThroughWebview::class.java)
                intent.putExtra("myurlis", url)
                startActivityForResult(intent, 2)

            } else if (url.contains("rumble")) {
                if (progressDralogGenaratinglink != null) {
                    progressDralogGenaratinglink.dismiss()
                }

                val intent = Intent(this@MainActivity, GetLinkThroughWebview::class.java)
                intent.putExtra("myurlis", url)
                startActivityForResult(intent, 2)

            } else if (url.contains("traileraddict")) {
                if (progressDralogGenaratinglink != null) {
                    progressDralogGenaratinglink.dismiss()
                }

                val intent = Intent(this@MainActivity, GetLinkThroughWebview::class.java)
                intent.putExtra("myurlis", url)
                startActivityForResult(intent, 2)

            }
//            else if (url.contains("veer.tv")) {
//                if (progressDralogGenaratinglink != null) {
//                    progressDralogGenaratinglink.dismiss()
//                }
//
//                val intent = Intent(this@MainActivity, GetLinkThroughWebview::class.java)
//                intent.putExtra("myurlis", url)
//                startActivityForResult(intent, 2)
//
//            }
            //ojoo video app
            else if (url.contains("bemate")) {
                if (progressDralogGenaratinglink != null) {
                    progressDralogGenaratinglink.dismiss()
                }

                var mynewval = url
                try{
                    mynewval = iUtils.extractUrls(mynewval)[0]
                }catch (e:Exception){

                }

                DownloadVideosMain.Start(this, mynewval.trim(), false)
                val intent = Intent(this@MainActivity, GetLinkThroughWebview::class.java)
                intent.putExtra("myurlis", mynewval)
                startActivityForResult(intent, 2)

            } else if (url.contains("chingari")) {


                var mynewval = url
                try{
                    mynewval = iUtils.extractUrls(mynewval)[0]
                }catch (e:Exception){

                }

                DownloadVideosMain.Start(this, mynewval.trim(), false)
            } else if (url.contains("sck.io") || url.contains("snackvideo")) {
                var mynewval = url
                try{
                    mynewval = iUtils.extractUrls(mynewval)[0]
                }catch (e:Exception){

                }

                DownloadVideosMain.Start(this, mynewval.trim(), false)

            } else {
                Log.d("mylogissssss33", "Thebbbbbbbloaded yet.")

                var mynewval = url
                try{
                    mynewval = iUtils.extractUrls(mynewval)[0]
                }catch (e:Exception){

                }

                DownloadVideosMain.Start(this, mynewval.trim(), false)
            }

        }
    }


    private fun showAdDialog() {

        chkAutoDownload.isChecked = true
        val checked = chkAutoDownload.isChecked

        if (checked) {
            Log.e("loged", "testing checked!")
            startClipboardMonitor()
        } else {
            Log.e("loged", "testing unchecked!")


            stopClipboardMonitor()
            // setNofication(false);
        }



        Log.d("TAG", "The rewarded ad wasn't ready yet.")

    }


    //insta finctions

    @Keep
    fun startInstaDownload(Url: String) {


//         https://www.instagram.com/p/CLBM34Rhxek/?igshid=41v6d50y6u4w
//          https://www.instagram.com/p/CLBM34Rhxek/
//           https://www.instagram.com/p/CLBM34Rhxek/?__a=1&__d=dis
//           https://www.instagram.com/tv/CRyVpDSAE59/

        /*
        * https://www.instagram.com/p/CUs4eKIBscn/?__a=1&__d=dis
        * https://www.instagram.com/p/CUktqS7pieg/?__a=1&__d=dis
        * https://www.instagram.com/p/CSMYRwGna3S/?__a=1&__d=dis
        * https://www.instagram.com/p/CR6AbwDB12R/?__a=1&__d=dis
        * https://www.instagram.com/p/CR6AbwDB12R/?__a=1&__d=dis
        * */


        var Urlwi: String?
        try {

            val uri = URI(Url)
            Urlwi = URI(
                uri.scheme,
                uri.authority,
                uri.path,
                null,  // Ignore the query part of the input url
                uri.fragment
            ).toString()


        } catch (ex: java.lang.Exception) {
            Urlwi = ""
            progressDralogGenaratinglink.dismiss()
            ShowToast(this@MainActivity, getString(R.string.invalid_url))
            return
        }

        System.err.println("workkkkkkkkk 1122112 $Url")

        var urlwithoutlettersqp: String? = Urlwi
        System.err.println("workkkkkkkkk 1122112 $urlwithoutlettersqp")

        urlwithoutlettersqp = "$urlwithoutlettersqp?__a=1&__d=dis"
        System.err.println("workkkkkkkkk 87878788 $urlwithoutlettersqp")

        if (urlwithoutlettersqp.contains("/reel/")) {
            urlwithoutlettersqp = urlwithoutlettersqp.replace("/reel/", "/p/")
        }

        if (urlwithoutlettersqp.contains("/tv/")) {
            urlwithoutlettersqp = urlwithoutlettersqp.replace("/tv/", "/p/")
        }
        System.err.println("workkkkkkkkk 777777 $urlwithoutlettersqp")

        AlertDialog.Builder(this@MainActivity)
            .setTitle("Select Server")
            .setCancelable(false)
            .setNegativeButton("Server 1"
            ) { _, _ ->
                try {
                    System.err.println("workkkkkkkkk 4")
                    val sharedPrefsFor = SharedPrefsForInstagram(this@MainActivity)
                    val map = sharedPrefsFor.preference
                    if (map != null && map.preferencE_USERID != null && map.preferencE_USERID != "oopsDintWork" && map.preferencE_USERID != ""
                    ) {
                        System.err.println("workkkkkkkkk 4.7")
                        downloadInstagramImageOrVideodata_old_withlogin(
                            urlwithoutlettersqp,
                            "ds_user_id=" + map.preferencE_USERID
                                    + "; sessionid=" + map.preferencE_USERID
                        )
                    } else {
                        System.err.println("workkkkkkkkk 4.8")
                        downloadInstagramImageOrVideodata_old(
                            urlwithoutlettersqp,
                            ""
                        )
                    }
                } catch (e: java.lang.Exception) {
                    progressDralogGenaratinglink.dismiss()
                    System.err.println("workkkkkkkkk 5")
                    e.printStackTrace()
                    ShowToast(this@MainActivity, getString(R.string.error_occ))
                }
            }.setNeutralButton(
                "Server 1"
            ) { _, _ ->
                try {
                    System.err.println("workkkkkkkkk 4")

                    downloadInstawitfintaApi(urlwithoutlettersqp, iUtils.myfintaTempCookies);
                } catch (e: java.lang.Exception) {
                    progressDralogGenaratinglink.dismiss()
                    System.err.println("workkkkkkkkk 5")
                    e.printStackTrace()
                    ShowToast(this@MainActivity, getString(R.string.error_occ))
                }
            }
            .setPositiveButton(
                "Server 2"
            ) { _, _ ->
                try {
                    System.err.println("workkkkkkkkk 4")
                    val sharedPrefsFor = SharedPrefsForInstagram(this@MainActivity)
                    val map = sharedPrefsFor.preference
                    if (map != null && map.preferencE_USERID != null && map.preferencE_USERID != "oopsDintWork" && map.preferencE_USERID != ""
                    ) {
                        System.err.println("workkkkkkkkk 5.2")
                        downloadInstagramImageOrVideodata_withlogin(
                            urlwithoutlettersqp,
                            "ds_user_id=" + map.preferencE_USERID
                                    + "; sessionid=" + map.preferencE_SESSIONID
                        )
                    } else {
                        System.err.println("workkkkkkkkk 4.5")
                        downloadInstagramImageOrVideodata(
                            urlwithoutlettersqp,
                            iUtils.myInstagramTempCookies
                        )
                    }
                } catch (e: java.lang.Exception) {
                    progressDralogGenaratinglink.dismiss()
                    System.err.println("workkkkkkkkk 5.1")
                    e.printStackTrace()
                    ShowToast(this@MainActivity, getString(R.string.error_occ))
                }
            }.show()
    }

    @Keep
    fun downloadInstawitfintaApi(URL: String?, Cookie: String?) {
        val random1 = Random()
        val j = random1.nextInt(iUtils.UserAgentsList.size)
        object : Thread() {
            override fun run() {
                Looper.prepare()


                val client = OkHttpClient().newBuilder()
                    .build()
                val body: RequestBody = RequestBody.Companion.create(
                    "application/json;charset=UTF-8".toMediaTypeOrNull(),
                    "{\"link\":\"" + URL + "\"}"
                )
                val request: Request = Request.Builder()
                    .url("https://sssinstagram.com/request")
                    .method("POST", body)
                    .addHeader("content-type", "application/json;charset=UTF-8")
                    .addHeader("origin", "https://sssinstagram.com")
                    .addHeader(
                        "user-agent",
                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36"
                    )
                    .addHeader("x-requested-with", "XMLHttpRequest")
                    .addHeader(
                        "x-xsrf-token",
                        "eyJpdiI6IkRnUDdZa2RYOFk1ZmpzemFwN2lwMXc9PSIsInZhbHVlIjoiZU03dlV5TlVvNWowenNrWXkrZmdZa0tpZy9abGs2aENBZnBIUmhLK1FNRmJBb3gyUUswRkY3cUxoMlBjL3l6YUFlM1ZFN1VNeWg2WDZOTzJlZ0xCVHVBY05sSjYwTy9Ca3piRVpGLzM4SU83bEtIeEl6TGRsVGVncXpuM0todWoiLCJtYWMiOiJjMjQ5NWMwMzZmYWM5ZjE1YzhiMTBjNzdlOTAxMTY4MWEwNzAwMWQ1YzQ4NWRhZWE0MDlmZjAwMDJmOWUyNTU3IiwidGFnIjoiIn0=" )
                    .build()
                val response = client.newCall(request).execute()
                val responseed = response.body!!.string()
                println("fjhjfhjsdfsdhf " + response.code)

                if (response.code == 200) {
                    println("fjhjfhjsdfsdhf $responseed")
                    DownloadVideosMain.dismissMyDialog()

                    try {


                        System.err.println(
                            "workkkkkkkkk 6.0.1 " + iUtils.extractUrls(responseed)
                                .toString()
                        )


                        val listofurls = iUtils.extractUrls(responseed)


                        for (i in listofurls) {

                            val i1 = StringEscapeUtils.unescapeJava(i)
                            System.err.println("workkkkkkkkk 7.0.1 " + i1)
                            val nameisfile = iUtils.getImageFilenameFromURL(i1.toString())

                            if (nameisfile.contains(".jpg")) {
                                DownloadFileMain.startDownloading(
                                    this@MainActivity,
                                    i1.toString(),
                                    nameisfile,
                                    ".png"
                                )
                            } else if (nameisfile.contains(".mp4")) {
                                DownloadFileMain.startDownloading(
                                    this@MainActivity,
                                    i1.toString(),
                                    nameisfile,
                                    ".mp4"
                                )
                            }
                            // etText.setText("");
                            try {
                                progressDralogGenaratinglink.dismiss()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }


                    } catch (e: java.lang.Exception) {
                        System.err.println("workkkkkkkkk 5nn errrr " + e.message)

                        progressDralogGenaratinglink.dismiss()
                        System.err.println("workkkkkkkkk 5.1")
                        e.printStackTrace()
                        ShowToast(this@MainActivity, getString(R.string.error_occ))


                    }
                } else {

                    try {
                        val client = OkHttpClient().newBuilder()
                            .build()
                        val request: Request = Request.Builder()
                            .url("https://instadownloader.co/insta_downloader.php?url=" + URL)
                            .method("GET", null)
                            .build()
                        val response = client.newCall(request).execute()
                        if (response.code == 200) {
                            val responseed = response.body!!.string()
                            DownloadVideosMain.dismissMyDialog()

                            var out = StringEscapeUtils.unescapeJava(responseed)
                            if(out.isNotEmpty()) {
                                out = out.substring(1, out.length - 1)
                            }else{

                                try {
                                    progressDralogGenaratinglink.dismiss()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                System.err.println("workkkkkkkkk 5.1")
                                ShowToast(this@MainActivity, getString(R.string.error_occ))
                                return;
                            }
//                            val images_linksList: ArrayList<String> = ArrayList()
//                            val videos_linksList: ArrayList<String> = ArrayList()


                            val videos_links = JSONObject(out).getJSONArray("videos_links")
                            val images_links = JSONObject(out).getJSONArray("images_links")

                            if (videos_links != null) {
                                for (i in 0 until videos_links.length()) {

                                    DownloadFileMain.startDownloading(
                                        this@MainActivity,
                                        videos_links.getJSONObject(0).getString("url"),
                                        "instagram_" + System.currentTimeMillis(),
                                        ".mp4"
                                    )

                                }
                            }

                            if (images_links != null) {
                                for (i in 0 until images_links.length()) {

                                    DownloadFileMain.startDownloading(
                                        this@MainActivity,
                                        images_links.getJSONObject(0).getString("url"),
                                        "instagram_" + System.currentTimeMillis(),
                                        ".png"
                                    )
                                }
                            }

                            try {
                                progressDralogGenaratinglink.dismiss()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            System.err.println("workkkkkkkkk fdffd " + out)

                        } else {
                            progressDralogGenaratinglink.dismiss()
                            System.err.println("workkkkkkkkk 5.1")

                            ShowToast(this@MainActivity, getString(R.string.error_occ))
                        }

                    } catch (e: java.lang.Exception) {
                        System.err.println("workkkkkkkkk 5nn errrr " + e.message)

                        progressDralogGenaratinglink.dismiss()
                        System.err.println("workkkkkkkkk 5.1")
                        e.printStackTrace()
                        ShowToast(this@MainActivity, getString(R.string.error_occ))


                    }
                }


            }
        }.start()
    }

    @Keep
    fun downloadInstagramImageOrVideodata_old(URL: String?, Cookie: String?) {
        val random1 = Random()
        val j = random1.nextInt(iUtils.UserAgentsList.size)
        object : Thread() {
            override fun run() {
                Looper.prepare()
                val client = OkHttpClient().newBuilder()
                    .build()
                val request: Request = Request.Builder()
                    .url(URL!!)
                    .method("GET", null)
                    .addHeader("Cookie", Cookie!!)
                    .addHeader(
                        "User-Agent",
                        iUtils.UserAgentsList[j]
                    )
                    .build()
                try {
                    val response = client.newCall(request).execute()

                    System.err.println("workkkkkkkkk 6 " + response.code)

                    if (response.code == 200) {

                        try {
                            val ressss = response.body!!.string()
//                            runOnUiThread {
//                                binding.etURL.setText(ressss.substring(100, 2000))
//
//                            }

                            val listType: Type =
                                object : TypeToken<ModelInstagramResponse?>() {}.type
                            val modelInstagramResponse: ModelInstagramResponse = Gson().fromJson(
                                ressss,
                                listType
                            )

                            if (modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children != null) {
                                val modelGetEdgetoNode: ModelGetEdgetoNode =
                                    modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children
                                myInstaUsername = modelInstagramResponse.modelGraphshortcode.shortcode_media.owner.username+"_"

                                val modelEdNodeArrayList: List<ModelEdNode> =
                                    modelGetEdgetoNode.modelEdNodes
                                for (i in modelEdNodeArrayList.indices) {
                                    if (modelEdNodeArrayList[i].modelNode.isIs_video) {
                                        myVideoUrlIs = modelEdNodeArrayList[i].modelNode.video_url
                                        DownloadFileMain.startDownloading(
                                            this@MainActivity,
                                            myVideoUrlIs,
                                            myInstaUsername+iUtils.getVideoFilenameFromURL(myVideoUrlIs),
                                            ".mp4"
                                        )
                                        // etText.setText("");
                                        try{
                                            progressDralogGenaratinglink.dismiss()
                                        }catch (e:Exception){
                                            e.printStackTrace()
                                        }
                                        myVideoUrlIs = ""
                                    } else {
                                        myPhotoUrlIs =
                                            modelEdNodeArrayList[i].modelNode.display_resources[modelEdNodeArrayList[i].modelNode.display_resources.size - 1].src
                                        DownloadFileMain.startDownloading(
                                            this@MainActivity,
                                            myPhotoUrlIs,
                                            myInstaUsername+iUtils.getImageFilenameFromURL(myPhotoUrlIs),
                                            ".png"
                                        )
                                        myPhotoUrlIs = ""
                                        try{
                                            progressDralogGenaratinglink.dismiss()
                                        }catch (e:Exception){
                                            e.printStackTrace()
                                        }
                                        // etText.setText("");
                                    }
                                }
                            } else {
                                val isVideo =
                                    modelInstagramResponse.modelGraphshortcode.shortcode_media.isIs_video

                                myInstaUsername = modelInstagramResponse.modelGraphshortcode.shortcode_media.owner.username+"_"

                                if (isVideo) {
                                    myVideoUrlIs =
                                        modelInstagramResponse.modelGraphshortcode.shortcode_media.video_url
                                    DownloadFileMain.startDownloading(
                                        this@MainActivity,
                                        myVideoUrlIs,
                                        myInstaUsername + iUtils.getVideoFilenameFromURL(
                                            myVideoUrlIs
                                        ),
                                        ".mp4"
                                    )
                                    try {
                                        progressDralogGenaratinglink.dismiss()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    myVideoUrlIs = ""
                                } else {
                                    myPhotoUrlIs =
                                        modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources[modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources.size - 1].src
                                    DownloadFileMain.startDownloading(
                                        this@MainActivity,
                                        myPhotoUrlIs,
                                        myInstaUsername + iUtils.getImageFilenameFromURL(
                                            myPhotoUrlIs
                                        ),
                                        ".png"
                                    )
                                    try {
                                        progressDralogGenaratinglink.dismiss()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    myPhotoUrlIs = ""
                                }
                            }
                        } catch (e: java.lang.Exception) {
                            System.err.println("workkkkkkkkk 5nn errrr " + e.message)
                            try{
                                try {
                                    System.err.println("workkkkkkkkk 4")
                                    downloadInstagramImageOrVideodata(
                                        URL, ""
                                    )
                                } catch (e: java.lang.Exception) {
                                    progressDralogGenaratinglink.dismiss()
                                    System.err.println("workkkkkkkkk 5.1")
                                    e.printStackTrace()
                                    ShowToast(this@MainActivity, getString(R.string.error_occ))
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                this@MainActivity.runOnUiThread {
                                    progressDralogGenaratinglink.dismiss()

                                    if (!this@MainActivity.isFinishing) {
                                        val alertDialog =
                                            AlertDialog.Builder(this@MainActivity)
                                                .create()
                                        alertDialog.setTitle(getString(R.string.logininsta))
                                        alertDialog.setMessage(getString(R.string.urlisprivate))
                                        alertDialog.setButton(
                                            AlertDialog.BUTTON_POSITIVE,
                                            getString(R.string.logininsta)
                                        ) { dialog, _ ->
                                            dialog.dismiss()
                                            val intent = Intent(
                                                this@MainActivity,
                                                InstagramLoginActivity::class.java
                                            )
                                            startActivityForResult(intent, 200)
                                        }
                                        alertDialog.setButton(
                                            AlertDialog.BUTTON_NEGATIVE,
                                            getString(R.string.cancel)
                                        ) { dialog, _ ->
                                            dialog.dismiss()
                                        }
                                        alertDialog.show()
                                    }
                                }
                            }
                        }

                    } else {
                        object : Thread() {
                            override fun run() {

                                val client = OkHttpClient().newBuilder()
                                    .build()
                                val request: Request = Request.Builder()
                                    .url(URL)
                                    .method("GET", null)
                                    .addHeader("Cookie", iUtils.myInstagramTempCookies)
                                    .addHeader(
                                        "User-Agent",
                                        iUtils.UserAgentsList[j]
                                    ).build()
                                try {
                                    val response1: Response = client.newCall(request).execute()
                                    System.err.println("workkkkkkkkk 6 1 " + response1.code)

                                    if (response1.code == 200) {
                                        try {
                                            val listType: Type =
                                                object :
                                                    TypeToken<ModelInstagramResponse?>() {}.type
                                            val modelInstagramResponse: ModelInstagramResponse =
                                                Gson().fromJson(
                                                    response1.body!!.string(),
                                                    listType
                                                )
                                            if (modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children != null) {
                                                val modelGetEdgetoNode: ModelGetEdgetoNode =
                                                    modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children
                                                myInstaUsername = modelInstagramResponse.modelGraphshortcode.shortcode_media.owner.username+"_"

                                                val modelEdNodeArrayList: List<ModelEdNode> =
                                                    modelGetEdgetoNode.modelEdNodes
                                                for (i in modelEdNodeArrayList.indices) {
                                                    if (modelEdNodeArrayList[i].modelNode.isIs_video) {
                                                        myVideoUrlIs =
                                                            modelEdNodeArrayList[i].modelNode.video_url
                                                        DownloadFileMain.startDownloading(
                                                            this@MainActivity,
                                                            myVideoUrlIs,
                                                            myInstaUsername+iUtils.getVideoFilenameFromURL(
                                                                myVideoUrlIs
                                                            ),
                                                            ".mp4"
                                                        )
                                                        // etText.setText("");
                                                        try{
                                                            progressDralogGenaratinglink.dismiss()
                                                        }catch (e:Exception){
                                                            e.printStackTrace()
                                                        }
                                                        myVideoUrlIs = ""
                                                    } else {
                                                        myPhotoUrlIs =
                                                            modelEdNodeArrayList[i].modelNode.display_resources[modelEdNodeArrayList[i].modelNode.display_resources.size - 1].src
                                                        DownloadFileMain.startDownloading(
                                                            this@MainActivity,
                                                            myPhotoUrlIs,
                                                            myInstaUsername+iUtils.getImageFilenameFromURL(
                                                                myPhotoUrlIs
                                                            ),
                                                            ".png"
                                                        )
                                                        myPhotoUrlIs = ""
                                                        try{
                                                            progressDralogGenaratinglink.dismiss()
                                                        }catch (e:Exception){
                                                            e.printStackTrace()
                                                        }
                                                        // etText.setText("");
                                                    }
                                                }
                                            } else {
                                                val isVideo =
                                                    modelInstagramResponse.modelGraphshortcode.shortcode_media.isIs_video
                                                myInstaUsername = modelInstagramResponse.modelGraphshortcode.shortcode_media.owner.username+"_"

                                                if (isVideo) {
                                                    myVideoUrlIs =
                                                        modelInstagramResponse.modelGraphshortcode.shortcode_media.video_url
                                                    DownloadFileMain.startDownloading(
                                                        this@MainActivity,
                                                        myVideoUrlIs,
                                                        myInstaUsername+iUtils.getVideoFilenameFromURL(myVideoUrlIs),
                                                        ".mp4"
                                                    )
                                                    try{
                                                        progressDralogGenaratinglink.dismiss()
                                                    }catch (e:Exception){
                                                        e.printStackTrace()
                                                    }
                                                    myVideoUrlIs = ""
                                                } else {
                                                    myPhotoUrlIs =
                                                        modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources[modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources.size - 1].src
                                                    DownloadFileMain.startDownloading(
                                                        this@MainActivity,
                                                        myPhotoUrlIs,
                                                        myInstaUsername+iUtils.getImageFilenameFromURL(myPhotoUrlIs),
                                                        ".png"
                                                    )
                                                    try{
                                                        progressDralogGenaratinglink.dismiss()
                                                    }catch (e:Exception){
                                                        e.printStackTrace()
                                                    }
                                                    myPhotoUrlIs = ""
                                                }
                                            }
                                        } catch
                                            (e: java.lang.Exception) {
                                            System.err.println("workkkkkkkkk 4vvv errrr " + e.message)
                                            e.printStackTrace()
                                            try{
                                                progressDralogGenaratinglink.dismiss()
                                            }catch (e:Exception){
                                                e.printStackTrace()
                                            }
                                        }
                                    } else {
                                        System.err.println("workkkkkkkkk 6bbb errrr ")
                                        this@MainActivity.runOnUiThread {
                                            progressDralogGenaratinglink.dismiss()

                                            if (!this@MainActivity.isFinishing) {
                                                val alertDialog =
                                                    AlertDialog.Builder(this@MainActivity)
                                                        .create()
                                                alertDialog.setTitle(getString(R.string.logininsta))
                                                alertDialog.setMessage(getString(R.string.urlisprivate))
                                                alertDialog.setButton(
                                                    AlertDialog.BUTTON_POSITIVE,
                                                    getString(R.string.logininsta)
                                                ) { dialog, _ ->
                                                    dialog.dismiss()
                                                    val intent = Intent(
                                                        this@MainActivity,
                                                        InstagramLoginActivity::class.java
                                                    )
                                                    startActivityForResult(intent, 200)
                                                }
                                                alertDialog.setButton(
                                                    AlertDialog.BUTTON_NEGATIVE,
                                                    getString(R.string.cancel)
                                                ) { dialog, _ ->
                                                    dialog.dismiss()
                                                }
                                                alertDialog.show()
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }.start()
                    }
                    println("working errpr \t Value: " + response.body!!.string())
                } catch (e: Exception) {
                    try {
                        println("response1122334455:   " + "Failed1 " + e.message)
                        progressDralogGenaratinglink.dismiss()
                    } catch (e: Exception) {

                    }
                }
            }
        }.start()
    }

    @Keep
    fun downloadInstagramImageOrVideodata(URL: String?, Coookie: String?) {

        val random1 = Random()
        val j = random1.nextInt(iUtils.UserAgentsList.size)
        var Cookie = Coookie
        if (TextUtils.isEmpty(Cookie)) {
            Cookie = ""
        }
        val apiService: RetrofitApiInterface =
            RetrofitClient.getClient()

        val callResult: Call<JsonObject> = apiService.getInstagramData(
            URL,
            Cookie,
            iUtils.UserAgentsList[j]
        )
        callResult.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(
                call: Call<JsonObject?>,
                response: retrofit2.Response<JsonObject?>
            ) {
                println("response1122334455 ress :   " + response.body())
                try {


//                                val userdata = response.body()!!.getAsJsonObject("graphql")
//                                    .getAsJsonObject("shortcode_media")
//                                binding.profileFollowersNumberTextview.setText(
//                                    userdata.getAsJsonObject(
//                                        "edge_followed_by"
//                                    )["count"].asString
//                                )
//                                binding.profileFollowingNumberTextview.setText(
//                                    userdata.getAsJsonObject(
//                                        "edge_follow"
//                                    )["count"].asString
//                                )
//                                binding.profilePostNumberTextview.setText(userdata.getAsJsonObject("edge_owner_to_timeline_media")["count"].asString)
//                                binding.profileLongIdTextview.setText(userdata["username"].asString)
//


                    val listType = object : TypeToken<ModelInstagramResponse?>() {}.type
                    val modelInstagramResponse: ModelInstagramResponse? = GsonBuilder().create()
                        .fromJson<ModelInstagramResponse>(
                            response.body().toString(),
                            listType
                        )
                    if (modelInstagramResponse != null) {
                        if (modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children != null) {
                            val modelGetEdgetoNode: ModelGetEdgetoNode =
                                modelInstagramResponse.modelGraphshortcode.shortcode_media.edge_sidecar_to_children
                            myInstaUsername = modelInstagramResponse.modelGraphshortcode.shortcode_media.owner.username+"_"

                            val modelEdNodeArrayList: List<ModelEdNode> =
                                modelGetEdgetoNode.modelEdNodes
                            for (i in modelEdNodeArrayList.indices) {
                                if (modelEdNodeArrayList[i].modelNode.isIs_video) {
                                    myVideoUrlIs =
                                        modelEdNodeArrayList[i].modelNode.video_url
                                    DownloadFileMain.startDownloading(
                                        this@MainActivity,
                                        myVideoUrlIs,
                                        myInstaUsername+iUtils.getVideoFilenameFromURL(
                                            myVideoUrlIs
                                        ),
                                        ".mp4"
                                    )
                                    // etText.setText("");
                                    try{
                                        progressDralogGenaratinglink.dismiss()
                                    }catch (e:Exception){
                                        e.printStackTrace()
                                    }
                                    myVideoUrlIs = ""
                                } else {
                                    myPhotoUrlIs =
                                        modelEdNodeArrayList[i].modelNode.display_resources[modelEdNodeArrayList[i].modelNode.display_resources.size - 1].src
                                    DownloadFileMain.startDownloading(
                                        this@MainActivity,
                                        myPhotoUrlIs,
                                        myInstaUsername+iUtils.getImageFilenameFromURL(
                                            myPhotoUrlIs
                                        ),
                                        ".png"
                                    )
                                    myPhotoUrlIs = ""
                                    try{
                                        progressDralogGenaratinglink.dismiss()
                                    }catch (e:Exception){
                                        e.printStackTrace()
                                    }
                                    // etText.setText("");
                                }
                            }
                        } else {
                            val isVideo =
                                modelInstagramResponse.modelGraphshortcode.shortcode_media.isIs_video
                            myInstaUsername = modelInstagramResponse.modelGraphshortcode.shortcode_media.owner.username+"_"

                            if (isVideo) {
                                myVideoUrlIs =
                                    modelInstagramResponse.modelGraphshortcode.shortcode_media.video_url
                                DownloadFileMain.startDownloading(
                                    this@MainActivity,
                                    myVideoUrlIs,
                                    myInstaUsername+iUtils.getVideoFilenameFromURL(myVideoUrlIs),
                                    ".mp4"
                                )
                                try{
                                    progressDralogGenaratinglink.dismiss()
                                }catch (e:Exception){
                                    e.printStackTrace()
                                }
                                myVideoUrlIs = ""
                            } else {
                                myPhotoUrlIs =
                                    modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources[modelInstagramResponse.modelGraphshortcode.shortcode_media.display_resources.size - 1].src
                                DownloadFileMain.startDownloading(
                                    this@MainActivity,
                                    myPhotoUrlIs,
                                    myInstaUsername+iUtils.getImageFilenameFromURL(myPhotoUrlIs),
                                    ".png"
                                )
                                try{
                                    progressDralogGenaratinglink.dismiss()
                                }catch (e:Exception){
                                    e.printStackTrace()
                                }
                                myPhotoUrlIs = ""
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            resources.getString(R.string.somthing),
                            Toast.LENGTH_SHORT
                        ).show()
                        try{
                            progressDralogGenaratinglink.dismiss()
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
                } catch (e: java.lang.Exception) {
                    try{
                        try {
                            System.err.println("workkkkkkkkk 4")

                            downloadInstagramImageOrVideodata(
                                URL, "")
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            println("response1122334455 exe 1:   " + e.localizedMessage)
                            try{
                                progressDralogGenaratinglink.dismiss()
                            }catch (e:Exception){
                                e.printStackTrace()
                            }
                            System.err.println("workkkkkkkkk 5.1")
                            e.printStackTrace()
                            ShowToast(this@MainActivity, getString(R.string.error_occ))
                        }

                    }catch (e:Exception)
                    {
                        e.printStackTrace()
                        this@MainActivity.runOnUiThread {
                            progressDralogGenaratinglink.dismiss()
                            if (!this@MainActivity.isFinishing) {
                                e.printStackTrace()
                                Toast.makeText(
                                    this@MainActivity,
                                    resources.getString(R.string.somthing),
                                    Toast.LENGTH_SHORT
                                ).show()
                                println("response1122334455 exe 1:   " + e.localizedMessage)
                                try{
                                    progressDralogGenaratinglink.dismiss()
                                }catch (e:Exception){
                                    e.printStackTrace()
                                }
                                val alertDialog = AlertDialog.Builder(this@MainActivity).create()
                                alertDialog.setTitle(getString(R.string.logininsta))
                                alertDialog.setMessage(getString(R.string.urlisprivate))
                                alertDialog.setButton(
                                    AlertDialog.BUTTON_POSITIVE, getString(R.string.logininsta)
                                ) { dialog, _ ->
                                    dialog.dismiss()
                                    val intent = Intent(
                                        this@MainActivity,
                                        InstagramLoginActivity::class.java
                                    )
                                    startActivityForResult(intent, 200)
                                }
                                alertDialog.setButton(
                                    AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel)
                                ) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                alertDialog.show()
                            }
                        }
                    }
                }
            }
            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                try{
                    println("response1122334455:   " + "Failed0" + t.message)
                    progressDralogGenaratinglink.dismiss()
                    Toast.makeText(
                        this@MainActivity,
                        resources.getString(R.string.somthing),
                        Toast.LENGTH_SHORT
                    ).show()
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        })
    }

    @Keep
    fun downloadInstagramImageOrVideodata_old_withlogin(URL: String?, Cookie: String?) {
        val random1 = Random()
        val j = random1.nextInt(iUtils.UserAgentsList.size)
        object : Thread() {
            override fun run() {
                Looper.prepare()
                val client = OkHttpClient().newBuilder()
                    .build()
                val request: Request = Request.Builder()
                    .url(URL!!)
                    .method("GET", null)
                    .addHeader("Cookie", Cookie!!)
                    .addHeader(
                        "User-Agent",
                        iUtils.UserAgentsList[j]
                    )
                    .build()
                try {
                    val response = client.newCall(request).execute()

                    if (response.code == 200) {

                        val ress = response.body!!.string()
                        println("working errpr \t Value: $ress")

                        try {
                            val listType: Type =
                                object : TypeToken<ModelInstaWithLogin?>() {}.type
                            val modelInstagramResponse: ModelInstaWithLogin = Gson().fromJson(
                                ress,
                                listType
                            )
                            println("workkkkk777 " + modelInstagramResponse.items[0].code)

                            if (modelInstagramResponse.items[0].mediaType == 8) {
                                myInstaUsername = modelInstagramResponse.items[0].user.username+"_"

                                val modelGetEdgetoNode = modelInstagramResponse.items[0]
                                val modelEdNodeArrayList: List<CarouselMedia> =
                                    modelGetEdgetoNode.carouselMedia
                                for (i in modelEdNodeArrayList.indices) {
                                    if (modelEdNodeArrayList[i].mediaType == 2) {
                                        myVideoUrlIs =
                                            modelEdNodeArrayList[i].videoVersions[0].geturl()
                                        DownloadFileMain.startDownloading(
                                            this@MainActivity,
                                            myVideoUrlIs,
                                            myInstaUsername+iUtils.getVideoFilenameFromURL(myVideoUrlIs),
                                            ".mp4"
                                        )
                                        // etText.setText("");
                                        try{
                                            progressDralogGenaratinglink.dismiss()
                                        }catch (e:Exception){
                                            e.printStackTrace()
                                        }
                                        myVideoUrlIs = ""
                                    } else {
                                        myPhotoUrlIs =
                                            modelEdNodeArrayList[i].imageVersions2.candidates[0]
                                                .geturl()
                                        DownloadFileMain.startDownloading(
                                            this@MainActivity,
                                            myPhotoUrlIs,
                                            myInstaUsername+iUtils.getVideoFilenameFromURL(myPhotoUrlIs),
                                            ".png"
                                        )
                                        myPhotoUrlIs = ""
                                        try{
                                            progressDralogGenaratinglink.dismiss()
                                        }catch (e:Exception){
                                            e.printStackTrace()
                                        }
                                        // etText.setText("");
                                    }
                                }
                            } else {
                                val modelGetEdgetoNode = modelInstagramResponse.items[0]
                                myInstaUsername = modelInstagramResponse.items[0].user.username+"_"

                                if (modelGetEdgetoNode.mediaType == 2) {
                                    myVideoUrlIs =
                                        modelGetEdgetoNode.videoVersions[0].geturl()
                                    DownloadFileMain.startDownloading(
                                        this@MainActivity,
                                        myVideoUrlIs,
                                        myInstaUsername+iUtils.getVideoFilenameFromURL(myVideoUrlIs),
                                        ".mp4"
                                    )
                                    try{
                                        progressDralogGenaratinglink.dismiss()
                                    }catch (e:Exception){
                                        e.printStackTrace()
                                    }
                                    myVideoUrlIs = ""
                                } else {
                                    myPhotoUrlIs =
                                        modelGetEdgetoNode.imageVersions2.candidates[0].geturl()
                                    DownloadFileMain.startDownloading(
                                        this@MainActivity,
                                        myPhotoUrlIs,
                                        myInstaUsername+iUtils.getVideoFilenameFromURL(myPhotoUrlIs),

                                        ".png"
                                    )
                                    try{
                                        progressDralogGenaratinglink.dismiss()
                                    }catch (e:Exception){
                                        e.printStackTrace()
                                    }
                                    myPhotoUrlIs = ""
                                }
                            }
                        } catch (e: java.lang.Exception) {
                            System.err.println("workkkkkkkkk 5nn errrr " + e.message)
                            try{
                                try {
                                    System.err.println("workkkkkkkkk 4")

                                    val sharedPrefsFor = SharedPrefsForInstagram(this@MainActivity)
                                    val map = sharedPrefsFor.preference
                                    if (map != null && map.preferencE_USERID != null && map.preferencE_USERID != "oopsDintWork" && map.preferencE_USERID != ""
                                    ) {
                                        System.err.println("workkkkkkkkk 5.2")
                                        downloadInstagramImageOrVideodata(
                                            URL, "ds_user_id=" + map.preferencE_USERID
                                                    + "; sessionid=" + map.preferencE_SESSIONID
                                        )
                                    }else{
                                        progressDralogGenaratinglink.dismiss()
                                        System.err.println("workkkkkkkkk 5.1")
                                        e.printStackTrace()
                                        ShowToast(this@MainActivity, getString(R.string.error_occ))
                                    }
                                } catch (e: java.lang.Exception) {
                                    progressDralogGenaratinglink.dismiss()
                                    System.err.println("workkkkkkkkk 5.1")
                                    e.printStackTrace()
                                    ShowToast(this@MainActivity, getString(R.string.error_occ))
                                }
                            }catch (e:Exception) {
                                e.printStackTrace()
                                try {
                                    this@MainActivity.runOnUiThread {
                                        progressDralogGenaratinglink.dismiss()
                                        if (!this@MainActivity.isFinishing) {
                                            val alertDialog =
                                                AlertDialog.Builder(this@MainActivity).create()
                                            alertDialog.setTitle(getString(R.string.logininsta))
                                            alertDialog.setMessage(getString(R.string.urlisprivate))
                                            alertDialog.setButton(
                                                AlertDialog.BUTTON_POSITIVE,
                                                getString(R.string.logininsta)
                                            ) { dialog, _ ->
                                                dialog.dismiss()
                                                val intent = Intent(
                                                    this@MainActivity,
                                                    InstagramLoginActivity::class.java
                                                )
                                                startActivityForResult(intent, 200)
                                            }
                                            alertDialog.setButton(
                                                AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel)
                                            ) { dialog, _ ->
                                                dialog.dismiss()
                                            }
                                            alertDialog.show()
                                        }
                                    }
                                }catch (e: Exception){

                                }
                            }
                        }
                    } else
                    {
                        object : Thread() {
                            override fun run() {

                                val client = OkHttpClient().newBuilder()
                                    .build()
                                val request: Request = Request.Builder()
                                    .url(URL)
                                    .method("GET", null)
                                    .addHeader("Cookie", iUtils.myInstagramTempCookies)
                                    .addHeader(
                                        "User-Agent",
                                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36"
                                    ).build()
                                try {
                                    val response1: Response = client.newCall(request).execute()

                                    if (response1.code == 200) {

                                        try {
                                            val listType: Type =
                                                object : TypeToken<ModelInstaWithLogin?>() {}.type
                                            val modelInstagramResponse: ModelInstaWithLogin =
                                                Gson().fromJson(
                                                    response.body!!.string(),
                                                    listType
                                                )
                                            if (modelInstagramResponse.items[0].mediaType == 8) {
                                                myInstaUsername = modelInstagramResponse.items[0].user.username+"_"

                                                val modelGetEdgetoNode =
                                                    modelInstagramResponse.items[0]
                                                val modelEdNodeArrayList: List<CarouselMedia> =
                                                    modelGetEdgetoNode.carouselMedia
                                                for (i in modelEdNodeArrayList.indices) {
                                                    if (modelEdNodeArrayList[i].mediaType == 2) {
                                                        myVideoUrlIs =
                                                            modelEdNodeArrayList[i].videoVersions[0].geturl()
                                                        DownloadFileMain.startDownloading(
                                                            this@MainActivity,
                                                            myVideoUrlIs,
                                                            iUtils.getVideoFilenameFromURL(myVideoUrlIs),
                                                            ".mp4"
                                                        )
                                                        // etText.setText("");
                                                        try{
                                                            progressDralogGenaratinglink.dismiss()
                                                        }catch (e:Exception){
                                                            e.printStackTrace()
                                                        }
                                                        myVideoUrlIs = ""
                                                    } else {
                                                        myPhotoUrlIs =
                                                            modelEdNodeArrayList[i].imageVersions2.candidates[0].geturl()
                                                        DownloadFileMain.startDownloading(
                                                            this@MainActivity,
                                                            myPhotoUrlIs,
                                                            myInstaUsername+iUtils.getVideoFilenameFromURL(myPhotoUrlIs),
                                                            ".png"
                                                        )
                                                        myPhotoUrlIs = ""
                                                        try{
                                                            progressDralogGenaratinglink.dismiss()
                                                        }catch (e:Exception){
                                                            e.printStackTrace()
                                                        }
                                                        // etText.setText("");
                                                    }
                                                }
                                            } else {
                                                myInstaUsername = modelInstagramResponse.items[0].user.username+"_"

                                                val modelGetEdgetoNode =
                                                    modelInstagramResponse.items[0]
                                                if (modelGetEdgetoNode.mediaType == 2) {
                                                    myVideoUrlIs =
                                                        modelGetEdgetoNode.videoVersions[0]
                                                            .geturl()
                                                    DownloadFileMain.startDownloading(
                                                        this@MainActivity,
                                                        myVideoUrlIs,
                                                        myInstaUsername+iUtils.getVideoFilenameFromURL(myVideoUrlIs),
                                                        ".mp4"
                                                    )
                                                    try{
                                                        progressDralogGenaratinglink.dismiss()
                                                    }catch (e:Exception){
                                                        e.printStackTrace()
                                                    }
                                                    myVideoUrlIs = ""
                                                } else {
                                                    myPhotoUrlIs =
                                                        modelGetEdgetoNode.imageVersions2.candidates[0].geturl()
                                                    DownloadFileMain.startDownloading(
                                                        this@MainActivity,
                                                        myPhotoUrlIs,
                                                        myInstaUsername+iUtils.getVideoFilenameFromURL(myPhotoUrlIs),
                                                        ".png"
                                                    )
                                                    try{
                                                        progressDralogGenaratinglink.dismiss()
                                                    }catch (e:Exception){
                                                        e.printStackTrace()
                                                    }
                                                    myPhotoUrlIs = ""
                                                }
                                            }
                                        } catch (e: java.lang.Exception) {
                                            System.err.println("workkkkkkkkk 5nn errrr " + e.message)
                                            e.printStackTrace()
                                            try {
                                                this@MainActivity.runOnUiThread {
                                                    progressDralogGenaratinglink.dismiss()

                                                    if (!this@MainActivity.isFinishing) {
                                                        val alertDialog =
                                                            AlertDialog.Builder(this@MainActivity)
                                                                .create()
                                                        alertDialog.setTitle(getString(R.string.logininsta))
                                                        alertDialog.setMessage(getString(R.string.urlisprivate))
                                                        alertDialog.setButton(
                                                            AlertDialog.BUTTON_POSITIVE,
                                                            getString(R.string.logininsta)
                                                        ) { dialog, _ ->
                                                            dialog.dismiss()
                                                            val intent = Intent(
                                                                this@MainActivity,
                                                                InstagramLoginActivity::class.java
                                                            )
                                                            startActivityForResult(intent, 200)
                                                        }
                                                        alertDialog.setButton(
                                                            AlertDialog.BUTTON_NEGATIVE,
                                                            getString(R.string.cancel)
                                                        ) { dialog, _ ->
                                                            dialog.dismiss()
                                                        }
                                                        alertDialog.show()
                                                    }
                                                }
                                            }catch (e:Exception){
                                                e.printStackTrace()
                                            }
                                        }
                                    } else {
                                        System.err.println("workkkkkkkkk 6bbb errrr ")
                                        this@MainActivity.runOnUiThread {
                                            progressDralogGenaratinglink.dismiss()

                                            if (!this@MainActivity.isFinishing) {
                                                val alertDialog =
                                                    AlertDialog.Builder(this@MainActivity)
                                                        .create()
                                                alertDialog.setTitle(getString(R.string.logininsta))
                                                alertDialog.setMessage(getString(R.string.urlisprivate))
                                                alertDialog.setButton(
                                                    AlertDialog.BUTTON_POSITIVE,
                                                    getString(R.string.logininsta)
                                                ) { dialog, _ ->
                                                    dialog.dismiss()
                                                    val intent = Intent(
                                                        this@MainActivity,
                                                        InstagramLoginActivity::class.java
                                                    )
                                                    startActivityForResult(intent, 200)
                                                }
                                                alertDialog.setButton(
                                                    AlertDialog.BUTTON_NEGATIVE,
                                                    getString(R.string.cancel)
                                                ) { dialog, _ ->
                                                    dialog.dismiss()
                                                }
                                                alertDialog.show()
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }.start()
                    }
                }
                catch (e: Exception)
                {
                    try {
                        println("response1122334455:   " + "Failed1 " + e.message)
                        progressDralogGenaratinglink.dismiss()
                    } catch (e: Exception) {

                    }
                }
            }
        }.start()
    }

    @Keep
    fun downloadInstagramImageOrVideodata_withlogin(URL: String?, Cookie: String?) {
        /*instagram product types
        * product_type
        *
        * igtv "media_type": 2
        * carousel_container  "media_type": 8
        * clips  "media_type": 2
        * feed   "media_type": 1
        * */

        val random1 = Random()
        val j = random1.nextInt(iUtils.UserAgentsList.size)

        var Cookie = Cookie
        if (TextUtils.isEmpty(Cookie)) {
            Cookie = ""
        }
        val apiService: RetrofitApiInterface =
            RetrofitClient.getClient()

        val callResult: Call<JsonObject> = apiService.getInstagramData(
            URL,
            Cookie,
            iUtils.UserAgentsList[j]
        )
        callResult.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(
                call: Call<JsonObject?>,
                response: retrofit2.Response<JsonObject?>
            ) {

                try {
                    val listType: Type =
                        object : TypeToken<ModelInstaWithLogin?>() {}.type
                    val modelInstagramResponse: ModelInstaWithLogin = Gson().fromJson(
                        response.body(),
                        listType
                    )
                    println("workkkkk777 " + modelInstagramResponse.items[0].code)

                    if (modelInstagramResponse.items[0].mediaType == 8) {
                        myInstaUsername = modelInstagramResponse.items[0].user.username+"_"

                        val modelGetEdgetoNode = modelInstagramResponse.items[0]

                        val modelEdNodeArrayList: List<CarouselMedia> =
                            modelGetEdgetoNode.carouselMedia
                        for (i in modelEdNodeArrayList.indices) {
                            if (modelEdNodeArrayList[i].mediaType == 2) {
                                myVideoUrlIs =
                                    modelEdNodeArrayList[i].videoVersions[0].geturl()
                                DownloadFileMain.startDownloading(
                                    this@MainActivity,
                                    myVideoUrlIs,
                                    myInstaUsername+iUtils.getVideoFilenameFromURL(myVideoUrlIs),
                                    ".mp4"
                                )
                                // etText.setText("");
                                try{
                                    progressDralogGenaratinglink.dismiss()
                                }catch (e:Exception){
                                    e.printStackTrace()
                                }
                                myVideoUrlIs = ""
                            } else {
                                myPhotoUrlIs =
                                    modelEdNodeArrayList[i].imageVersions2.candidates[0]
                                        .geturl()
                                DownloadFileMain.startDownloading(
                                    this@MainActivity,
                                    myPhotoUrlIs,
                                    myInstaUsername+iUtils.getVideoFilenameFromURL(myPhotoUrlIs),
                                    ".png"
                                )
                                myPhotoUrlIs = ""
                                try{
                                    progressDralogGenaratinglink.dismiss()
                                }catch (e:Exception){
                                    e.printStackTrace()
                                }
                                // etText.setText("");
                            }
                        }
                    } else {
                        val modelGetEdgetoNode = modelInstagramResponse.items[0]
                        myInstaUsername = modelInstagramResponse.items[0].user.username+"_"

                        if (modelGetEdgetoNode.mediaType == 2) {
                            myVideoUrlIs =
                                modelGetEdgetoNode.videoVersions[0].geturl()
                            DownloadFileMain.startDownloading(
                                this@MainActivity,
                                myVideoUrlIs,
                                myInstaUsername+iUtils.getVideoFilenameFromURL(myVideoUrlIs),
                                ".mp4"
                            )
                            try{
                                progressDralogGenaratinglink.dismiss()
                            }catch (e:Exception){
                                e.printStackTrace()
                            }
                            myVideoUrlIs = ""
                        } else {
                            myPhotoUrlIs =
                                modelGetEdgetoNode.imageVersions2.candidates[0].geturl()
                            DownloadFileMain.startDownloading(
                                this@MainActivity,
                                myPhotoUrlIs,
                                myInstaUsername+iUtils.getVideoFilenameFromURL(myPhotoUrlIs),
                                ".png"
                            )
                            try{
                                progressDralogGenaratinglink.dismiss()
                            }catch (e:Exception){
                                e.printStackTrace()
                            }
                            myPhotoUrlIs = ""
                        }
                    }
                } catch (e: java.lang.Exception) {
                    System.err.println("workkkkkkkkk 5nn errrr " + e.message)

                    try{

                        try {
                            System.err.println("workkkkkkkkk 4")

                            val sharedPrefsFor = SharedPrefsForInstagram(this@MainActivity)
                            val map = sharedPrefsFor.preference
                            if (map != null && map.preferencE_USERID != null && map.preferencE_USERID != "oopsDintWork" && map.preferencE_USERID != ""
                            ) {
                                System.err.println("workkkkkkkkk 5.2")
                                downloadInstagramImageOrVideodata_old(
                                    URL, "ds_user_id=" + map.preferencE_USERID
                                            + "; sessionid=" + map.preferencE_SESSIONID
                                )
                            }else{
                                progressDralogGenaratinglink.dismiss()
                                System.err.println("workkkkkkkkk 5.1")
                                e.printStackTrace()
                                ShowToast(this@MainActivity, getString(R.string.error_occ))
                            }
                        } catch (e: java.lang.Exception) {
                            progressDralogGenaratinglink.dismiss()
                            System.err.println("workkkkkkkkk 5.1")
                            e.printStackTrace()
                            ShowToast(this@MainActivity, getString(R.string.error_occ))
                        }

                    }catch (e:Exception){

                        e.printStackTrace()
                        this@MainActivity.runOnUiThread {
                            progressDralogGenaratinglink.dismiss()
                            if (!this@MainActivity.isFinishing) {
                                val alertDialog =
                                    AlertDialog.Builder(this@MainActivity)
                                        .create()
                                alertDialog.setTitle(getString(R.string.logininsta))
                                alertDialog.setMessage(getString(R.string.urlisprivate))
                                alertDialog.setButton(
                                    AlertDialog.BUTTON_POSITIVE,
                                    getString(R.string.logininsta)
                                ) { dialog, _ ->
                                    dialog.dismiss()
                                    val intent = Intent(
                                        this@MainActivity,
                                        InstagramLoginActivity::class.java
                                    )
                                    startActivityForResult(intent, 200)
                                }
                                alertDialog.setButton(
                                    AlertDialog.BUTTON_NEGATIVE,
                                    getString(R.string.cancel)
                                ) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                alertDialog.show()
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                println("response1122334455:   " + "Failed0")
                try{
                    progressDralogGenaratinglink.dismiss()
                }catch (e:Exception){
                    e.printStackTrace()
                }
                Toast.makeText(
                    this@MainActivity,
                    resources.getString(R.string.somthing),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }






    private fun showAdmobAds() {
        if (Constants.show_Ads) {
            if (nn == "nnn") {
                AdsManager.loadInterstitialAd(this@MainActivity)

            }
        }
    }


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }

    companion object {
        lateinit var myString: String
    }



}