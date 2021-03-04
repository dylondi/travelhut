package com.example.travelhut.viewmodel.main.profile.toolbar;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.travelhut.model.NotificationsAppRepository;
import com.google.firebase.database.DataSnapshot;

public class NotificationsActivityViewModel extends AndroidViewModel {

    Application application;
    private NotificationsAppRepository notificationsAppRepository;
    public NotificationsActivityViewModel(@NonNull Application application) {
        super(application);
        notificationsAppRepository = new NotificationsAppRepository();
        this.application = application;
    }

    @NonNull
    public LiveData<DataSnapshot> getFollowingSnapshot() {

        return notificationsAppRepository;
    }
}
