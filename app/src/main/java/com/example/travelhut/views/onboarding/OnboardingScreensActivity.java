package com.example.travelhut.views.onboarding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.travelhut.R;
import com.example.travelhut.viewmodel.OnboardingScreensViewModel;
import com.example.travelhut.views.authentication.RegisterLoginActivity;
import com.example.travelhut.views.main.newsfeed.NewsFeedActivity;
import com.example.travelhut.views.onboarding.util.SlideViewPagerAdapter;

public class OnboardingScreensActivity extends AppCompatActivity {

    //Instance Variables
    public static ViewPager viewPager;
    private SlideViewPagerAdapter adapter;
    private OnboardingScreensViewModel onboardingScreensViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_screens);

        //Initialize viewPager and adapter and set adapter to viewPager
        viewPager = findViewById(R.id.viewPager);
        adapter = new SlideViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        //Initialize ViewModel
        onboardingScreensViewModel = ViewModelProviders.of(this).get(OnboardingScreensViewModel.class);

        //Observe LiveData object which indicates whether the user is already signed in
        onboardingScreensViewModel.getUserMutableLiveData().observe(this, firebaseUser -> {
            if (firebaseUser != null) {

                //Go to NewsFeedActivity
                Intent myIntent = new Intent(this, NewsFeedActivity.class);
                startActivity(myIntent);
            }
        });

        //If the user has opened this application before these onboarding screens will be skipped
        if (isOpenAlready()) {

            //Create intent to navigate to RegisterLoginActivity
            Intent intent = new Intent(OnboardingScreensActivity.this, RegisterLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {

            //Set a SharedPreference boolean to indicate user has now seen these onboarding screens and will not need to again
            SharedPreferences.Editor editor = getSharedPreferences("slide", MODE_PRIVATE).edit();
            editor.putBoolean("slide", true);
            editor.commit();
        }
    }

    //Checks if user has seen the onboarding screens before
    private boolean isOpenAlready() {
        SharedPreferences sharedPreferences = getSharedPreferences("slide", MODE_PRIVATE);
        boolean result = sharedPreferences.getBoolean("slide", false);
        return result;
    }
}