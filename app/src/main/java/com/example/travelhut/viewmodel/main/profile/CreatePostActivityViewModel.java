package com.example.travelhut.viewmodel.main.profile;

import android.app.Application;
import android.app.ProgressDialog;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.travelhut.model.CreatePostAppRepository;
import com.example.travelhut.model.PostsAppRepository;

public class CreatePostActivityViewModel extends AndroidViewModel {

    Application application;
    CreatePostAppRepository createPostAppRepository;
    public CreatePostActivityViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        createPostAppRepository = new CreatePostAppRepository();
    }

    public void uploadImage(Uri imageUri, String fileExtension, ProgressDialog progressDialog, String description){
        //postsAppRepository.uploadImage(imageUri,fileExtension,application,progressDialog);
        createPostAppRepository.uploadImage(imageUri,fileExtension,application,progressDialog, description);
    }
}
