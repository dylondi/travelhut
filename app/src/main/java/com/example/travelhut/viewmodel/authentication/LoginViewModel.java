package com.example.travelhut.viewmodel.authentication;

import android.app.Application;
import android.os.Build;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.travelhut.model.authentication.AuthRepository;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends AndroidViewModel {

    //Instance Variables
    private AuthRepository authRepository;
    private MutableLiveData<FirebaseUser> userMutableLiveData;
    private RegisterLoginFormValidator registerLoginFormValidator;

    //Constructor
    public LoginViewModel(@NonNull Application application) {
        super(application);

        //Initialize objects
        authRepository = new AuthRepository(application);
        userMutableLiveData = authRepository.getUserMutableLiveData();
        registerLoginFormValidator = new RegisterLoginFormValidator();
    }

    //This method calls the login method in AuthRepository
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void login(String email, String password) {
        authRepository.login(email, password);
    }

    //This method calls and returns the boolean result from validateEmail in RegisterLoginFormValidator.java
    public boolean validateEmail(EditText email) {
        return registerLoginFormValidator.validateEmail(email);
    }

    //This method calls and returns the boolean result from validatePassword in RegisterLoginFormValidator.java
    public boolean validatePasswordOne(EditText password) {
        return registerLoginFormValidator.validatePasswordOne(password);
    }

    //This method returns the FirebaseUser object userMutableLiveData from the Model class AuthRepository.java
    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    //This method returns the value of loggedOutMutableLiveData from the Model class AuthRepository.java
    public MutableLiveData<Boolean> getLoggedOutMutableLiveData() {
        return authRepository.getLoggedOutMutableLiveData();
    }

    //This method returns the login failed message from the Model class AuthRepository.java
    public MutableLiveData<String> getLoginFailedMessageMutableLiveData() {
        return authRepository.getLoginFailedMessageMutableLiveData();
    }
}
