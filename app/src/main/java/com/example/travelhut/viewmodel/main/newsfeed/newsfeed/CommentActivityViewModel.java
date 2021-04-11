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

    //This method notifies commentRepository that a comment needs to be created with message as param
    public void createComment(String comment){
        commentRepository.createComment(comment);
    }

    //This method notifies commentRepository that a notification needs to be created
    public void addNotification(String comment){
        commentRepository.addNotification(comment);
    }

    //This method returns the LiveData object from commentRepository containing image for comment
    @NonNull
    public LiveData<DataSnapshot> getCommentImageLiveData() {
        return commentImageRepository;
    }

    //This method returns the LiveData object from commentRepository containing list of comments in dataSnapshot
    @NonNull
    public LiveData<DataSnapshot> getCommentsLiveData() {
        return commentRepository;
    }
}
