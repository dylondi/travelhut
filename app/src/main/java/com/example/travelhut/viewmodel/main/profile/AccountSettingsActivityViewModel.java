package com.example.travelhut.viewmodel.main.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.travelhut.model.authentication.AuthRepository;

public class AccountSettingsActivityViewModel extends AndroidViewModel {

    //Instance Variables
    private AuthRepository authRepository;
    private MutableLiveData<Boolean> loggedOutLiveData;

    //Constructor
    public AccountSettingsActivityViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository();
        loggedOutLiveData = authRepository.getLoggedOutMutableLiveData();

    }


    public void logout() {
        authRepository.logout();
    }

    public MutableLiveData<Boolean> getLoggedOutMutableLiveData() {
        return loggedOutLiveData;
    }
}
