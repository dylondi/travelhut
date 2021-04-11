package com.example.travelhut.views.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.travelhut.R;
import com.example.travelhut.views.onboarding.OnboardingScreensActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private ImageView logo, appName, splashImage;
    private LottieAnimationView lottieAnimationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        logo = findViewById(R.id.logo_white);
        appName = findViewById(R.id.travelhut_text_white);
        splashImage = findViewById(R.id.background);
        lottieAnimationView = findViewById(R.id.lottie);


        splashScreenAnimation();
        Runnable r = () -> {
            // if you are redirecting from a fragment then use getActivity() as the context.
            startActivity(new Intent(SplashScreenActivity.this, OnboardingScreensActivity.class));

        };


        Handler h = new Handler();
        // The Runnable will be executed after the given delay time
        h.postDelayed(r, 2600); // will be delayed for 1.5 seconds

    }

    //This method animates each view of th splash screen
    private void splashScreenAnimation() {
        splashImage.animate().translationY(-4600).setDuration(700).setStartDelay(2000);
        logo.animate().translationY(3400).setDuration(700).setStartDelay(2000);
        appName.animate().translationY(3400).setDuration(700).setStartDelay(2000);
        lottieAnimationView.animate().translationY(3400).setDuration(700).setStartDelay(2000);
    }


}