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

public class CommentImageRepository extends LiveData<DataSnapshot> {

    //Instance Variables
    private static final String TAG = "CommentImageAppRepository";
    private CommentImageEventListener commentImageEventListener = new CommentImageEventListener();
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;

    //Constructor
    public CommentImageRepository() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference(StringsRepository.USERS_CAP).child(firebaseUser.getUid());
    }

    //Method called when an observer is active
    @Override
    protected void onActive() {
        Log.d(TAG, StringsRepository.ON_ACTIVE);
        //Assign event listener to find changes in posts data
        reference.addValueEventListener(commentImageEventListener);
    }

    //Method called when an observers lifecycle states has not started or resumed
    @Override
    protected void onInactive() {
        Log.d(TAG, StringsRepository.ON_INACTIVE);
        //Remove event listener
        reference.removeEventListener(commentImageEventListener);
    }

    //Event listener to find changes in data
    private class CommentImageEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //Update dataSnapshot with new data
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, "Can't listen to reference " + reference.toString(), databaseError.toException());
        }
    }
}
