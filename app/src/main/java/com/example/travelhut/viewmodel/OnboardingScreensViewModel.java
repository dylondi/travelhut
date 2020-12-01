package com.example.travelhut.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.travelhut.model.AuthAppRepository;
import com.google.firebase.auth.FirebaseUser;

public class OnboardingScreensViewModel extends AndroidViewModel {

    private AuthAppRepository authAppRepository;
    private MutableLiveData<FirebaseUser> userMutableLiveData;

    public OnboardingScreensViewModel(@NonNull Application application) {
        super(application);

        authAppRepository = new AuthAppRepository(application);
        userMutableLiveData = authAppRepository.getUserMutableLiveData();
    }

    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }

}
