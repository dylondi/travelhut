package com.example.travelhut.viewmodel.newsfeed;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.travelhut.model.AuthAppRepository;
//import com.example.travelhut.model.UsersAppRepository;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class NewsFeedActivityViewModel extends AndroidViewModel {

    private AuthAppRepository authAppRepository;
    private MutableLiveData<FirebaseUser> userMutableLiveData;
    private MutableLiveData<Boolean> loggedOutMutableLiveData;
    private Query userSearchQuery;


    public NewsFeedActivityViewModel(@NonNull Application application, String s) {
        super(application);

        authAppRepository = new AuthAppRepository(application);
        userMutableLiveData = authAppRepository.getUserMutableLiveData();
        loggedOutMutableLiveData = authAppRepository.getLoggedOutMutableLiveData();
        this.userSearchQuery = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(s)
                .endAt(s+"\uf8ff");
    }

    public void logout(){
        authAppRepository.logout();
    }

    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    public MutableLiveData<Boolean> getLoggedOutMutableLiveData() {
        return loggedOutMutableLiveData;
    }


    //private final UsersAppRepository liveData = new UsersAppRepository(FIREBASE_REF);

//    @NonNull
//    public LiveData<Query> getUserSearchQuery(String s){
//        Query FIREBASE_REF = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
//                .startAt(s)
//                .endAt(s+"\uf8ff");
//        final UsersAppRepository liveData = new UsersAppRepository(FIREBASE_REF);
//
//    }
//
//    @
//    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
//        return liveData;
//    }
}
