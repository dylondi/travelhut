package com.example.travelhut.viewmodel.main.newsfeed.newsfeed;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.travelhut.model.CommentAppRepository;
import com.example.travelhut.model.CommentImageAppRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.UUID;

public class CommentActivityViewModel extends AndroidViewModel {

    private CommentAppRepository commentAppRepository;
    private CommentImageAppRepository commentImageAppRepository;
    //private String postId, publisherId;

    public CommentActivityViewModel(@NonNull Application application, String postId, String publisherId) {
        super(application);
//        this.postId = postId;
//        this.publisherId = publisherId;
        commentAppRepository = new CommentAppRepository(postId, publisherId);
        commentImageAppRepository = new CommentImageAppRepository();
    }

    public void createComment(String comment){
        commentAppRepository.createComment(comment);
    }

    public void addNotification(String comment){
        commentAppRepository.addNotification(comment);
    }

    @NonNull
    public LiveData<DataSnapshot> getCommentImageLiveData() {

        return commentImageAppRepository;
    }

    @NonNull
    public LiveData<DataSnapshot> getCommentsLiveData() {

        return commentAppRepository;
    }
}
