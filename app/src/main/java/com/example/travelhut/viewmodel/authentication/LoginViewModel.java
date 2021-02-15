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

public class LoginViewModel extends AndroidViewModel {
    private AuthAppRepository authAppRepository;
    private MutableLiveData<FirebaseUser> userMutableLiveData;
    private RegisterLoginFormValidator registerLoginFormValidator;


    public LoginViewModel(@NonNull Application application) {
        super(application);

        authAppRepository = new AuthAppRepository(application);
        userMutableLiveData = authAppRepository.getUserMutableLiveData();
        registerLoginFormValidator = new RegisterLoginFormValidator();

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void login(String email, String password){
        authAppRepository.login(email, password);
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

    public void logout(){
        authAppRepository.logout();
    }

}
