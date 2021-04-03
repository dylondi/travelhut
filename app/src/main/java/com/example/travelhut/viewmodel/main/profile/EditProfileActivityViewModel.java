package com.example.travelhut.viewmodel.main.profile;

import android.app.Application;
import android.app.ProgressDialog;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.travelhut.model.main.profile.ProfileRepository;
import com.google.firebase.database.DataSnapshot;

public class EditProfileActivityViewModel extends AndroidViewModel {

    //Instance Variables
    private ProfileRepository profileRepository;
    private Application application;

    //Constructor
    public EditProfileActivityViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        profileRepository = new ProfileRepository();
    }

    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return profileRepository;
    }

    public void updateProfile(String displayName, String username, String bio, String url){
        profileRepository.updateProfile(displayName,username,bio,url);
    }

    public void uploadImage(Uri imageUri, String fileExtension,ProgressDialog progressDialog){
        profileRepository.uploadImage(imageUri,fileExtension,application,progressDialog);
    }

}
