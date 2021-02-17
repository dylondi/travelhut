package com.example.travelhut.views.main.profile.toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.service.autofill.Dataset;
import android.view.View;
import android.widget.ImageView;

import com.example.travelhut.R;
import com.example.travelhut.viewmodel.main.profile.CreatePostActivityViewModel;
import com.example.travelhut.viewmodel.main.profile.toolbar.NotificationsActivityViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    private ImageView backArrow;
    NotificationsActivityViewModel notificationsActivityViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        notificationsActivityViewModel = ViewModelProviders.of(this).get(NotificationsActivityViewModel.class);

        recyclerView = findViewById(R.id.notifications_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(this, notificationList);
        recyclerView.setAdapter(notificationAdapter);
        backArrow = findViewById(R.id.notifications_back_arrow);
        readNotifications();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void readNotifications(){

        LiveData<DataSnapshot> liveData = notificationsActivityViewModel.getFollowingSnapshot();

        liveData.observe(this, dataSnapshot -> {
            notificationList.clear();
            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                Notification notification = snapshot.getValue(Notification.class);
                notificationList.add(notification);
            }
            Collections.reverse(notificationList);
            notificationAdapter.notifyDataSetChanged();
        });

    }
}