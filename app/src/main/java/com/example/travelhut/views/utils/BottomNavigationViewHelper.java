package com.example.travelhut.views.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.travelhut.R;
import com.example.travelhut.model.utils.StringsRepository;
import com.example.travelhut.views.main.map_search.MapSearchActivity;
import com.example.travelhut.views.main.newsfeed.NewsFeedActivity;
import com.example.travelhut.views.main.planner.PlannerActivity;
import com.example.travelhut.views.main.profile.ProfileActivity;
import com.example.travelhut.views.main.trips.TripsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class BottomNavigationViewHelper {

    //Instance Variable
    private static final String TAG = "BottomNavigationViewHel";

    //This method configures the bottom navigation view
    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx) {
        Log.d(TAG, "setupBottomNavigationView: Setting up BottomNavigationView");
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }


    //This method assigns intents to each item in a menu by iterating with a switch statement
    public static void enableNavigation(final Context context, BottomNavigationViewEx view) {
        view.setOnNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.ic_news_feed:
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
                    SharedPreferences.Editor editor = context.getSharedPreferences(StringsRepository.PREFS, Context.MODE_PRIVATE).edit();
                    editor.putString(StringsRepository.PROFILE_ID, FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.apply();
                    context.startActivity(new Intent(context, ProfileActivity.class));
                    break;

            }
            return false;
        });
    }
}
