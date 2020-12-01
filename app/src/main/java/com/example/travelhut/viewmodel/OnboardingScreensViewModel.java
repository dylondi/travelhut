package com.example.travelhut.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.travelhut.model.AppRepository;
import com.google.firebase.auth.FirebaseUser;

public class OnboardingScreensViewModel extends AndroidViewModel {

    private AppRepository appRepository;
    private MutableLiveData<FirebaseUser> userMutableLiveData;

    public OnboardingScreensViewModel(@NonNull Application application) {
        super(application);

        appRepository = new AppRepository(application);
        userMutableLiveData = appRepository.getUserMutableLiveData();
    }

    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }

}
