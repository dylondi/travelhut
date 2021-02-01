package com.example.travelhut.viewmodel.newsfeed.toolbar.users;

import android.app.Application;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModel;

import com.example.travelhut.model.UserSearchAppRepository;
import com.google.firebase.auth.FirebaseUser;


public class UserSearchAdapterViewModel extends ViewModel implements LifecycleObserver {

    UserSearchAppRepository userSearchAppRepository;
    //boolean userMutableLiveData;

    private String userId;

    public UserSearchAdapterViewModel(String userId) {
        userSearchAppRepository = new UserSearchAppRepository();
        this.userId = userId;
        //userMutableLiveData = userSearchAppRepository.isFollowing();
    }

//    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
//        return userMutableLiveData;
//    }

    //@OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public boolean isFollowing(){
        System.out.println(userSearchAppRepository.isFollowing(userId));
        return userSearchAppRepository.isFollowing(userId);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void disconnectListener() {

    }

    public void follow() {
        userSearchAppRepository.follow(userId);
    }

    public void unfollow() {
        userSearchAppRepository.unfollow(userId);
    }
}
