package com.example.travelhut.viewmodel.authentication;

import android.app.Application;
import android.os.Build;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.travelhut.model.AuthAppRepository;
import com.google.firebase.auth.FirebaseUser;

public class RegisterViewModel extends AndroidViewModel {

    private AuthAppRepository authAppRepository;
    private MutableLiveData<FirebaseUser> userMutableLiveData;
    private RegisterLoginFormValidator registerLoginFormValidator;

    public RegisterViewModel(@NonNull Application application) {
        super(application);

        authAppRepository = new AuthAppRepository(application);
        userMutableLiveData = authAppRepository.getUserMutableLiveData();
        registerLoginFormValidator = new RegisterLoginFormValidator();

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void register(String email, String username, String password){
        authAppRepository.register(email, username, password);
    }

    public boolean validateEmail(EditText email){
        return registerLoginFormValidator.validateEmail(email);
    }

    public boolean validateUsername(EditText username){
        return registerLoginFormValidator.validateUsername(username);
    }
    public boolean validatePasswordOne(EditText password){
        return registerLoginFormValidator.validatePasswordOne(password);
    }

    public boolean validatePasswordTwo(EditText passwordOne, EditText passwordTwo){
        return registerLoginFormValidator.validatePasswordTwo(passwordOne, passwordTwo);
    }

    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }


}
