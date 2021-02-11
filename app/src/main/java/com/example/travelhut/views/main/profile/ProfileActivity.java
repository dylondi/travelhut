package com.example.travelhut.views.main.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.travelhut.R;
import com.example.travelhut.model.UniversalImageLoader;
import com.example.travelhut.utils.BottomNavigationViewHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private Context mContext = ProfileActivity.this;
    private static final int ACTIVITY_NUM = 4;
    private static final int NUM_OF_GRID_COLUMNS = 3;
    private TextView username;
    private FirebaseAuth firebaseAuth;
    private ProgressBar mProgressBar;
    private ImageView profileImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: started.");
        username = findViewById(R.id.display_name);

        String s = username.getText().toString();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        username.setText(user.getDisplayName());

        setupBottomNavigationView();
        setupToolbar();
        setupActivityWidgets();


        tempGridSetup();
        setProfileImage();




        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("username").getValue().toString();
                username.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
     }

     private void tempGridSetup(){
        ArrayList<String> imgURLs = new ArrayList<>();
        imgURLs.add("https://picsum.photos/id/237/200/300");
        imgURLs.add("https://picsum.photos/seed/picsum/200/300");
        imgURLs.add("https://picsum.photos/200/300?grayscale");
        imgURLs.add("https://picsum.photos/200/300/?blur");
        imgURLs.add("https://picsum.photos/id/870/200/300?grayscale&blur=2");
         imgURLs.add("https://picsum.photos/seed/picsum/200/300");
         imgURLs.add("https://picsum.photos/200/300?grayscale");
         imgURLs.add("https://picsum.photos/200/300/?blur");
         imgURLs.add("https://picsum.photos/id/870/200/300?grayscale&blur=2");
        imgURLs.add("https://i.picsum.photos/id/1003/1181/1772.jpg?hmac=oN9fHMXiqe9Zq2RM6XT-RVZkojgPnECWwyEF1RvvTZk");
        imgURLs.add("https://i.picsum.photos/id/1010/5184/3456.jpg?hmac=7SE0MNAloXpJXDxio2nvoshUx9roGIJ_5pZej6qdxXs");
        imgURLs.add("https://i.picsum.photos/id/1015/6000/4000.jpg?hmac=aHjb0fRa1t14DTIEBcoC12c5rAXOSwnVlaA5ujxPQ0I");
        imgURLs.add("https://i.picsum.photos/id/102/4320/3240.jpg?hmac=ico2KysoswVG8E8r550V_afIWN963F6ygTVrqHeHeRc");
        imgURLs.add("https://i.picsum.photos/id/102/4320/3240.jpg?hmac=ico2KysoswVG8E8r550V_afIWN963F6ygTVrqHeHeRc");
        imgURLs.add("https://i.picsum.photos/id/102/4320/3240.jpg?hmac=ico2KysoswVG8E8r550V_afIWN963F6ygTVrqHeHeRc");
        imgURLs.add("https://i.picsum.photos/id/102/4320/3240.jpg?hmac=ico2KysoswVG8E8r550V_afIWN963F6ygTVrqHeHeRc");

        setupImageGridView(imgURLs);
     }

     private void setProfileImage(){
        String imgURL = "https://www.sportsfile.com/winshare/w540/Library/SF722/523374.jpg";
         UniversalImageLoader.setImage(imgURL, profileImage, mProgressBar, "");
     }

    private void setupActivityWidgets() {
        mProgressBar = (ProgressBar) findViewById(R.id.profile_progress_bar);
        mProgressBar.setVisibility(View.GONE);
        profileImage = (ImageView) findViewById(R.id.profile_image);
    }

     private void setupToolbar(){
         Toolbar toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
         setSupportActionBar(toolbar);

         ImageView overFlowMenu = findViewById(R.id.overflow_menu);
         overFlowMenu.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Log.i(TAG, "onClick called starting Intent for AccountSettingsActivity.");
                // Intent intent = new Intent(mContext, AccountSettingsActivity.class);
                 startActivity(new Intent(mContext, AccountSettingsActivity.class));
             }
         });
     }



     private void setupImageGridView(ArrayList<String> imgURLs){
         GridView gridView = findViewById(R.id.grid_view_profile);
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imagewidth = gridWidth/NUM_OF_GRID_COLUMNS;
        gridView.setColumnWidth(imagewidth);
         GridImageAdapter adapter = new GridImageAdapter(mContext, R.layout.layout_grid_imageview, "", imgURLs);
         gridView.setAdapter(adapter);
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
