package com.example.travelhut.model.main.profile;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.travelhut.model.utils.StringsRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SinglePostRepository extends LiveData<DataSnapshot> {

    //Instance Variables
    private SinglePostEventListener singlePostEventListener = new SinglePostEventListener();
    private DatabaseReference postReference;
    private static final String TAG = "SinglePostAppRepository";

    public SinglePostRepository(String postid) {
        postReference = FirebaseDatabase.getInstance().getReference(StringsRepository.POSTS_CAP).child(postid);
    }

    //Method called when an observer is active
    @Override
    protected void onActive() {
        Log.d(TAG, StringsRepository.ON_ACTIVE);
        //Assign event listener to find changes in posts data
        postReference.addValueEventListener(singlePostEventListener);
    }

    //Method called when an observers lifecycle states has not started or resumed
    @Override
    protected void onInactive() {
        Log.d(TAG, StringsRepository.ON_INACTIVE);
        //remove event listener
        postReference.removeEventListener(singlePostEventListener);
    }

    //Event listener to find changes in data
    private class SinglePostEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            //Log.e(LOG_TAG, "Can't listen to query " + query, databaseError.toException());
        }
    }
}
