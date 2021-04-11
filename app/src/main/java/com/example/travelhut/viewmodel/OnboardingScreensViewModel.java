package com.example.travelhut.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.travelhut.model.authentication.AuthRepository;
import com.google.firebase.auth.FirebaseUser;

public class OnboardingScreensViewModel extends AndroidViewModel {

    //Instance Variables
    private AuthRepository authRepository;
    private MutableLiveData<FirebaseUser> userMutableLiveData;

    //Constructor
    public OnboardingScreensViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
        userMutableLiveData = authRepository.getUserMutableLiveData();
    }

    //This method returns a LiveData object containing the current FirebaseUser
    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }

}
