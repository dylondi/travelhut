package com.example.travelhut.viewmodel.main.newsfeed.toolbar.users;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.travelhut.model.main.profile.FollowersRepository;
import com.example.travelhut.model.main.newsfeed.UserSearchRepository;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;


public class UserSearchAdapterViewModel extends ViewModel {

    //Instance Variables
    private UserSearchRepository userSearchRepository;
    private MutableLiveData<FirebaseUser> userMutableLiveData;
    private FollowersRepository followersRepository;

    //Constructor
    public UserSearchAdapterViewModel(String userId) {
        userSearchRepository = new UserSearchRepository();
        userMutableLiveData = userSearchRepository.getUserMutableLiveData();
        followersRepository = new FollowersRepository(userId);
    }

    //This method returns a LiveData object containing list of users the current user follows
    public LiveData<DataSnapshot> getFollowing() {
        return followersRepository;
    }

    //This method notifies userSearchRepository to follow user with userId
    public void follow(String userId) {
        userSearchRepository.follow(userId);
    }

    //This method notifies userSearchRepository to unfollow user with userId
    public void unFollow(String userId) {
        userSearchRepository.unFollow(userId);
    }
}
