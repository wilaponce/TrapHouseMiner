package com.rih.easymoney.activity;

import static com.rih.easymoney.Config.adMobAds;
import static com.rih.easymoney.Config.appLovinAds;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.rih.easymoney.R;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class GamesActivity extends AppCompatActivity {

    Button btn_mathQuiz, btn_TrueFalseQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        setToolbar();
        setBannerAds();

        btn_mathQuiz = findViewById(R.id.btn_mathQuiz);
        btn_TrueFalseQuiz= findViewById(R.id.btn_TrueFalseQuiz);

        btn_mathQuiz.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), MathQuizActivity.class);
            startActivity(intent);
        });

        btn_TrueFalseQuiz.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), TrueFalseMathQuizActivity.class);
            startActivity(intent);
        });
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.toolbar_games);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private void setBannerAds()
    {
        // Applovin Ads
        AppLovinSdk.getInstance( this ).setMediationProvider( "max" );
        AppLovinSdk.initializeSdk( this, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration)
            {
                // AppLovin SDK is initialized, start loading ads
                if(appLovinAds.contains("True"))
                {
                    ShowMaxBannerAd();
                }
            }
        } );

        if(adMobAds.contains("True")){
            MobileAds.initialize(this, initializationStatus -> {
            });
            AdView mAdView = findViewById(R.id.adView);
            @SuppressLint("VisibleForTests")
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
    }

    private void ShowMaxBannerAd()
    {
        MaxAdView maxBannerAdView = findViewById(R.id.MaxAdView);
        maxBannerAdView.loadAd();
    }
}