package com.example.travelhut.views.main.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.travelhut.R;
import com.example.travelhut.utils.BottomNavigationViewHelper;
import com.example.travelhut.views.authentication.RegisterLoginActivity;
import com.example.travelhut.views.main.profile.toolbar.EditProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

public class AccountSettingsActivity extends AppCompatActivity {

    private static final String TAG = "AccountSettingsActivity";
    private static final int ACTIVITY_NUM = 4;
    private Context mContext;
    private SectionsStateAdapter stateAdapter;
    private ViewPager2 mViewPager;
    private RelativeLayout mRelativeLayout;


    TextView logout, settings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        Log.d(TAG, "onCreate: started.");
        mContext = AccountSettingsActivity.this;
//        mViewPager = findViewById(R.id.view_pager_2);
//        mViewPager.setUserInputEnabled(false);


        logout = findViewById(R.id.acc_settings_logout_btn);
        settings = findViewById(R.id.acc_settings_settings_btn);

        mRelativeLayout = findViewById(R.id.acc_settings_rel_layout);
        //mViewPager.setAdapter(new );
        setupBottomNavigationView();
//        setSettingsList();
//        setupFragments();



        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AccountSettingsActivity.this, EditProfileActivity.class));
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(AccountSettingsActivity.this, RegisterLoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

//    private void setupFragments(){
//        stateAdapter = new SectionsStateAdapter(getSupportFragmentManager(), getLifecycle(), mContext);
//        stateAdapter.addFragment(new EditProfileFragment(), getString(R.string.edit_profile));
//        stateAdapter.addFragment(new SignOutFragment(), getString(R.string.sign_out));
//    }
//
//    private void setViewPager(int fragmentNumber){
//        mRelativeLayout.setVisibility(View.GONE);
//        mViewPager.setAdapter(stateAdapter);
//        mViewPager.setCurrentItem(fragmentNumber);
//    }

//    private void setSettingsList(){
//        Log.d(TAG, "setSettingsList: started.");
//        ListView listView = (ListView) findViewById(R.id.account_settings_list_view);
//
//        ArrayList<String> options = new ArrayList<>();
//        options.add(getString(R.string.edit_profile));
//        options.add(getString(R.string.sign_out));
//
//        ArrayAdapter arrayAdapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, options);
//        listView.setAdapter(arrayAdapter);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                setViewPager(position);
//            }
//        });
//    }


    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView.");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
//            fragment.onActivityResult(requestCode, resultCode, data);
//        }
//    }
}
