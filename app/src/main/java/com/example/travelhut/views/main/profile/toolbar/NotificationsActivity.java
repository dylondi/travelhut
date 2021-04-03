package com.example.travelhut.views.main.profile.toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.travelhut.R;
import com.example.travelhut.views.utils.BottomNavigationViewHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class NotificationsActivity extends AppCompatActivity {


    //Instance Variables
    private static final String TAG = "NotificationsActivity";
    private static final int ACTIVITY_NUM = 4;
    private NotificationsFragment notificationsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        setupBottomNavigationView();
        notificationsFragment = new NotificationsFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.notifications_frame_layout, (Fragment) notificationsFragment).commit();
    }

    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView.");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}