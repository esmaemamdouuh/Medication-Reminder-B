package com.example.medicalreminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // بعد 3 ثوانٍ، اذهب إلى شاشة Login
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish(); // لا نعود إلى هذه الشاشة مرة أخرى
            }
        }, 3000); // 3000 مللي ثانية = 3 ثوانٍ
    }
}