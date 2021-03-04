package com.example.travelhut.viewmodel.main.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.travelhut.model.AuthAppRepository;

public class AccountSettingsActivityViewModel extends AndroidViewModel {

    private AuthAppRepository authAppRepository;
    private MutableLiveData<Boolean> loggedOutLiveData;

    public AccountSettingsActivityViewModel(@NonNull Application application) {
        super(application);
        authAppRepository = new AuthAppRepository();
        loggedOutLiveData = authAppRepository.getLoggedOutMutableLiveData();

    }


    public void logout(){
        authAppRepository.logout();
    }

    public MutableLiveData<Boolean> getLoggedOutMutableLiveData(){
        return loggedOutLiveData;
    }
}
