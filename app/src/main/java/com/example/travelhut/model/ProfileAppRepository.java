package com.example.travelhut.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileAppRepository extends LiveData<DataSnapshot> {
    private static final String LOG_TAG = "ProfileAppRepository";
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private final MyValueEventListener listener = new MyValueEventListener();

    //constructor
    public ProfileAppRepository() {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference(StringsRepository.USERS_CAP).child(firebaseUser.getUid());
    }

    //method called when an observer is active
    @Override
    protected void onActive() {
        Log.d(LOG_TAG, StringsRepository.ON_ACTIVE);
        //assign event listener to find changes in profile data
        reference.addValueEventListener(listener);
    }

    //method called when an observers lifecycle states has not started or resumed
    @Override
    protected void onInactive() {
        Log.d(LOG_TAG, StringsRepository.ON_INACTIVE);
        //remove event listener
        reference.removeEventListener(listener);
    }

    //event listener to find changes in data
    private class MyValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            setValue(dataSnapshot);
            if (this == null){
                return;
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            //Log.e(LOG_TAG, "Can't listen to query " + query, databaseError.toException());
        }
    }
}
