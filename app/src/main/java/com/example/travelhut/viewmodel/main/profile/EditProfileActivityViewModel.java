package com.example.travelhut.viewmodel.main.profile;

import android.app.Application;
import android.app.ProgressDialog;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.travelhut.model.FollowersAppRepository;
import com.example.travelhut.model.FollowingAppRepository;
import com.example.travelhut.model.ProfileAppRepository;
import com.google.firebase.database.DataSnapshot;

public class EditProfileActivityViewModel extends AndroidViewModel {


    ProfileAppRepository profileAppRepository;
    Application application;

    public EditProfileActivityViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        profileAppRepository = new ProfileAppRepository();

    }

    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {

        return profileAppRepository;
    }

    public void updateProfile(String displayName, String username, String bio, String url){
        profileAppRepository.updateProfile(displayName,username,bio,url);
    }

    public void uploadImage(Uri imageUri, String fileExtension,ProgressDialog progressDialog){
        profileAppRepository.uploadImage(imageUri,fileExtension,application,progressDialog);
    }

}
