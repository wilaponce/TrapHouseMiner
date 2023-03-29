package com.rih.easymoney.activity;

import static com.rih.easymoney.Config.privacyPolicyUrl;
import static com.rih.easymoney.Config.websiteUrl;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.rih.easymoney.BuildConfig;
import com.rih.easymoney.R;

public class AboutActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView toolbarTitle;
    TextView appVerion;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setToolbar();

        appVerion = findViewById(R.id.app_verion);
        appVerion.setText("" + BuildConfig.VERSION_NAME);
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.toolbarTitle = findViewById(R.id.toolbar_title);
        this.toolbarTitle.setText(R.string.toolbar_about);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    public void websiteClick(View view)
    {
        Intent pp = new Intent(Intent.ACTION_VIEW);
        pp.setData(Uri.parse(websiteUrl));
        startActivity(pp);
    }

    public void tandClick(View view)
    {
        Intent pp = new Intent(Intent.ACTION_VIEW);
        pp.setData(Uri.parse(privacyPolicyUrl));
        startActivity(pp);
    }

}