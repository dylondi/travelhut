package com.example.travelhut.views.main.trips;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.travelhut.R;
import com.example.travelhut.views.utils.BottomNavigationViewHelper;
import com.example.travelhut.model.objects.Trip;
import com.google.android.material.tabs.TabLayout;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;

public class TripsActivity extends AppCompatActivity {

    //Instance Variables
    private static final String TAG = "TripsActivity";
    private static final int ACTIVITY_NUM = 1;
    public List<Trip> tripList;
    private TripsSectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabs;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);
        Log.d(TAG, "onCreate: started.");

        //Initialize objects
        sectionsPagerAdapter = new TripsSectionsPagerAdapter(this, getSupportFragmentManager());
        tripList = new ArrayList<>();
        viewPager = findViewById(R.id.view_pager_trips);
        tabs = findViewById(R.id.tabs);

        //Config views
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setupWithViewPager(viewPager);

        setupBottomNavigationView();

    }

    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
