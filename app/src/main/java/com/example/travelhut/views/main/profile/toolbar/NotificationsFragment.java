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
import com.example.travelhut.model.objects.Notification;
import com.example.travelhut.viewmodel.main.profile.toolbar.NotificationsActivityViewModel;
import com.example.travelhut.views.main.profile.toolbar.utils.NotificationsAdapter;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationsFragment extends Fragment {

    //Instance Variables
    private RecyclerView recyclerView;
    private NotificationsAdapter notificationsAdapter;
    private List<Notification> notificationList;
    private ImageView backArrow;
    private NotificationsActivityViewModel notificationsActivityViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        notificationsActivityViewModel = ViewModelProviders.of(this).get(NotificationsActivityViewModel.class);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        //Initialize objects
        recyclerView = view.findViewById(R.id.notifications_recycler_view);
        backArrow = view.findViewById(R.id.notifications_back_arrow);
        notificationList = new ArrayList<>();
        notificationsAdapter = new NotificationsAdapter(getContext(), notificationList);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(notificationsAdapter);
        readNotifications();

        backArrow.setOnClickListener(v -> getActivity().finish());

        return view;
    }


    //This method updates the list of Notifications from the ViewModel
    private void readNotifications() {

        LiveData<DataSnapshot> liveData = notificationsActivityViewModel.getFollowingSnapshot();

        liveData.observe(getViewLifecycleOwner(), dataSnapshot -> {
            notificationList.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Notification notification = snapshot.getValue(Notification.class);
                notificationList.add(notification);
            }
            Collections.reverse(notificationList);
            notificationsAdapter.notifyDataSetChanged();
        });

    }
}