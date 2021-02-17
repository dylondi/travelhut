package com.example.travelhut.viewmodel.main.profile.toolbar;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.travelhut.model.NotificationsAdapterAppRepository;
import com.google.firebase.database.DataSnapshot;

public class NotificationAdapterViewModel extends ViewModel {

    NotificationsAdapterAppRepository adapterAppRepository;
    String publisherid;

    public NotificationAdapterViewModel(String publisherid) {
        this.publisherid = publisherid;
        adapterAppRepository = new NotificationsAdapterAppRepository(publisherid);
    }

    @NonNull
    public LiveData<DataSnapshot> getFollowingSnapshot() {

        return adapterAppRepository;
    }
}
