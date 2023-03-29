package com.rih.easymoney.activity;

import static com.rih.easymoney.Config.adMobAds;
import static com.rih.easymoney.Config.appLovinAds;
import static com.rih.easymoney.Config.mocoins;
import static com.rih.easymoney.Config.userAchievementsUrl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.rih.easymoney.MainActivity;
import com.rih.easymoney.R;

public class QuizResultActivity extends AppCompatActivity implements MaxAdListener {

    private int gameScore;
    private TextView txtMocionsNew;

    private ProgressDialog pDialog;
    private String email ,password;
    private SharedPreferences prefs;
    private int mocouns;
    private int pointsAdded;
    private int theCategory;
    private InterstitialAd interstitialAd;
    private MaxInterstitialAd maxInterstitialAd;
    private int retryAttempt;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        setToolbar();

        // AdMob Ads
        if (adMobAds.contains("True"))
        {
            LoadAds();

            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });

            AdView adView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }


        // AppLovin Ads
        AppLovinSdk.getInstance( this ).setMediationProvider( "max" );
        AppLovinSdk.initializeSdk( this, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration)
            {
                if(appLovinAds.contains("True"))
                {
                    ShowMaxBannerAd();
                }
            }
        } );


        LoadMaxInterstitialAd();

        TextView rightAnswersText = findViewById(R.id.txt_score);
        TextView mocionsText = findViewById(R.id.txt_mocions);
        txtMocionsNew = findViewById(R.id.txt_mocionsNew);

        prefs = this.getSharedPreferences("User", Context.MODE_PRIVATE);
        email = prefs.getString("userEmail", "");
        password = prefs.getString("userPassword", "");

        Bundle resultBundle = getIntent().getExtras();
        gameScore = resultBundle.getInt("theResult");
        pointsAdded = resultBundle.getInt("thePoints");
        theCategory = resultBundle.getInt("theCategory");

        rightAnswersText.setText("" + gameScore);
        mocionsText.setText("" + pointsAdded);


        openProgressBar();
        new SendRequest().execute();

        checkAchievements();
    }

    public class SendRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try{
                URL url = new URL(mocoins);

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("email", email);
                postDataParams.put("password", password);
                postDataParams.put("mocions", pointsAdded);
                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK)
                {
                    BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while((line = in.readLine()) != null)
                    {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    return sb.toString();
                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {

            Pattern pattern2 = Pattern.compile("M1(.*?)M2");
            Matcher matcher2 = pattern2.matcher(result);

            if (matcher2.find())
            {
                mocouns = Integer.parseInt(matcher2.group(1));
                mocouns = mocouns + pointsAdded;
                txtMocionsNew.setText("" + mocouns);
            }

            if (result.contains("Sorry, your email or password is incorrect"))
            {
                Intent intent = new Intent(QuizResultActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

            if(appLovinAds.contains("True"))
            {
                ShowMaxInterstitialAd();
            }
            showInterstitial();
            closeProgressBar();
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.toolbar_result);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    public void PlayAgainClick(View view)
    {
        if (theCategory == 1)
        {
            Intent intent = new Intent(QuizResultActivity.this, MathQuizActivity.class);
            startActivity(intent);
            finish();
            showInterstitial();
        }
        if (theCategory == 2)
        {
            Intent intent = new Intent(QuizResultActivity.this, TrueFalseMathQuizActivity.class);
            startActivity(intent);
            finish();
            showInterstitial();
        }

    }

    public void BackClick(View view)
    {
        Intent intent = new Intent(QuizResultActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        showInterstitial();
    }

    protected void openProgressBar(){
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.loading));
        pDialog.show();
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
    }

    protected void closeProgressBar(){
        pDialog.dismiss();
    }


    public void LoadAds()
    {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                this,
                getString(R.string.admob_interstitial_id),
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        QuizResultActivity.this.interstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");

                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        QuizResultActivity.this.interstitialAd = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        QuizResultActivity.this.interstitialAd = null;
                                        Log.d("TAG", "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {

                                        Log.d("TAG", "The ad was shown.");

                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.getMessage());
                        interstitialAd = null;
                    }
                });
    }

    private void showInterstitial() {
        // Show the ad if it's ready.
        if (interstitialAd != null) {
            interstitialAd.show(this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showInterstitial();

        if(appLovinAds.contains("True"))
        {
            ShowMaxInterstitialAd();
        }

        finish();
    }

    public void ShowMaxBannerAd(){
        MaxAdView maxBannerAdView = findViewById(R.id.MaxAdView);
        maxBannerAdView.loadAd();
    }

    private void ShowMaxInterstitialAd()
    {
        if ( maxInterstitialAd.isReady() )
        {
            maxInterstitialAd.showAd();
        }
    }

    public void LoadMaxInterstitialAd(){
        maxInterstitialAd = new MaxInterstitialAd( getString(R.string.appLovin_interstitial_id), this );
        maxInterstitialAd.setListener( this );

        maxInterstitialAd.loadAd();
    }

    // MAX Ad Listener
    @Override
    public void onAdLoaded(final MaxAd maxAd)
    {
        retryAttempt = 0;
    }

    @Override
    public void onAdLoadFailed(final String adUnitId, final MaxError error)
    {
        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis( (long) Math.pow( 2, Math.min( 6, retryAttempt ) ) );

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                maxInterstitialAd.loadAd();
            }
        }, delayMillis );
        // Toast.makeText(getApplicationContext(), "onAdLoadFailed: " + error.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAdDisplayFailed(final MaxAd maxAd, final MaxError error)
    {
        // Toast.makeText(getApplicationContext(), "onAdLoadFailed: " + error.getMessage(), Toast.LENGTH_LONG).show();
        // Interstitial ad failed to display. We recommend loading the next ad
        maxInterstitialAd.loadAd();
    }

    @Override
    public void onAdDisplayed(final MaxAd maxAd) {
    }

    @Override
    public void onAdClicked(final MaxAd maxAd) {}

    @Override
    public void onAdHidden(final MaxAd maxAd)
    {
        // Interstitial ad is hidden. Pre-load the next ad
        maxInterstitialAd.loadAd();
    }


    private void checkAchievements()
    {
        if (gameScore >= 10 && theCategory == 1)
        {
            sendRequest("1");
        }
        if (gameScore >= 30 && theCategory == 1) {
            sendRequest("2");
        }
        if (gameScore >= 15 && theCategory == 2) {
            sendRequest("3");
        }
    }
    public void sendRequest(String achID)
    {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, userAchievementsUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                Log.e("SetAchievementsResponse", response);
            }
        }, error -> {
            //This code is executed if there is an error.
            Toast.makeText(getApplicationContext(), "error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            closeProgressBar();
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<>();
                MyData.put("email", email);
                MyData.put("action", "Set");
                MyData.put("achID", achID);
                return MyData;
            }
        };

        MyRequestQueue.add(MyStringRequest);
    }
}
