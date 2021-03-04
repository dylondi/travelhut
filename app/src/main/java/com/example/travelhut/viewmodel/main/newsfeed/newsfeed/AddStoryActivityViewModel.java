package com.example.travelhut.viewmodel.main.newsfeed.newsfeed;

import android.app.Application;
import android.app.ProgressDialog;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.travelhut.model.AddStoryAppRepository;
import com.example.travelhut.views.main.newsfeed.newsfeed.AddStoryActivity;

public class AddStoryActivityViewModel extends AndroidViewModel {

    private AddStoryAppRepository addStoryAppRepository;
    private Application application;

    public AddStoryActivityViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        addStoryAppRepository = new AddStoryAppRepository();

    }

    public void publishStory(Uri imageUri, String fileExtension, ProgressDialog progressDialog){
        //postsAppRepository.uploadImage(imageUri,fileExtension,application,progressDialog);

        addStoryAppRepository.publishStory(imageUri,fileExtension,application,progressDialog);
    }
}
