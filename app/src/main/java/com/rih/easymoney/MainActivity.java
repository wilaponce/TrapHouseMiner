package com.rih.easymoney;

import static com.rih.easymoney.Config.adMobAds;
import static com.rih.easymoney.Config.adUnitId;
import static com.rih.easymoney.Config.appLovinAds;
import static com.rih.easymoney.Config.mocoins;
import static com.rih.easymoney.Config.testMode;
import static com.rih.easymoney.Config.unityGameID;
import static com.rih.easymoney.Config.userProfile;
import static com.rih.easymoney.Config.videoPoints;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
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
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import com.rih.easymoney.activity.AchievementsActivity;
import com.rih.easymoney.activity.GamesActivity;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rih.easymoney.activity.AboutActivity;
import com.rih.easymoney.activity.FeedbackActivity;
import com.rih.easymoney.activity.HelpActivity;
import com.rih.easymoney.activity.LoginActivity;
import com.rih.easymoney.activity.PaymentActivity;
import com.rih.easymoney.activity.ProfileActivity;
import com.rih.easymoney.activity.ProofActivity;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;

public class MainActivity extends AppCompatActivity implements MaxAdViewAdListener, IUnityAdsInitializationListener {

    private TextView userName, mocoinsTxt;
    LinearLayout startPlay, paymentsProof, payment, howItWorks, feedback, share, aboutApp, RateBtn
            , videoUnityBtn, myAchievementsBtn;
    SharedPreferences prefs;
    private ProgressDialog pDialog;
    private String email ,password;
    private MaxAdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userName = findViewById(R.id.userName_Txt);
        mocoinsTxt = findViewById(R.id.Mocoins_Txt);

        startPlay = findViewById(R.id.Start_playBtn);
        videoUnityBtn = findViewById(R.id.videoUnityBtn);
        myAchievementsBtn = findViewById(R.id.myAchievementsBtn);
        paymentsProof = findViewById(R.id.Payments_proofBtn);
        payment = findViewById(R.id.PaymentBtn);
        howItWorks = findViewById(R.id.HowItWorksBtn);
        feedback = findViewById(R.id.FeedbackBtn);
        share = findViewById(R.id.ShareBtn);
        aboutApp = findViewById(R.id.AboutAppBtn);
        RateBtn = findViewById(R.id.RateBtn);

        setToolbar();
        setBannerAds();

        // Firebase Notification Topic
        FirebaseMessaging.getInstance().subscribeToTopic("general");
        //FirebaseMessaging.getInstance().setAutoInitEnabled(true);

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


        prefs = this.getSharedPreferences("User", Context.MODE_PRIVATE);
        email = prefs.getString("userEmail", "");
        password = prefs.getString("userPassword", "");


        startPlay.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GamesActivity.class);
            startActivity(intent);
        });

        videoUnityBtn.setOnClickListener(v -> DisplayRewardedAd());

        myAchievementsBtn.setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, AchievementsActivity.class);
            startActivity(intent);
        });

        paymentsProof.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ProofActivity.class);
            startActivity(intent);
        });

        payment.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
            startActivity(intent);
        });

        howItWorks.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(intent);
        });

        feedback.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, FeedbackActivity.class);
            startActivity(intent);
        });

        RateBtn.setOnClickListener(v -> {
            try {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        });

        share.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            String sAux = getString(R.string.Share_Text);
            sAux = sAux + "https://play.google.com/store/apps/details?id=" + getPackageName();
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "choose one"));
        });

        aboutApp.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        openProgressBar();
        new SendRequest().execute();

        //Unity Ads
        // Initialize the SDK:
        UnityAds.initialize(getApplicationContext(), unityGameID, testMode, this);
    }



    private void ShowMaxBannerAd()
    {
        MaxAdView maxBannerAdView = findViewById(R.id.MaxAdView);
        maxBannerAdView.loadAd();
    }

    private void setBannerAds()
    {

        if (adMobAds.contains("True"))
        {
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });

            AdView adView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }

    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.toolbar_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onAdExpanded(MaxAd ad) {

    }

    @Override
    public void onAdCollapsed(MaxAd ad) {

    }

    @Override
    public void onAdLoaded(MaxAd ad) {

    }

    @Override
    public void onAdDisplayed(MaxAd ad) {

    }

    @Override
    public void onAdHidden(MaxAd ad) {

    }

    @Override
    public void onAdClicked(MaxAd ad) {

    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {

    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {

    }

    public class SendRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try{
                URL url = new URL(userProfile);

                JSONObject postDataParams = new JSONObject();

                postDataParams.put("email", email);
                postDataParams.put("password", password);

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

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while((line = in.readLine()) != null) {

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

            Pattern pattern = Pattern.compile("N1(.*?)N2");
            Pattern pattern2 = Pattern.compile("M1(.*?)M2");
            Matcher matcher = pattern.matcher(result);
            Matcher matcher2 = pattern2.matcher(result);

            if (matcher.find() && matcher2.find())
            {
                userName.setText("Welcome, " + matcher.group(1));
                mocoinsTxt.setText("" + matcher2.group(1));
            }
            if (result.contains("Sorry, your email or password is incorrect"))
            {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            if (result.equals("0"))
            {
                unconfermedAccountDialog();
            }
            if(result.contains("Contact me for this error: support@ibrahimodeh.com"))
            {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }

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

    private void unconfermedAccountDialog()
    {
        AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);
        build.setTitle("Sorry !");
        build.setMessage("You Have Problem in your Account, please contact us on our email.");
        build.setCancelable(false);

        build.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
                finish();
            }
        });

        AlertDialog olustur = build.create();
        olustur.show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        //openProgressBar();
        new SendRequest().execute();
    }

    public class SendRequest3 extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try{
                URL url = new URL(mocoins);

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("email", email);
                postDataParams.put("password", password);
                postDataParams.put("mocions", videoPoints);
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
                int mocouns;
                mocouns = Integer.parseInt(matcher2.group(1));
                mocouns = mocouns + videoPoints;
                mocoinsTxt.setText("" + mocouns);
            }

            if (result.contains("Sorry, your email or password is incorrect"))
            {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

            closeProgressBar();

        }
    }


    @Override
    public void onInitializationComplete() {
        //DisplayRewardedAd();
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
        Log.e("UnityAdsExample", "Unity Ads initialization failed with error: [" + error + "] " + message);

    }
    // Implement a function to load a rewarded ad. The ad will start to show once the ad has been loaded.
    public void DisplayRewardedAd () {
        UnityAds.load(adUnitId, loadListener);
    }
    // Unity ads
    private final IUnityAdsLoadListener loadListener = new IUnityAdsLoadListener() {
        @Override
        public void onUnityAdsAdLoaded(String placementId) {
            UnityAds.show(MainActivity.this, adUnitId, new UnityAdsShowOptions(), showListener);
        }

        @Override
        public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
            Log.e("UnityAdsExample", "Unity Ads failed to load ad for " + placementId + " with error: [" + error + "] " + message);
            Toast.makeText(getApplicationContext(), "No Video Ads, Try Again Later",  Toast.LENGTH_LONG).show();
        }
    };

    private final IUnityAdsShowListener showListener = new IUnityAdsShowListener() {
        @Override
        public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
            Log.e("UnityAdsExample", "Unity Ads failed to show ad for " + placementId + " with error: [" + error + "] " + message);
            Toast.makeText(getApplicationContext(), "No Video Ads, Try Again Later",  Toast.LENGTH_LONG).show();
        }

        @Override
        public void onUnityAdsShowStart(String placementId) {
            Log.v("UnityAdsExample", "onUnityAdsShowStart: " + placementId);
        }

        @Override
        public void onUnityAdsShowClick(String placementId) {
            Log.v("UnityAdsExample", "onUnityAdsShowClick: " + placementId);
        }

        @Override
        public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
            Log.v("UnityAdsExample", "onUnityAdsShowComplete: " + placementId);
            if (state.equals(UnityAds.UnityAdsShowCompletionState.COMPLETED)) {
                // Reward the user for watching the ad to completion
                openProgressBar();
                new SendRequest3().execute();
            } else {
                // Do not reward the user for skipping the ad
            }
        }
    };


}