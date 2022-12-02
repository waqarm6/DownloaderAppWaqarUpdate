package com.abtech.mp3.mp4.videodownloader.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.abtech.mp3.mp4.videodownloader.R;
import com.abtech.mp3.mp4.videodownloader.databinding.ActivityRateUsWebViewBinding;
import com.abtech.mp3.mp4.videodownloader.utils.LocaleHelper;



public class RateWebView extends AppCompatActivity {


    private ActivityRateUsWebViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_us_web_view);

        binding = ActivityRateUsWebViewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent i = getIntent();
        String url = i.getStringExtra("link");
        binding.webviewView.loadUrl(url);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        newBase = LocaleHelper.onAttach(newBase);
        super.attachBaseContext(newBase);
    }
}