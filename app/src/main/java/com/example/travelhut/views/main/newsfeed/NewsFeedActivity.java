package com.example.travelhut.views.main.newsfeed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;


import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.travelhut.R;
import com.example.travelhut.views.utils.BottomNavigationViewHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class NewsFeedActivity extends AppCompatActivity implements LifecycleOwner {


    //Instance Variables
    private final static String TAG = "NewsFeedActivity";
    private static final int ACTIVITY_NUM = 2;
    private NewsFeedFragment newsFeedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupBottomNavigationView();
        if (savedInstanceState == null) {
            newsFeedFragment = new NewsFeedFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, (Fragment) newsFeedFragment).commit();
        }
    }

    //Sets up bottom navigation view
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


    @Override
    public void onBackPressed() {

    }





}