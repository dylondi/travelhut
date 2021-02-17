package com.example.travelhut.viewmodel.main.profile.toolbar;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.travelhut.model.NotifcationsAppRepository;
import com.google.firebase.database.DataSnapshot;

public class NotificationsActivityViewModel extends AndroidViewModel {

    Application application;
    private NotifcationsAppRepository notifcationsAppRepository;
    public NotificationsActivityViewModel(@NonNull Application application) {
        super(application);
        notifcationsAppRepository = new NotifcationsAppRepository();
        this.application = application;
    }

    @NonNull
    public LiveData<DataSnapshot> getFollowingSnapshot() {

        return notifcationsAppRepository;
    }
}
