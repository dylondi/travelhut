package com.example.travelhut.viewmodel.main.profile;

import android.app.Application;
import android.app.ProgressDialog;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

    //This method notifies profileRepository to update the current user data with new data
    public void updateProfile(String displayName, String username, String bio, String url){
        profileRepository.updateProfile(displayName,username,bio,url);
    }

    //This method notifies profileRepository to upload an image to firebase
    public void uploadImage(Uri imageUri, String fileExtension){
        profileRepository.uploadImage(imageUri,fileExtension);
    }

    //This method returns a LiveData object containing a DataSnapshot
    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData() {
        return profileRepository;
    }

    //This method returns a LiveData object containing a boolean which indicates whether the image was uploaded successfully or not
    @NonNull
    public MutableLiveData<Boolean> getImageUploadedMutableLiveData(){
        return profileRepository.getImageUploadedMutableLiveData();
    }

    //This method returns a LiveData object containing a String representing the upload failed message
    @NonNull
    public MutableLiveData<String> getUploadFailedMessage(){
        return profileRepository.getUploadFailedMessage();
    }
}
