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

public class RegisterViewModel extends AndroidViewModel {

    //Instance Variables
    private AuthRepository authRepository;
    private MutableLiveData<FirebaseUser> userMutableLiveData;
    private RegisterLoginFormValidator registerLoginFormValidator;

    //Constructor
    public RegisterViewModel(@NonNull Application application) {
        super(application);

        //Initialize objects
        authRepository = new AuthRepository(application);
        userMutableLiveData = authRepository.getUserMutableLiveData();
        registerLoginFormValidator = new RegisterLoginFormValidator();
    }

    //This method calls the register() method in the Model class AuthRepository.java
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void register(String email, String username, String password) {
        authRepository.register(email, username, password);
    }

    //This method calls and returns the boolean result from validateEmail method in RegisterLoginFormValidator.java
    public boolean validateEmail(EditText email) {
        return registerLoginFormValidator.validateEmail(email);
    }

    //This method calls and returns the boolean result from validateUsername method in RegisterLoginFormValidator.java
    public boolean validateUsername(EditText username) {
        return registerLoginFormValidator.validateUsername(username);
    }

    //This method calls and returns the boolean result from validatePassword method in RegisterLoginFormValidator.java
    public boolean validatePasswordOne(EditText password) {
        return registerLoginFormValidator.validatePasswordOne(password);
    }

    //This method calls and returns the boolean result from validatePasswordTwo method in RegisterLoginFormValidator.java
    public boolean validatePasswordTwo(EditText passwordOne, EditText passwordTwo) {
        return registerLoginFormValidator.validatePasswordTwo(passwordOne, passwordTwo);
    }

    //This method returns the boolean value of registeredMutableLiveData object from the Model class AuthRepository.java
    public MutableLiveData<Boolean> getRegisteredMutableLiveData() {
        return authRepository.getRegisteredMutableLiveData();
    }

    //This method returns the registration failed message from the Model class AuthRepository.java
    public MutableLiveData<String> getRegFailedMessageMutableLiveData() {
        return authRepository.getRegFailedMessageMutableLiveData();
    }


}
