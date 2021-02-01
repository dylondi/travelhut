package com.example.travelhut.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserSearchAppRepository extends LiveData<DataSnapshot> {

    //private Application application;
    private MutableLiveData<Boolean> isFollowing;
   //private MutableLiveData<Boolean> userMutableLiveData;
    //private FirebaseUser firebaseUser;


    public UserSearchAppRepository() {
        //this.application = application;
        //this.userMutableLiveData = new MutableLiveData<>();
        isFollowing = new MutableLiveData<>();


    }

//    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
//        return userMutableLiveData;
//    }

    public boolean isFollowing(String userId) {
        final boolean[] x = new boolean[1];
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(userId).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userId).exists()) {
                    //isFollowing.setValue(true);
                    x[0] = true;
                    //button.setText("following");
                } else {
                    isFollowing.setValue(false);
                    x[0] = false;
                    //button.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return x[0];
    }

    public void follow(String userId) {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following").child(userId).setValue(true);
        FirebaseDatabase.getInstance().getReference().child("Follow").child(userId)
                .child("followers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
    }

    public void unfollow(String userId) {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following").child(userId).removeValue();
        FirebaseDatabase.getInstance().getReference().child("Follow").child(userId)
                .child("followers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
    }

//    public MutableLiveData<Boolean> getIsFollowing(String userId){
//        isFollowing(userId);
//        return isFollowing;
//    }
}
