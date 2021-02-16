package com.example.travelhut.model;

import android.app.Application;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.travelhut.views.authentication.RegisterLoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class AuthAppRepository extends LiveData<DataSnapshot> {

    private Application application;
    private MutableLiveData<FirebaseUser> userMutableLiveData;
    private MutableLiveData<Boolean> loggedOutMutableLiveData;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    public AuthAppRepository(Application application) {
        this.application = application;

        firebaseAuth = FirebaseAuth.getInstance();
        userMutableLiveData = new MutableLiveData<>();
        loggedOutMutableLiveData = new MutableLiveData<>();

        if(firebaseAuth.getCurrentUser() != null){
            userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
            loggedOutMutableLiveData.postValue(false);
        }
    }

    public AuthAppRepository() {
        userMutableLiveData = new MutableLiveData<>();
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null) {
            userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
        }
    }

    //Registers a user with firebase authentication
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void register(String email, String username, String password){
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(application.getMainExecutor(), task -> {

            //if registration is successful
            if(task.isSuccessful()){

                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                String userId = firebaseUser.getUid();

                databaseReference = FirebaseDatabase.getInstance().getReference().child(StringsRepository.USERS_CAP).child(userId);
                HashMap<String, Object> hashMap = getUserHashMap(email, username, userId);

                //attempts to create user object in firebase realtime database with generated hashMap
                databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //If successful -> post current user to userMutableLiveData object for ViewModel to observe and go to login screen
                        if(task.isSuccessful()){
                            userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
                            Toast.makeText(application, StringsRepository.USER_CREATED, Toast.LENGTH_SHORT).show();
                            RegisterLoginActivity.viewPager.setCurrentItem(0);
                        }
                    }
                });


            }
            //else -> registration failed and error message shown if exists
            else if(task.getException().getMessage() != null){
                Toast.makeText(application, StringsRepository.REG_FAILED + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(application, StringsRepository.REG_FAILED , Toast.LENGTH_SHORT).show();
            }
        });
    }


    //this method created a hashMap object of user details
    private HashMap<String, Object> getUserHashMap(String email, String username, String userId) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(StringsRepository.ID, userId);
        hashMap.put(StringsRepository.USERNAME, username);
        hashMap.put("displayname", "");
        hashMap.put("bio", "");
        hashMap.put("url", "");
        hashMap.put(StringsRepository.EMAIL, email);
        hashMap.put(StringsRepository.IMAGE_URL, StringsRepository.PLACEHOLDER_PROFILE_IMAGE_URL);
        return hashMap;
    }


    //attempts to login user
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void login(String email, String password){
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(application.getMainExecutor(), task -> {
                    if(task.isSuccessful()){
                        userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
                    }else if(task.getException().getMessage() != null){
                        Toast.makeText(application, StringsRepository.LOGIN_FAILED + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(application, StringsRepository.LOGIN_FAILED , Toast.LENGTH_SHORT).show();
                    }
                });

    }

    //return LiveData object of a FirebaseUser which is the current user in this class
    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    public MutableLiveData<Boolean> getLoggedOutMutableLiveData() {
        return loggedOutMutableLiveData;
    }

    //this method logs out a signed in user
    public void logout(){
        firebaseAuth.signOut();
        loggedOutMutableLiveData.postValue(true);
    }




}
