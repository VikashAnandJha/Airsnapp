package com.vaj.airsnapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Roboto-Bold.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

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