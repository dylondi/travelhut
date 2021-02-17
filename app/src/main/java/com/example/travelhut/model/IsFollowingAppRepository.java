package com.example.travelhut.model;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class IsFollowingAppRepository extends LiveData<DataSnapshot> {

    DatabaseReference reference;
    FirebaseUser firebaseUser;
    IsFollowingEventListener isFollowingEventListener;

    public IsFollowingAppRepository() {
        isFollowingEventListener = new IsFollowingEventListener();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference()
                .child(StringsRepository.FOLLOW_CAP).child(firebaseUser.getUid()).child(StringsRepository.FOLLOWING);

    }



    @Override
    protected void onActive() {
        //Log.d(TAG, StringsRepository.ON_ACTIVE);
        //assign event listener to find changes in posts data
        reference.addValueEventListener(isFollowingEventListener);
    }

    //method called when an observers lifecycle states has not started or resumed
    @Override
    protected void onInactive() {
       // Log.d(TAG, StringsRepository.ON_INACTIVE);
        //remove event listener
        reference.removeEventListener(isFollowingEventListener);
    }

    private class IsFollowingEventListener implements ValueEventListener {
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
