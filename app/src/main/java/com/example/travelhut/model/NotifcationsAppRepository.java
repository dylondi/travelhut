package com.example.travelhut.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.travelhut.views.main.profile.toolbar.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;


public class NotifcationsAppRepository extends LiveData<DataSnapshot> {

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    private final MyValueEventListener listener = new MyValueEventListener();

    public NotifcationsAppRepository() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid());
    }


    //method called when an observer is active
    @Override
    protected void onActive() {
        //Log.d(LOG_TAG, StringsRepository.ON_ACTIVE);
        reference.addValueEventListener(listener);
    }

    //method called when an observers lifecycle states has not started or resumed
    @Override
    protected void onInactive() {
        //Log.d(LOG_TAG, StringsRepository.ON_INACTIVE);
        reference.removeEventListener(listener);
    }

    private void readNotifications(){

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setValue(dataSnapshot);
//                notificationList.clear();
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Notification notification = snapshot.getValue(Notification.class);
//                    notificationList.add(notification);
//                }
//
//                Collections.reverse(notificationList);
//                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }



    private class MyValueEventListener implements ValueEventListener {
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
