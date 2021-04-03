package com.example.travelhut.model.authentication;

import android.app.Application;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.travelhut.model.utils.StringsRepository;
import com.example.travelhut.views.authentication.RegisterLoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class AuthRepository extends LiveData<DataSnapshot> {


    //Instance Variables
    private Application application;
    private MutableLiveData<FirebaseUser> userMutableLiveData;
    private MutableLiveData<Boolean> loggedOutMutableLiveData;
    private MutableLiveData<Boolean> registeredMutableLiveData;
    private MutableLiveData<String> regFailedMessageMutableLiveData;
    private MutableLiveData<String> loginFailedMessageMutableLiveData;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    //Constructor containing application object
    public AuthRepository(Application application) {
        this.application = application;

        //Initialize FirebaseAuth & MutableLiveData objects
        firebaseAuth = FirebaseAuth.getInstance();
        initMutableLiveData();

        //Set default value for boolean MutableLiveData objects
        loggedOutMutableLiveData.setValue(true);
        registeredMutableLiveData.setValue(false);

        //Check if user is logged in
        if (firebaseAuth.getCurrentUser() != null) {

            //Post current user's FirebaseUser object to userMutableLiveData
            userMutableLiveData.postValue(firebaseAuth.getCurrentUser());

            //Post false to loggedOutMutableLiveData
            loggedOutMutableLiveData.postValue(false);
        }
    }

    //Constructor
    public AuthRepository() {

        //Initialize objects
        firebaseAuth = FirebaseAuth.getInstance();
        userMutableLiveData = new MutableLiveData<>();
        loggedOutMutableLiveData = new MutableLiveData<>();

        //Check if user is logged in
        if (firebaseAuth.getCurrentUser() != null) {

            //Post current user's FirebaseUser object to userMutableLiveData
            userMutableLiveData.setValue(firebaseAuth.getCurrentUser());

            //Post false to loggedOutMutableLiveData
            loggedOutMutableLiveData.setValue(false);
        }
    }

    //Registers a user with firebase authentication
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void register(String email, String username, String password) {

        //Create a new thread which calls the createUserWithEmailAndPassword() method on the FirebaseAuth object using the email and password inputs
        new Thread(() -> firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(application.getMainExecutor(), task -> {

            //If registration is successful
            if (task.isSuccessful()) {

                //Get current user UUID
                String userId = firebaseAuth.getCurrentUser().getUid();

                //Get database reference to current user
                databaseReference = FirebaseDatabase.getInstance().getReference().child(StringsRepository.USERS_CAP).child(userId);

                //Create HashMap with all of the current user's info
                HashMap<String, Object> hashMap = getUserHashMap(email, username, userId);

                //Attempts to set HashMap to databaseReference
                databaseReference.setValue(hashMap).addOnCompleteListener(task1 -> {

                    //If successful
                    if (task1.isSuccessful()) {

                        //Post current firebase user to userMutableLiveData object
                        userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
                        //Post true to registeredMutableLiveData object to notify registration was successful
                        registeredMutableLiveData.postValue(true);
                        return;
                    }
                });


            }
            //Else -> registration failed and error message shown if exists
            else if (task.getException().getMessage() != null) {
                    regFailedMessageMutableLiveData.postValue(StringsRepository.REG_FAILED + task.getException().getMessage());
                    return;
            } else {
                    regFailedMessageMutableLiveData.postValue(StringsRepository.REG_FAILED + task.getException().getMessage());
                    return;
            }
        })).start();
    }

    //Attempts to login user
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void login(String email, String password) {

        //Create new thread which call FirebaseAuth's signInWithEmailAndPassword() method
        new Thread(() -> firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(application.getMainExecutor(), task -> {

                    //If sign in is successful
                    if (task.isSuccessful()) {
                        //Post current user object to userMutableLiveData object
                        userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
                        return;
                    }
                    //Else if sign in failed, post exception message to loginFailedMessageMutableLiveData
                    else if (task.getException().getMessage() != null) {
                        loginFailedMessageMutableLiveData.postValue(StringsRepository.LOGIN_FAILED + task.getException().getMessage());
                        return;
                    }
                    //Else if sign in failed, post login failed message to loginFailedMessageMutableLiveData
                    else {
                        loginFailedMessageMutableLiveData.postValue(StringsRepository.LOGIN_FAILED);
                        return;
                    }
                })).start();

    }



    //This method creates a hashMap object of user details
    private HashMap<String, Object> getUserHashMap(String email, String username, String userId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(StringsRepository.ID, userId);
        hashMap.put(StringsRepository.USERNAME, username);
        hashMap.put(StringsRepository.DISPLAY_NAME, "");
        hashMap.put(StringsRepository.BIO, "");
        hashMap.put(StringsRepository.URL, "");
        hashMap.put(StringsRepository.EMAIL, email);
        hashMap.put(StringsRepository.IMAGE_URL, StringsRepository.PLACEHOLDER_PROFILE_IMAGE_URL);
        return hashMap;
    }

    //This method initializes all MutableLiveData objects
    private void initMutableLiveData() {
        userMutableLiveData = new MutableLiveData<>();
        loggedOutMutableLiveData = new MutableLiveData<>();
        registeredMutableLiveData = new MutableLiveData<>();
        regFailedMessageMutableLiveData = new MutableLiveData<>();
        loginFailedMessageMutableLiveData = new MutableLiveData<>();
    }

    //Return LiveData object of a FirebaseUser which is the current user in this class
    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    //Return MutableLiveData Boolean object indicating if a user is logged out or not
    public MutableLiveData<Boolean> getLoggedOutMutableLiveData() {
        return loggedOutMutableLiveData;
    }

    //Return MutableLiveData Boolean object indicating if user has registered successfully out or not
    public MutableLiveData<Boolean> getRegisteredMutableLiveData() {
        return registeredMutableLiveData;
    }

    //Return MutableLiveData String containing the error message of a failed registration
    public MutableLiveData<String> getRegFailedMessageMutableLiveData() {
        return regFailedMessageMutableLiveData;
    }

    //Return MutableLiveData String containing the error message of a failed login
    public MutableLiveData<String> getLoginFailedMessageMutableLiveData() {
        return loginFailedMessageMutableLiveData;
    }

    //This method logs out a signed in user
    public void logout() {
        firebaseAuth.signOut();
        loggedOutMutableLiveData.postValue(true);
    }


}
