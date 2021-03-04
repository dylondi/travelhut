package com.example.travelhut.viewmodel.main.newsfeed.toolbar.users;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.travelhut.model.FollowersAppRepository;
import com.example.travelhut.model.FollowingAppRepository;
import com.example.travelhut.model.PostsAppRepository;
import com.example.travelhut.model.ProfileAppRepository;
import com.example.travelhut.model.UserSearchAppRepository;
import com.google.firebase.database.DataSnapshot;

public class ProfileFragmentViewModel extends AndroidViewModel {

    private ProfileAppRepository profileAppRepository;
    private FollowersAppRepository followersAppRepository;
    private FollowingAppRepository followingAppRepository;
    private PostsAppRepository postsAppRepository;


    public ProfileFragmentViewModel(@NonNull Application application, String profileId) {
        super(application);
        profileAppRepository = new ProfileAppRepository(profileId);
        followersAppRepository = new FollowersAppRepository(profileId);
        followingAppRepository = new FollowingAppRepository(profileId);
        postsAppRepository = new PostsAppRepository();


    }

    public void follow(String userId) {
        profileAppRepository.follow(userId);
    }
    public void unFollow(String userId) {
        profileAppRepository.unFollow(userId);
    }
    public void followNotification(String userId) {
        profileAppRepository.followNotification(userId);
    }
    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {

        return profileAppRepository;
    }

    @NonNull
    public MutableLiveData<Boolean> getIsFollowing(){
        return profileAppRepository.getIsFollowing();
    }

    @NonNull
    public LiveData<DataSnapshot> getFollowersSnapshot() {

        return followersAppRepository;
    }

    @NonNull
    public LiveData<DataSnapshot> getFollowingSnapshot() {

        return followingAppRepository;
    }
    @NonNull
    public LiveData<DataSnapshot> getPostsLiveData() {
        return postsAppRepository;
    }
}
