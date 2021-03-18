package com.example.travelhut.model;

import android.app.Application;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
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


    private Handler mainHandler = new Handler(Looper.getMainLooper());

    //Variables
    private Application application;
    private MutableLiveData<FirebaseUser> userMutableLiveData;
    private MutableLiveData<Boolean> loggedOutMutableLiveData;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private volatile boolean threadFinished;

    //Constructor containing application context
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

    //Constructor
    public AuthAppRepository() {
        userMutableLiveData = new MutableLiveData<>();
        firebaseAuth = FirebaseAuth.getInstance();
        loggedOutMutableLiveData = new MutableLiveData<>();

        if(firebaseAuth.getCurrentUser() != null) {
            userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
            loggedOutMutableLiveData.postValue(false);
        }
    }

    //Registers a user with firebase authentication
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void register(String email, String username, String password){


        new Thread(new Runnable() {
            @Override
            public void run() {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(application.getMainExecutor(), task -> {

                    //If registration is successful
                    if(task.isSuccessful()){

                        //Get current user UUID
                        String userId = firebaseAuth.getCurrentUser().getUid();

                        //Get database reference to current user
                        databaseReference = FirebaseDatabase.getInstance().getReference().child(StringsRepository.USERS_CAP).child(userId);

                        //Create HashMap with all of the current user's info
                        HashMap<String, Object> hashMap = getUserHashMap(email, username, userId);

                        //attempts to create user object in firebase realtime database with generated hashMap
                        databaseReference.setValue(hashMap).addOnCompleteListener(task1 -> {

                            //If successful -> post current user to userMutableLiveData object for ViewModel to observe and go to login screen
                            if(task1.isSuccessful()){
                                userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(application, StringsRepository.USER_CREATED, Toast.LENGTH_SHORT).show();
                                        RegisterLoginActivity.viewPager.setCurrentItem(0);
                                        return;
                                    }
                                });

                            }
                        });


                    }
                    //else -> registration failed and error message shown if exists
                    else if(task.getException().getMessage() != null){
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(application, StringsRepository.REG_FAILED + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        });
                    } else{
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(application, StringsRepository.REG_FAILED , Toast.LENGTH_SHORT).show();
                                return;
                            }
                        });
                    }
                });
            }
        }).start();


//        RegisterThread registerThread = new RegisterThread(email, password, username);
//        new Thread(registerThread).start();
//        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(application.getMainExecutor(), task -> {
//
//            //If registration is successful
//            if(task.isSuccessful()){
//
//                //Get current user UUID
//                String userId = firebaseAuth.getCurrentUser().getUid();
//
//                //Get database reference to current user
//                databaseReference = FirebaseDatabase.getInstance().getReference().child(StringsRepository.USERS_CAP).child(userId);
//
//                //Create HashMap with all of the current user's info
//                HashMap<String, Object> hashMap = getUserHashMap(email, username, userId);
//
//                //attempts to create user object in firebase realtime database with generated hashMap
//                databaseReference.setValue(hashMap).addOnCompleteListener(task1 -> {
//
//                    //If successful -> post current user to userMutableLiveData object for ViewModel to observe and go to login screen
//                    if(task1.isSuccessful()){
//                        userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
//                        Toast.makeText(application, StringsRepository.USER_CREATED, Toast.LENGTH_SHORT).show();
//                        RegisterLoginActivity.viewPager.setCurrentItem(0);
//                    }
//                });
//
//
//            }
//            //else -> registration failed and error message shown if exists
//            else if(task.getException().getMessage() != null){
//                Toast.makeText(application, StringsRepository.REG_FAILED + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//            } else{
//                Toast.makeText(application, StringsRepository.REG_FAILED , Toast.LENGTH_SHORT).show();
//            }
//        });
    }


    //this method created a hashMap object of user details
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


    //attempts to login user
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void login(String email, String password){
        //Login with firebase sign in method
//        firebaseAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(application.getMainExecutor(), task -> {
//                    //If sign in is successful
//                    if(task.isSuccessful()){
//                        //Post current user object to userMutableLiveData object
//                        userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
//                    }
//                    //Else if sign in failed and there is an exception message to show -> display toast with exception message
//                    else if(task.getException().getMessage() != null){
//                        Toast.makeText(application, StringsRepository.LOGIN_FAILED + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                    //Else -> display toast with login failed message
//                    else{
//                        Toast.makeText(application, StringsRepository.LOGIN_FAILED , Toast.LENGTH_SHORT).show();
//                    }
//                });

//        LoginThread loginThread = new LoginThread(email, password);
//        new Thread(loginThread).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(application.getMainExecutor(), task -> {
                            //If sign in is successful
                            if(task.isSuccessful()){
                                //Post current user object to userMutableLiveData object
                                userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
                                return;
                            }
                            //Else if sign in failed and there is an exception message to show -> display toast with exception message
                            else if(task.getException().getMessage() != null){
                                Toast.makeText(application, StringsRepository.LOGIN_FAILED + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            //Else -> display toast with login failed message
                            else{
                                Toast.makeText(application, StringsRepository.LOGIN_FAILED , Toast.LENGTH_SHORT).show();
                                return;
                            }
                        });
            }
        }).start();

    }

    //Return LiveData object of a FirebaseUser which is the current user in this class
    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    //Return MutableLiveData Boolean object telling if user is logged out or not
    public MutableLiveData<Boolean> getLoggedOutMutableLiveData() {
        return loggedOutMutableLiveData;
    }

    //This method logs out a signed in user
    public void logout(){
        firebaseAuth.signOut();
        loggedOutMutableLiveData.postValue(true);
    }

 class LoginThread implements Runnable{

        String email, password;
     public LoginThread(String email, String password) {
         this.email = email;
         this.password = password;
     }

     @RequiresApi(api = Build.VERSION_CODES.P)
     @Override
     public void run() {
//         firebaseAuth.signInWithEmailAndPassword(email, password)
//                 .addOnCompleteListener(application.getMainExecutor(), task -> {
//                     //If sign in is successful
//                     if(task.isSuccessful()){
//                         //Post current user object to userMutableLiveData object
//                         userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
//                     }
//                     //Else if sign in failed and there is an exception message to show -> display toast with exception message
//                     else if(task.getException().getMessage() != null){
//                         Toast.makeText(application, StringsRepository.LOGIN_FAILED + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                     }
//                     //Else -> display toast with login failed message
//                     else{
//                         Toast.makeText(application, StringsRepository.LOGIN_FAILED , Toast.LENGTH_SHORT).show();
//                     }
//                 });
     }
 }

 class RegisterThread implements Runnable{

        String email, password, username;
     public RegisterThread(String email, String password, String username) {
         this.email = email;
         this.password = password;
         this.username = username;
     }

     @RequiresApi(api = Build.VERSION_CODES.P)
     @Override
     public void run() {
         firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(application.getMainExecutor(), task -> {

             //If registration is successful
             if(task.isSuccessful()){

                 //Get current user UUID
                 String userId = firebaseAuth.getCurrentUser().getUid();

                 //Get database reference to current user
                 databaseReference = FirebaseDatabase.getInstance().getReference().child(StringsRepository.USERS_CAP).child(userId);

                 //Create HashMap with all of the current user's info
                 HashMap<String, Object> hashMap = getUserHashMap(email, username, userId);

                 //attempts to create user object in firebase realtime database with generated hashMap
                 databaseReference.setValue(hashMap).addOnCompleteListener(task1 -> {

                     //If successful -> post current user to userMutableLiveData object for ViewModel to observe and go to login screen
                     if(task1.isSuccessful()){
                         userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
                         mainHandler.post(new Runnable() {
                             @Override
                             public void run() {
                            Toast.makeText(application, StringsRepository.USER_CREATED, Toast.LENGTH_SHORT).show();
                            RegisterLoginActivity.viewPager.setCurrentItem(0);
                             }
                         });

                     }
                 });


             }
             //else -> registration failed and error message shown if exists
             else if(task.getException().getMessage() != null){
                 mainHandler.post(new Runnable() {
                     @Override
                     public void run() {
                         Toast.makeText(application, StringsRepository.REG_FAILED + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                     }
                 });
             } else{
                 mainHandler.post(new Runnable() {
                     @Override
                     public void run() {
                         Toast.makeText(application, StringsRepository.REG_FAILED , Toast.LENGTH_SHORT).show();
                     }
                 });
             }
         });
     }
 }


}
