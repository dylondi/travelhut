package com.example.travelhut.model.main.posts;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.travelhut.model.utils.StringsRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PostsRepository extends LiveData<DataSnapshot> {


    //Instance Variable
    private static final String TAG = "PostsAppRepository";
    private PostsEventListener postsEventListener = new PostsEventListener();
    private DatabaseReference reference;

    //Constructor
    public











































    PostsRepository() {
        reference = FirebaseDatabase.getInstance().getReference(StringsRepository.POSTS_CAP);
    }

    //Method called when an observer is active
    @Override
    protected void onActive() {
        Log.d(TAG, StringsRepository.ON_ACTIVE);
        //assign event listener to find changes in posts data
        reference.addValueEventListener(postsEventListener);
    }

    //Method called when an observers lifecycle states has not started or resumed
    @Override
    protected void onInactive() {
        Log.d(TAG, StringsRepository.ON_INACTIVE);
        //remove event listener
        reference.removeEventListener(postsEventListener);
    }

    //Event listener to find changes in data
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

}
