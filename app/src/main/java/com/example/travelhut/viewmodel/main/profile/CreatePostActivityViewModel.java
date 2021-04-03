package com.example.travelhut.viewmodel.main.profile;

import android.app.Application;
import android.app.ProgressDialog;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.travelhut.model.main.posts.CreatePostRepository;

public class CreatePostActivityViewModel extends AndroidViewModel {

    //Instance Variables
    private Application application;
    private CreatePostRepository createPostRepository;

    //Constructor
    public CreatePostActivityViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        createPostRepository = new CreatePostRepository();
    }

    public void uploadImage(Uri imageUri, String fileExtension, ProgressDialog progressDialog, String description){
        createPostRepository.uploadImage(imageUri,fileExtension,application,progressDialog, description);
    }
}
