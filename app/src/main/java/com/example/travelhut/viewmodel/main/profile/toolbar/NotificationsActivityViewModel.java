package com.example.travelhut.viewmodel.main.profile.toolbar;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.travelhut.model.main.profile.NotificationsRepository;
import com.google.firebase.database.DataSnapshot;

public class NotificationsActivityViewModel extends AndroidViewModel {

    //Instance Variable
    private NotificationsRepository notificationsRepository;

    //Constructor
    public NotificationsActivityViewModel(@NonNull Application application) {
        super(application);
        notificationsRepository = new NotificationsRepository();
    }

    //This method returns a MutableLiveData object containing a list of notifications
    @NonNull
    public LiveData<DataSnapshot> getFollowingSnapshot() {
        return notificationsRepository;
    }
}
