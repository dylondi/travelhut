package com.example.travelhut.views.main.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.travelhut.R;
import com.example.travelhut.views.utils.BottomNavigationViewHelper;
import com.example.travelhut.viewmodel.main.profile.AccountSettingsActivityViewModel;
import com.example.travelhut.views.authentication.RegisterLoginActivity;
import com.example.travelhut.views.main.profile.toolbar.EditProfileActivity;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class AccountSettingsActivity extends AppCompatActivity {

    //Instance Variables
    private static final String TAG = "AccountSettingsActivity";
    private static final int ACTIVITY_NUM = 4;
    private AccountSettingsActivityViewModel accountSettingsActivityViewModel;
    private TextView logout, settings;
    private ImageView backArrow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        Log.d(TAG, "onCreate: started.");

        accountSettingsActivityViewModel =  ViewModelProviders.of(this).get(AccountSettingsActivityViewModel.class);

        //Initialize views
        backArrow = findViewById(R.id.back_arrow);
        logout = findViewById(R.id.acc_settings_logout_btn);
        settings = findViewById(R.id.acc_settings_settings_btn);

        setupBottomNavigationView();

        //Check loggedOutMutableLiveData object from ViewModel to check if user logged out
        accountSettingsActivityViewModel.getLoggedOutMutableLiveData().observe(this , bool -> {
            if(bool){

                //Start RegisterLoginActivity
                startActivity(new Intent(AccountSettingsActivity.this, RegisterLoginActivity.class));
            }
        });

        //Set OnClickListener for settings
        settings.setOnClickListener(v -> startActivity(new Intent(AccountSettingsActivity.this, EditProfileActivity.class)));

        //Set OnClickListener for logout
        logout.setOnClickListener(v -> accountSettingsActivityViewModel.logout());

        //Set OnClickListener for backArrow
        backArrow.setOnClickListener(v -> finish());
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
