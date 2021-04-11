package com.example.travelhut.views.authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.travelhut.R;
import com.example.travelhut.views.authentication.utils.AuthViewStrings;
import com.example.travelhut.views.authentication.utils.RegisterLoginAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class RegisterLoginActivity extends AppCompatActivity {

    //Instance Variables
    private TabLayout tabLayout;
    public static ViewPager viewPager;
    private FloatingActionButton google;
    float v = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_login);

        //Initialize views
        initViews();

        //Add login and register tabs to tabLayout
        configTabLayout();


        //Create and initialize RegisterLoginAdapter
        final RegisterLoginAdapter adapter = new RegisterLoginAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        //Set adapter of ViewPager
        viewPager.setAdapter(adapter);
        //Add an OnPageChangeListener to handle swipe gesture
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        animateViews();

        //OnTabSelectedListener to switch between login and register tabs when either one is clicked by user
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    //This method animates this activity's views
    private void animateViews() {
        google.setTranslationY(300);
        tabLayout.setTranslationY(300);
        google.setAlpha(v);
        tabLayout.setAlpha(v);
        google.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400);
        tabLayout.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400);
    }

    //This method adds register and login tabs to the TabLayout
    private void configTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText(AuthViewStrings.LOGIN));
        tabLayout.addTab(tabLayout.newTab().setText(AuthViewStrings.REGISTER));
        tabLayout.setTabGravity(tabLayout.GRAVITY_FILL);
    }

    //This method initializes the view objects
    private void initViews() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        google = findViewById(R.id.fab_google);
    }

    @Override
    //Do nothing when back button is pressed
    public void onBackPressed() {
    }


}