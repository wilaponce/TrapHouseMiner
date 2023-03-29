 package com.rih.easymoney.activity;

import static com.rih.easymoney.Config.userAchievementsUrl;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rih.easymoney.R;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

 public class AchievementsActivity extends AppCompatActivity {

     CardView cv_achievement_1, cv_achievement_2, cv_achievement_3;
     ProgressDialog pDialog;
     SharedPreferences prefs;
     String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        setToolbar();
        iniViews();

        prefs = this.getSharedPreferences("User", Context.MODE_PRIVATE);
        userEmail = prefs.getString("userEmail", "");

        openProgressBar();
        sendRequest();
    }

     private void setToolbar() {
         Toolbar toolbar = findViewById(R.id.toolbar);
         setSupportActionBar(toolbar);
         TextView toolbarTitle = findViewById(R.id.toolbar_title);
         toolbarTitle.setText(R.string.toolbar_achievements);
         ActionBar actionBar = getSupportActionBar();
         actionBar.setDisplayHomeAsUpEnabled(true);
         actionBar.setHomeButtonEnabled(false);
         actionBar.setDisplayShowTitleEnabled(false);
     }

     private void iniViews(){
         cv_achievement_1 = findViewById(R.id.cv_achievement_1);
         cv_achievement_2 = findViewById(R.id.cv_achievement_2);
         cv_achievement_3 = findViewById(R.id.cv_achievement_3);
     }

     public void sendRequest()
     {
         RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
         StringRequest MyStringRequest = new StringRequest(Request.Method.POST, userAchievementsUrl, new Response.Listener<String>() {
             @Override
             public void onResponse(String response)
             {
                 Pattern pattern = Pattern.compile("N1(.*?)N2");
                 Pattern pattern2 = Pattern.compile("M1(.*?)M2");
                 Pattern pattern3 = Pattern.compile("E1(.*?)E2");
                 Matcher matcher = pattern.matcher(response);
                 Matcher matcher2 = pattern2.matcher(response);
                 Matcher matcher3 = pattern3.matcher(response);

                 if (matcher.find() && matcher2.find() && matcher3.find())
                 {
                     if (String.valueOf(matcher.group(1)).equals("1"))
                     {
                         cv_achievement_1.setAlpha(1);
                     }
                     if (String.valueOf(matcher2.group(1)).equals("1"))
                     {
                         cv_achievement_2.setAlpha(1);
                     }
                     if (String.valueOf(matcher3.group(1)).equals("1"))
                     {
                         cv_achievement_3.setAlpha(1);
                     }
                 }

                 closeProgressBar();
                 Log.e("AchievementsResponse", response);
             }
         }, error -> {
             //This code is executed if there is an error.
             Toast.makeText(getApplicationContext(), "error: " + error.getMessage(), Toast.LENGTH_LONG).show();
             closeProgressBar();
         }) {
             protected Map<String, String> getParams() {
                 Map<String, String> MyData = new HashMap<>();
                 MyData.put("email", userEmail);
                 MyData.put("action", "Get");
                 return MyData;
             }
         };

         MyRequestQueue.add(MyStringRequest);
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
}