package com.example.travelhut.viewmodel.main.profile;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.travelhut.model.main.profile.FollowersRepository;
import com.example.travelhut.model.main.profile.FollowingRepository;
import com.example.travelhut.model.main.posts.PostsRepository;
import com.example.travelhut.model.main.profile.ProfileRepository;
import com.google.firebase.database.DataSnapshot;

public class ProfileActivityViewModel extends ViewModel{

    //Instance Variables
    private ProfileRepository profileRepository;
    private FollowersRepository followersRepository;
    private FollowingRepository followingRepository;
    private PostsRepository postsRepository;

    //Constructors
    public ProfileActivityViewModel(String profileId) {
        profileRepository = new ProfileRepository();
        followersRepository = new FollowersRepository(profileId);
        followingRepository = new FollowingRepository(profileId);
        postsRepository = new PostsRepository();
    }


    //This method returns a LiveData object containing a DataSnapshot
    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return profileRepository;
    }

    //This method returns a LiveData object containing a DataSnapshot containing the followers of the current user
    @NonNull
    public LiveData<DataSnapshot> getFollowersSnapshot() {
        return followersRepository;
    }

    //This method returns a LiveData object containing a DataSnapshot containing the following of the current user
    @NonNull
    public LiveData<DataSnapshot> getFollowingSnapshot() {
        return followingRepository;
    }

    //This method returns a LiveData object containing a DataSnapshot posts
    @NonNull
    public LiveData<DataSnapshot> getPostsLiveData() {
        return postsRepository;
    }
}
