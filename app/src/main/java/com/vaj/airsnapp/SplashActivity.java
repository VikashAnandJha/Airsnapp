package com.vaj.airsnapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        TextView versionTv=findViewById(R.id.versionText);
        versionTv.setText("v"+Config.APP_VERSION);


        findViewById(R.id.launchCameraBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashActivity.this,CameraActivity.class));
            }
        });

        findViewById(R.id.viewInAirGallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.showToast(SplashActivity.this,"Coming Soon..");
            }
        });

    }
}