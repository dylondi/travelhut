package com.example.travelhut.viewmodel.main.newsfeed.toolbar.users;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.travelhut.model.main.profile.FollowersRepository;
import com.example.travelhut.model.main.profile.FollowingRepository;
import com.example.travelhut.model.main.posts.PostsRepository;
import com.example.travelhut.model.main.profile.ProfileRepository;
import com.google.firebase.database.DataSnapshot;

public class ProfileFragmentViewModel extends AndroidViewModel {

    //Instance Variables
    private ProfileRepository profileRepository;
    private FollowersRepository followersRepository;
    private FollowingRepository followingRepository;
    private PostsRepository postsRepository;

    //Constructor
    public ProfileFragmentViewModel(@NonNull Application application, String profileId) {
        super(application);
        profileRepository = new ProfileRepository(profileId);
        followersRepository = new FollowersRepository(profileId);
        followingRepository = new FollowingRepository(profileId);
        postsRepository = new PostsRepository();
    }

    public void follow(String userId) {
        profileRepository.follow(userId);
    }

    public void unFollow(String userId) {
        profileRepository.unFollow(userId);
    }

    public void followNotification(String userId) {
        profileRepository.followNotification(userId);
    }

    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return profileRepository;
    }

    @NonNull
    public MutableLiveData<Boolean> getIsFollowing() {
        return profileRepository.getIsFollowing();
    }

    @NonNull
    public LiveData<DataSnapshot> getFollowersSnapshot() {
        return followersRepository;
    }

    @NonNull
    public LiveData<DataSnapshot> getFollowingSnapshot() {
        return followingRepository;
    }

    @NonNull
    public LiveData<DataSnapshot> getPostsLiveData() {
        return postsRepository;
    }
}
