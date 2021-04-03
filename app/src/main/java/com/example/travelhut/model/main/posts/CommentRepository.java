package com.example.travelhut.model.main.posts;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.travelhut.model.utils.StringsRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CommentRepository extends LiveData<DataSnapshot> {


    //Instance Variables
    private String postId;
    private String publisherId;
    private FirebaseUser firebaseUser;
    private DatabaseReference commentsReference;
    private CommentsEventListener commentsEventListener = new CommentsEventListener();
    private static final String TAG = "CommentAppRepository";

    //Constructor
    public CommentRepository(String postId, String publisherId) {
        this.postId = postId;
        this.publisherId = publisherId;
        commentsReference = FirebaseDatabase.getInstance().getReference(StringsRepository.COMMENTS_CAP).child(postId);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    //method called when an observer is active
    @Override
    protected void onActive() {
        Log.d(TAG, StringsRepository.ON_ACTIVE);
        //assign event listener to find changes in posts data
        commentsReference.addValueEventListener(commentsEventListener);
    }

    //method called when an observers lifecycle states has not started or resumed
    @Override
    protected void onInactive() {
        Log.d(TAG, StringsRepository.ON_INACTIVE);
        //remove event listener
        commentsReference.removeEventListener(commentsEventListener);
    }

    //event listener to find changes in data
    private class CommentsEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    }

    //This method creates a comment HashMap and pushes the HashMap to the database with the commentsReference DatabaseReference
    public void createComment(String comment){

        //Create HashMap and put comment and publisher into HashMap
        new Thread(() -> {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(StringsRepository.COMMENT, comment);
            hashMap.put(StringsRepository.PUBLISHER, firebaseUser.getUid());

            commentsReference.push().setValue(hashMap);
        }).start();
    }

    //This method adds a notification to the receiving users notifications database section
    public void addNotification(String comment){


        new Thread(() -> {
            //Get reference to receiving users Notifications database section
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StringsRepository.NOTIFICATIONS_CAP).child(publisherId);

            //Create HashMap and put relevant info into HashMap
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(StringsRepository.USER_ID, firebaseUser.getUid());
            hashMap.put(StringsRepository.TEXT, StringsRepository.COMMENTED + comment);
            hashMap.put(StringsRepository.POST_ID, postId);
            hashMap.put(StringsRepository.IS_POST, true);

            //Push HashMap to database
            reference.push().setValue(hashMap);
        }).start();
    }
}
