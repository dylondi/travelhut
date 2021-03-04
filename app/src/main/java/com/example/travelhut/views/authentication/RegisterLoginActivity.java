package com.example.travelhut.views.authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.travelhut.R;
import com.example.travelhut.views.authentication.utils.RegisterLoginAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class RegisterLoginActivity extends AppCompatActivity {

    TabLayout tabLayout;
    public static ViewPager viewPager;
    FloatingActionButton google;
    float v = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //assigning views
        assignViews();

        //add login and register tabs to tabLayout
        configTabLayout();

        //declaring and initializing the adapter which will adapt the login and register fragment to this viewpager
        final RegisterLoginAdapter adapter = new RegisterLoginAdapter(getSupportFragmentManager(), this, tabLayout.getTabCount());

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        animateViews();

        //onTabSelectedListener to switch between login and register tabs when either one is clicked by user
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
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

    private void animateViews() {
        google.setTranslationY(300);
        tabLayout.setTranslationY(300);
        google.setAlpha(v);
        tabLayout.setAlpha(v);
        google.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400);
        tabLayout.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400);
    }

    private void configTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText(AuthViewStrings.LOGIN));
        tabLayout.addTab(tabLayout.newTab().setText(AuthViewStrings.REGISTER));
        tabLayout.setTabGravity(tabLayout.GRAVITY_FILL);
    }

    private void assignViews() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        google = findViewById(R.id.fab_google);
    }

    @Override
    public void onBackPressed() {
        // Simply Do noting!
    }


}