package com.example.travelhut.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PostsAppRepository extends LiveData<DataSnapshot> {


    private static final String TAG = "PostsAppRepository";

    private PostsEventListener listener = new PostsEventListener();
    private MutableLiveData<DatabaseReference> postMutableLiveData;

    DatabaseReference reference;

    //constructor
    public PostsAppRepository() {
        reference = FirebaseDatabase.getInstance().getReference(StringsRepository.POSTS_CAP);

    }

    //method called when an observer is active
    @Override
    protected void onActive() {
        Log.d(TAG, StringsRepository.ON_ACTIVE);
        //assign event listener to find changes in posts data
        reference.addValueEventListener(listener);
    }

    //method called when an observers lifecycle states has not started or resumed
    @Override
    protected void onInactive() {
        Log.d(TAG, StringsRepository.ON_INACTIVE);
        //remove event listener
        reference.removeEventListener(listener);
    }

    //event listener to find changes in data
    private class PostsEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            //Log.e(LOG_TAG, "Can't listen to query " + query, databaseError.toException());
        }
    }


//    public MutableLiveData<DataSnapshot> getPostMutableLiveData(String postid) {
//        postMutableLiveData.postValue(FirebaseDatabase.getInstance().getReference("Posts").child(postid));
//
//
//        return postMutableLiveData;
//    }
}
