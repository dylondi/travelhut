package com.example.travelhut.model;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.travelhut.views.authentication.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class  AppRepository {

    private Application application;
    private MutableLiveData<FirebaseUser> userMutableLiveData;
    private MutableLiveData<Boolean> loggedOutMutableLiveData;
    private FirebaseAuth firebaseAuth;

    public AppRepository(Application application) {
        this.application = application;

        firebaseAuth = FirebaseAuth.getInstance();
        userMutableLiveData = new MutableLiveData<>();
        loggedOutMutableLiveData = new MutableLiveData<>();

        if(firebaseAuth.getCurrentUser() != null){
            userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
            loggedOutMutableLiveData.postValue(false);
        }
    }

    public void register(String email, String password){
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(application.getMainExecutor(), task -> {
            if(task.isSuccessful()){
                userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
                Toast.makeText(application, "User Created", Toast.LENGTH_SHORT).show();
                LoginActivity.viewPager.setCurrentItem(0);
            }else if(task.getException().getMessage() != null){
                Toast.makeText(application, "Registration Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(application, "Registration Failed" , Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void login(String email, String passowrd){
        firebaseAuth.signInWithEmailAndPassword(email, passowrd)
                .addOnCompleteListener(application.getMainExecutor(), task -> {
                    if(task.isSuccessful()){
                        userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
                    }else if(task.getException().getMessage() != null){
                        Toast.makeText(application, "Login Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(application, "Login Failed" , Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    public MutableLiveData<Boolean> getLoggedOutMutableLiveData() {
        return loggedOutMutableLiveData;
    }

    public void logout(){
        firebaseAuth.signOut();
        loggedOutMutableLiveData.postValue(true);
    }
}
