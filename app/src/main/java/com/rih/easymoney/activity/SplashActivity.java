package com.rih.easymoney.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.rih.easymoney.MainActivity;
import com.rih.easymoney.MyApplication;
import com.rih.easymoney.R;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private ImageView logo;
    private String userEmail, userPassword;
    private SharedPreferences prefs;
    private ProgressBar progressBar;
    private LinearLayout lytNoConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        logo = findViewById(R.id.logo);
        progressBar = findViewById(R.id.progress_bar);
        lytNoConnection = findViewById(R.id.lyt_no_connection);

        progressBar.setVisibility(View.GONE);
        lytNoConnection.setVisibility(View.GONE);

        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        logo.startAnimation(myanim);

        prefs = this.getSharedPreferences("User", Context.MODE_PRIVATE);
        userEmail = prefs.getString("userEmail", "");
        userPassword = prefs.getString("userPassword", "");

        int splashTimeOut = 2500;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (isConnectingToInternet(SplashActivity.this)) {
                    Application application = getApplication();
                    // If the application is not an instance of MyApplication, log an error message and
                    // start the MainActivity without showing the app open ad.
                    if (!(application instanceof MyApplication)) {
                        Log.e("MyApplication", "Failed to cast application to MyApplication.");
                        getTheData();
                        return;
                    }

                    // Show the app open ad.
                    ((MyApplication) application)
                            .showAdIfAvailable(
                                    SplashActivity.this,
                                    new MyApplication.OnShowAdCompleteListener() {
                                        @Override
                                        public void onShowAdComplete() {
                                            getTheData();
                                        }
                                    });
                } else {
                    showNoInterNet();
                }

            }
        }, splashTimeOut);


        lytNoConnection.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            lytNoConnection.setVisibility(View.GONE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isConnectingToInternet(SplashActivity.this)) {
                        getTheData();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        lytNoConnection.setVisibility(View.VISIBLE);
                    }
                }
            }, 1000);
        });

    }

    private void getTheData() {
        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }
    }

    private void showNoInterNet() {
        logo.setVisibility(View.GONE);

        progressBar.setVisibility(View.GONE);
        lytNoConnection.setVisibility(View.VISIBLE);

    }

    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity =
                (ConnectivityManager) context.getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }


}
