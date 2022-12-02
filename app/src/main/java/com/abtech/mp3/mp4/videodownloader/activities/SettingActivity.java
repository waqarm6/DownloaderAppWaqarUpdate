package com.abtech.mp3.mp4.videodownloader.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.abtech.mp3.mp4.videodownloader.BuildConfig;
import com.abtech.mp3.mp4.videodownloader.R;
import com.abtech.mp3.mp4.videodownloader.databinding.ActivitySettingBinding;
import com.abtech.mp3.mp4.videodownloader.utils.LocaleHelper;
import com.abtech.mp3.mp4.videodownloader.utils.iUtils;

import java.util.Locale;


public class SettingActivity extends AppCompatActivity {

    private ActivitySettingBinding binding;
    ImageView backbutton;
    SwitchCompat darkmode;
    TextView darktext,moreappstext,sharetext,mailtext,abouttext,privacytext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("lang_pref", MODE_PRIVATE);
        String lang = prefs.getString("lang", Locale.getDefault().getLanguage());//"No name defined" is the default value.
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        binding.moreapps.setOnClickListener(new View.OnClickListener() {            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=" + getString(R.string.google_developer_id))));
                } catch (android.content.ActivityNotFoundException anfe) {
                    Toast.makeText(SettingActivity.this, getString(R.string.error_occ), Toast.LENGTH_SHORT).show();
                }

            }
        });

        darkmode    = findViewById(R.id.darkmodeswitch);
        darktext    = findViewById(R.id.darktext);
        moreappstext    = findViewById(R.id.moreappstext);
        sharetext     = findViewById(R.id.sharetext);
        abouttext      = findViewById(R.id.abouttext);
        mailtext       = findViewById(R.id.mailtext);
        privacytext     = findViewById(R.id.privacytext);
        LinearLayout main_layout = findViewById(R.id.main_layout);

        if(iUtils.checked)
        {
            darkmode.setChecked(true);
        }
        darkmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                iUtils.checked = darkmode.isChecked();

                if (!iUtils.checked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    iUtils.checked = false;
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    iUtils.checked = true;
                }
            }
        });
        backbutton = findViewById(R.id.backarrow);

        int nightModeFlags =
                getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                binding.darkmode.setCardBackgroundColor(getResources().getColor(R.color.lightblack));
                binding.moreapps.setCardBackgroundColor(getResources().getColor(R.color.lightblack));
                binding.shareapp.setCardBackgroundColor(getResources().getColor(R.color.lightblack));
                binding.mailussuggsions.setCardBackgroundColor(getResources().getColor(R.color.lightblack));
                binding.aboutuspage.setCardBackgroundColor(getResources().getColor(R.color.lightblack));
                binding.privacypolicy.setCardBackgroundColor(getResources().getColor(R.color.lightblack));
                binding.ratetheapp.setCardBackgroundColor(getResources().getColor(R.color.lightblack));
                darktext.setTextColor(getResources().getColor(R.color.white));
                moreappstext.setTextColor(getResources().getColor(R.color.white));
                sharetext.setTextColor(getResources().getColor(R.color.white));
                mailtext.setTextColor(getResources().getColor(R.color.white));
                abouttext.setTextColor(getResources().getColor(R.color.white));
                privacytext.setTextColor(getResources().getColor(R.color.white));
                main_layout.setBackgroundColor(getResources().getColor(R.color.black));


                break;

            case Configuration.UI_MODE_NIGHT_NO:
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                break;
        }

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });


        binding.ratetheapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }

            }
        });


        binding.shareapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Mp4 Video Downloader");
                    String shareMessage= "\nLet me recommend you this application\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }

            }
        });

        binding.mailussuggsions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"" + getString(R.string.supportemail)});
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.suggs));
                i.putExtra(Intent.EXTRA_TEXT, getString(R.string.entermesss));
                try {
                    startActivity(Intent.createChooser(i, " "));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(SettingActivity.this, getString(R.string.error_occ), Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.aboutuspage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, AboutActivity.class));

            }
        });
        binding.privacypolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("" + getString(R.string.privacy_policy_url))));
                } catch (android.content.ActivityNotFoundException anfe) {
                    Toast.makeText(SettingActivity.this, getString(R.string.error_occ), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }



}