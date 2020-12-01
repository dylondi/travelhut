package com.example.travelhut.model;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.travelhut.views.authentication.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AuthAppRepository {

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

    public void register(String email, String username, String password){
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(application.getMainExecutor(), task -> {
            if(task.isSuccessful()){

                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                String userId = firebaseUser.getUid();

                databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", userId);
                hashMap.put("username", username);
                hashMap.put("email", email);
                hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/travelhut-a660a.appspot.com/o/placeholder.png?alt=media&token=af8ac3db-ebc3-479b-97f9-72b93a6a7e00");

                databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
                            Toast.makeText(application, "User Created", Toast.LENGTH_SHORT).show();
                            LoginActivity.viewPager.setCurrentItem(0);
                        }
                    }
                });


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
