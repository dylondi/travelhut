package com.example.travelhut.viewmodel.main.profile;

import android.app.Application;
import android.app.ProgressDialog;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

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

    //This method notifies createPostRepository to upload a new post to firebase
    public void uploadImage(Uri imageUri, String fileExtension, String description){
        createPostRepository.uploadImage(imageUri,fileExtension, description);
    }

    //This method returns LiveData object containing a boolean which indicates whether the post was uploaded successfully or not
    public MutableLiveData<Boolean> getImageUploadedMutableLiveData(){
        return createPostRepository.getImageUploadedMutableLiveData();
    }

    //This method returns LiveData object containing a message describing the reason the post failed, if it did fail
    public MutableLiveData<String> getUploadFailedMessage(){
        return createPostRepository.getUploadFailedMessage();
    }
}
