package com.example.travelhut.viewmodel.authentication;

import android.app.Application;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.travelhut.model.AppRepository;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends AndroidViewModel {
    private AppRepository appRepository;
    private MutableLiveData<FirebaseUser> userMutableLiveData;
    private RegisterLoginFormValidator registerLoginFormValidator;


    public LoginViewModel(@NonNull Application application) {
        super(application);

        appRepository = new AppRepository(application);
        userMutableLiveData = appRepository.getUserMutableLiveData();
        registerLoginFormValidator = new RegisterLoginFormValidator();

    }

    public void login(String email, String password){
        appRepository.login(email, password);
    }

    public boolean validateEmail(EditText email){
        return registerLoginFormValidator.validateEmail(email);
    }
    public boolean validatePasswordOne(EditText password){
        return registerLoginFormValidator.validatePasswordOne(password);
    }

    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }

}
