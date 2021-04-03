package com.example.travelhut.model.main.trips;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.travelhut.model.utils.StringsRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class TripInfoRepository extends LiveData<DataSnapshot> {

    //Instance Variables
    private DatabaseReference tripInfoReference;
    private TripInfoEventListener tripInfoEventListener = new TripInfoEventListener();
    private static final String TAG = "TripInfoAppRepository";

    //Constructor
    public TripInfoRepository(String tripId) {
        tripInfoReference = FirebaseDatabase.getInstance().getReference(StringsRepository.TRIPS_CAP).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(tripId);
    }

    //Method called when an observer is active
    @Override
    protected void onActive() {
        Log.d(TAG, StringsRepository.ON_ACTIVE);
        //assign event listener to find changes in posts data
        tripInfoReference.addValueEventListener(tripInfoEventListener);
    }

    //Method called when an observers lifecycle states has not started or resumed
    @Override
    protected void onInactive() {
        Log.d(TAG, StringsRepository.ON_INACTIVE);
        //remove event listener
        tripInfoReference.removeEventListener(tripInfoEventListener);
    }

    //Event listener to find changes in data
    private class TripInfoEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    }
}
