package com.example.travelhut.views.main.profile.toolbar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.travelhut.R;
import com.example.travelhut.viewmodel.main.profile.toolbar.NotificationAdapterViewModel;
import com.example.travelhut.viewmodel.main.profile.toolbar.NotificationsActivityViewModel;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    private ImageView backArrow;
    NotificationsActivityViewModel notificationsActivityViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_notifications, container, false);

        notificationsActivityViewModel = ViewModelProviders.of(this).get(NotificationsActivityViewModel.class);

        recyclerView = view.findViewById(R.id.notifications_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(getContext(), notificationList);
        recyclerView.setAdapter(notificationAdapter);
        backArrow = view.findViewById(R.id.notifications_back_arrow);
        readNotifications();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });


        return view;
    }


    private void readNotifications(){

        LiveData<DataSnapshot> liveData = notificationsActivityViewModel.getFollowingSnapshot();

        liveData.observe(getViewLifecycleOwner(), dataSnapshot -> {
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