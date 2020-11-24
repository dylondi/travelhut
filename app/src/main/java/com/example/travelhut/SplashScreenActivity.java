package com.example.travelhut;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView logo, appName, splashImage;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        logo = findViewById(R.id.logo_white);
        appName = findViewById(R.id.travelhut_text_white);
        splashImage = findViewById(R.id.background);
        lottieAnimationView = findViewById(R.id.lottie);


        splashImage.animate().translationY(-3600).setDuration(1000).setStartDelay(4000);
        logo.animate().translationY(2400).setDuration(1000).setStartDelay(4000);
        appName.animate().translationY(2400).setDuration(1000).setStartDelay(4000);
        lottieAnimationView.animate().translationY(2400).setDuration(1000).setStartDelay(4000);

    }
}