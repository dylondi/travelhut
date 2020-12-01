package com.example.travelhut.views.main.newsfeed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.travelhut.R;
import com.example.travelhut.utils.BottomNavigationViewHelper;
import com.example.travelhut.viewmodel.MainActivityViewModel;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class NewsFeedActivity extends AppCompatActivity {

    private final static String TAG = "NewsFeedActivity";
    private Context mContext = NewsFeedActivity.this;
    private static final int ACTIVITY_NUM = 2;

    private TextView loggedInUserTextView;
    private Button logoutButton;

    private MainActivityViewModel mainActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupBottomNavigationView();
//
//        loggedInUserTextView = findViewById(R.id.user);
//        logoutButton = findViewById(R.id.logoutBtn);
//        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
//        mainActivityViewModel.getUserMutableLiveData().observe(this, firebaseUser -> {
//            if(firebaseUser != null){
//                loggedInUserTextView.setText("Logged in user: " + firebaseUser.getEmail());
//            }
//        });
//
//        logoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mainActivityViewModel.logout();
//            }
//        });
//
//        mainActivityViewModel.getLoggedOutMutableLiveData().observe(this, new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean loggedOut) {
//                if(loggedOut){
//                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
//                }
//            }
//        });

    }

    private void setupBottomNavigationView(){
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
        // Simply Do noting!
    }

}