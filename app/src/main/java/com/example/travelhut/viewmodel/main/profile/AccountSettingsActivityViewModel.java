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

    //This method notifies authRepository to sign out the current FirebaseUser
    public void logout() {
        authRepository.logout();
    }

    //This method returns a MutableLiveData object containing a boolean which indicates if the current user has logged out
    public MutableLiveData<Boolean> getLoggedOutMutableLiveData() {
        return loggedOutLiveData;
    }
}
