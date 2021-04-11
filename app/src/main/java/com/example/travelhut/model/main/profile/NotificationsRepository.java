package com.example.travelhut.model.main.profile;

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


public class NotificationsRepository extends LiveData<DataSnapshot> {

    //Instance Variables
    private static final String TAG = "NotificationsRepository";
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private final NotificationsEventListener notificationsEventListener = new NotificationsEventListener();

    //Constructor
    public NotificationsRepository() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid());
    }

    //Method called when an observer is active
    @Override
    protected void onActive() {
        Log.d(TAG, StringsRepository.ON_ACTIVE);
        reference.addValueEventListener(notificationsEventListener);
    }

    //Method called when an observers lifecycle states has not started or resumed
    @Override
    protected void onInactive() {
        Log.d(TAG, StringsRepository.ON_INACTIVE);
        reference.removeEventListener(notificationsEventListener);
    }

    private class NotificationsEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, "Can't listen to reference " + reference.toString(), databaseError.toException());
        }
    }
}
