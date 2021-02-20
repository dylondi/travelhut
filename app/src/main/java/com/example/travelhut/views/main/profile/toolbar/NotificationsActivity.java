package com.example.travelhut.views.main.profile.toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.service.autofill.Dataset;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.travelhut.R;
import com.example.travelhut.utils.BottomNavigationViewHelper;
import com.example.travelhut.viewmodel.main.profile.CreatePostActivityViewModel;
import com.example.travelhut.viewmodel.main.profile.toolbar.NotificationsActivityViewModel;
import com.example.travelhut.views.main.newsfeed.NewsFeedFragment;
import com.example.travelhut.views.main.profile.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private static final String TAG = "NotificationsActivity";
    private static final int ACTIVITY_NUM = 4;

    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    private ImageView backArrow;
    NotificationsActivityViewModel notificationsActivityViewModel;
    NotificationsFragment notificationsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        setupBottomNavigationView();
        notificationsFragment = new NotificationsFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.notifications_frame_layout, (Fragment) notificationsFragment).commit();
//        notificationsActivityViewModel = ViewModelProviders.of(this).get(NotificationsActivityViewModel.class);
//
//        recyclerView = findViewById(R.id.notifications_recycler_view);
//        recyclerView.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(linearLayoutManager);
//        notificationList = new ArrayList<>();
//        notificationAdapter = new NotificationAdapter(this, notificationList);
//        recyclerView.setAdapter(notificationAdapter);
//        backArrow = findViewById(R.id.notifications_back_arrow);
//        readNotifications();
//
//        backArrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
    }

//    private void readNotifications(){
//
//        LiveData<DataSnapshot> liveData = notificationsActivityViewModel.getFollowingSnapshot();
//
//        liveData.observe(this, dataSnapshot -> {
//            notificationList.clear();
//            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                Notification notification = snapshot.getValue(Notification.class);
//                notificationList.add(notification);
//            }
//            Collections.reverse(notificationList);
//            notificationAdapter.notifyDataSetChanged();
//        });
//
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
}