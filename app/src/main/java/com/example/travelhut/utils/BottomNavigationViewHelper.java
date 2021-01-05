package com.example.travelhut.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.travelhut.R;
import com.example.travelhut.views.main.newsfeed.NewsFeedActivity;
import com.example.travelhut.views.main.planner.PlannerActivity;
import com.example.travelhut.views.main.profile.ProfileActivity;
import com.example.travelhut.views.main.map_search.MapSearchActivity;
import com.example.travelhut.views.main.trips.TripsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class BottomNavigationViewHelper {

    private static final String TAG = "BottomNavigationViewHel";

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
        Log.d(TAG, "setupBottomNavigationView: Setting up BottomNavigationView");
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }

    public static void enableNavigation(final Context context, BottomNavigationViewEx view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.ic_news_feed:
                        //Intent intent1 = new Intent(context, NewsFeedActivity.class);
                        context.startActivity(new Intent(context, NewsFeedActivity.class));
                        break;

                    case R.id.ic_map_search:
                        context.startActivity(new Intent(context, MapSearchActivity.class));
                        break;

                    case R.id.ic_trips:
                        context.startActivity(new Intent(context, TripsActivity.class));
                        break;

                    case R.id.ic_planner:
                        context.startActivity(new Intent(context, PlannerActivity.class));
                        break;

                    case R.id.ic_profile:
                        SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                        editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        editor.apply();
                        context.startActivity(new Intent(context, ProfileActivity.class));
                        break;

                }
                return false;
            }
        });
    }
}
