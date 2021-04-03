package com.example.travelhut.viewmodel.main.newsfeed.newsfeed;

import android.app.Application;
import android.app.ProgressDialog;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.travelhut.model.main.newsfeed.UploadStoryRepository;

public class UploadStoryActivityViewModel extends AndroidViewModel {

    //Instance Variables
    private UploadStoryRepository uploadStoryRepository;
    private Application application;

    //Constructor
    public UploadStoryActivityViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        uploadStoryRepository = new UploadStoryRepository();

    }

    //This method calls uploadStory method of the UploadStoryRepository class
    public void publishStory(Uri imageUri, String fileExtension){
        uploadStoryRepository.uploadStory(imageUri,fileExtension);
    }

    public MutableLiveData<Boolean> getImageUploadedMutableLiveData(){
        return uploadStoryRepository.getImageUploadedMutableLiveData();
    }

    public MutableLiveData<String> getUploadFailedMessage(){
        return uploadStoryRepository.getUploadFailedMessage();
    }
}
