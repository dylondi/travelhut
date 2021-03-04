package com.example.travelhut.viewmodel.main.newsfeed.toolbar.users;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.travelhut.model.FollowersAppRepository;
import com.example.travelhut.model.UserSearchAppRepository;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;


public class UserSearchAdapterViewModel extends ViewModel{

    UserSearchAppRepository userSearchAppRepository;
    private MutableLiveData<FirebaseUser> userMutableLiveData;
    private MutableLiveData<DatabaseReference> referenceMutableLiveData;
    private MutableLiveData<Boolean> isFollowing;
    private FollowersAppRepository followersAppRepository;
    //boolean userMutableLiveData;

    private String userId;

    public UserSearchAdapterViewModel(String userId) {
        userSearchAppRepository = new UserSearchAppRepository();
        userMutableLiveData = userSearchAppRepository.getUserMutableLiveData();
        referenceMutableLiveData = userSearchAppRepository.getReferenceMutableLiveData();
        this.userId = userId;
        isFollowing = userSearchAppRepository.getIsFollowing(userId);
        followersAppRepository = new FollowersAppRepository(userId);

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

    public LiveData<DataSnapshot> getFollowing() {
        return followersAppRepository;
    }

    public void unFollow(String userId) {
        userSearchAppRepository.unFollow(userId);
    }
}
