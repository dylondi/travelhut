package com.example.travelhut.views.onboarding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.travelhut.R;
import com.example.travelhut.viewmodel.OnboardingScreensViewModel;
import com.example.travelhut.views.authentication.SlideViewPagerAdapter;
import com.example.travelhut.views.authentication.LoginActivity;
import com.example.travelhut.views.main.newsfeed.NewsFeedActivity;

public class OnboardingScreensActivity extends AppCompatActivity {

    public static ViewPager viewPager;
    SlideViewPagerAdapter adapter;
    OnboardingScreensViewModel onboardingScreensViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_screens);

        viewPager = findViewById(R.id.viewPager);
        adapter = new SlideViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        onboardingScreensViewModel = ViewModelProviders.of(this).get(OnboardingScreensViewModel.class);
        onboardingScreensViewModel.getUserMutableLiveData().observe(this, firebaseUser -> {
            if(firebaseUser != null){
                Intent myIntent = new Intent(this, NewsFeedActivity.class);
                startActivity(myIntent);
            }
        });

        if(isOpenAlready()){
            Intent intent = new Intent(OnboardingScreensActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
            startActivity(intent);
        }else{
            SharedPreferences.Editor editor = getSharedPreferences("slide", MODE_PRIVATE).edit();
            editor.putBoolean("slide", true);
            editor.commit();
        }
    }

    private boolean isOpenAlready(){
        SharedPreferences sharedPreferences = getSharedPreferences("slide", MODE_PRIVATE);
        boolean result = sharedPreferences.getBoolean("slide", false);
        return result;
    }
}