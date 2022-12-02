package com.abtech.mp3.mp4.videodownloader.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.abtech.mp3.mp4.videodownloader.R;
import com.github.appintro.AppIntroCustomLayoutFragment;
import com.github.appintro.AppIntroPageTransformerType;

public class AppIntro extends com.github.appintro.AppIntro {

    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sharedPreferences = getSharedPreferences("intro", Context.MODE_PRIVATE);

        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro1));
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro2));
        addSlide(AppIntroCustomLayoutFragment.newInstance(R.layout.intro3));



        // Fade Transition
        setTransformer(AppIntroPageTransformerType.Fade.INSTANCE);

        // Show/hide status bar
        showStatusBar(false);

        //Speed up or down scrolling
        setScrollDurationFactor(2);

        setColorDoneText(getResources().getColor(R.color.black));
        setColorSkipButton(getResources().getColor(R.color.black));

        //Enable the color "fade" animation between two slides (make sure the slide implements SlideBackgroundColorHolder)
        // setColorTransitionsEnabled(true);

        //Prevent the back button from exiting the slides
        setSystemBackButtonLocked(true);

        //Activate wizard mode (Some aesthetic changes)
        setWizardMode(true);

        //Show/hide skip button
        setSkipButtonEnabled(false);

        //Enable immersive mode (no status and nav bar)
        setImmersiveMode();

        //Enable/disable page indicators
        setIndicatorEnabled(true);

        //Dhow/hide ALL buttons
        setButtonsEnabled(true);


    }

    @Override


    protected void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);

        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean("intro",true);
        edit.apply();

        Intent i = new Intent(AppIntro.this,MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean("intro",true);
        edit.apply();

        Intent i = new Intent(AppIntro.this,MainActivity.class);
        startActivity(i);
        finish();
    }
}

