package com.abtech.mp3.mp4.videodownloader.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abtech.mp3.mp4.videodownloader.R;
import com.abtech.mp3.mp4.videodownloader.databinding.ActivityAboutUsBinding;

public class AboutActivity extends AppCompatActivity {

    private ActivityAboutUsBinding binding;
    ImageView backbutton;
    LinearLayout main_layout;
    TextView company_text,des_text;
    CardView aboutcard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutUsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.img.setOnClickListener(new View.OnClickListener() {
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

        backbutton = findViewById(R.id.backarrow);
        des_text    =findViewById(R.id.textdesc);
        company_text   = findViewById(R.id.companytext);
        aboutcard   = findViewById(R.id.aboutcard);
        main_layout    = findViewById(R.id.main_layout);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

        int nightModeFlags =
                getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                aboutcard.setCardBackgroundColor(getResources().getColor(R.color.lightblack));
                company_text.setTextColor(getResources().getColor(R.color.white));
                des_text.setTextColor(getResources().getColor(R.color.white));
                main_layout.setBackgroundColor(getResources().getColor(R.color.black));


                break;

            case Configuration.UI_MODE_NIGHT_NO:
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                break;
        }
    }
}