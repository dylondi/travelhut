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
import com.google.firebase.database.DatabaseReference;


public class UserSearchAdapterViewModel extends ViewModel{

    UserSearchAppRepository userSearchAppRepository;
    private MutableLiveData<FirebaseUser> userMutableLiveData;
    private MutableLiveData<DatabaseReference> referenceMutableLiveData;
    private MutableLiveData<Boolean> isFollowing;
    //boolean userMutableLiveData;

    private String userId;

    public UserSearchAdapterViewModel(String userId) {
        userSearchAppRepository = new UserSearchAppRepository();
        userMutableLiveData = userSearchAppRepository.getUserMutableLiveData();
        referenceMutableLiveData = userSearchAppRepository.getReferenceMutableLiveData();
        this.userId = userId;
        isFollowing = userSearchAppRepository.getIsFollowing(userId);

        //userMutableLiveData = userSearchAppRepository.isFollowing();
    }

    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }

//    //@OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
//    public boolean isFollowing(){
//        System.out.println(userSearchAppRepository.isFollowing(userId));
//        return userSearchAppRepository.isFollowing(userId);
//    }

    public void follow(String userId) {
        userSearchAppRepository.follow(userId);
    }
    public MutableLiveData<DatabaseReference> getReferenceMutableLiveData() {
        return referenceMutableLiveData;
    }

    public MutableLiveData<Boolean> getIsFollowing() {
        return isFollowing;
    }

    public void unfollow(String userId) {
        userSearchAppRepository.unfollow(userId);
    }
}
