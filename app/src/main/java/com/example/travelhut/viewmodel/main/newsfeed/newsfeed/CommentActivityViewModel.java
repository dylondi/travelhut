package com.example.travelhut.viewmodel.main.newsfeed.newsfeed;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.travelhut.model.main.posts.CommentRepository;
import com.example.travelhut.model.main.posts.CommentImageRepository;
import com.google.firebase.database.DataSnapshot;

public class CommentActivityViewModel extends AndroidViewModel {

    //Instance Variables
    private CommentRepository commentRepository;
    private CommentImageRepository commentImageRepository;

    //Constructor
    public CommentActivityViewModel(@NonNull Application application, String postId, String publisherId) {
        super(application);
        commentRepository = new CommentRepository(postId, publisherId);
        commentImageRepository = new CommentImageRepository();
    }

    public void createComment(String comment){
        commentRepository.createComment(comment);
    }

    public void addNotification(String comment){
        commentRepository.addNotification(comment);
    }

    @NonNull
    public LiveData<DataSnapshot> getCommentImageLiveData() {
        return commentImageRepository;
    }

    @NonNull
    public LiveData<DataSnapshot> getCommentsLiveData() {
        return commentRepository;
    }
}
