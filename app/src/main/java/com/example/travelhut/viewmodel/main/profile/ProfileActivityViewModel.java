package com.example.travelhut.viewmodel.main.profile;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.travelhut.model.FollowersAppRepository;
import com.example.travelhut.model.FollowingAppRepository;
import com.example.travelhut.model.PostsAppRepository;
import com.example.travelhut.model.ProfileAppRepository;
import com.google.firebase.database.DataSnapshot;

public class ProfileActivityViewModel extends ViewModel{


    ProfileAppRepository profileAppRepository;
    FollowersAppRepository followersAppRepository;
    FollowingAppRepository followingAppRepository;
    PostsAppRepository postsAppRepository;
    LiveData<DataSnapshot> liveData;

    public ProfileActivityViewModel(String profileId) {
        profileAppRepository = new ProfileAppRepository();
        followersAppRepository = new FollowersAppRepository(profileId);
        followingAppRepository = new FollowingAppRepository(profileId);
        postsAppRepository = new PostsAppRepository();
        //liveData = profileAppRepository.getUserMutableLiveData();

    }

    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {

        return profileAppRepository;
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
