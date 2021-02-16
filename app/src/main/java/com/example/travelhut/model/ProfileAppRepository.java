package com.example.travelhut.model;

import android.app.Application;
import android.app.ProgressDialog;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.example.travelhut.views.main.profile.EditProfileActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

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


    public void updateProfile(String displayName, String username, String bio, String url){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("displayname", displayName);
        hashMap.put("username", username);
        hashMap.put("bio", bio);
        hashMap.put("url", url);

        reference.updateChildren(hashMap);
    }


    public void uploadImage(Uri imageUri, String fileExtension, Application application, ProgressDialog progressDialog){

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("uploads");


        //Log.d(TAG, "uploadImage: file extension: " + getFileExtension(this, imageUri));
        if(imageUri != null){
           // Log.d(TAG, "uploadImage: imageUri: " + imageUri.toString());

            final StorageReference fileRef = storageRef.child(System.currentTimeMillis()
                    + "." + fileExtension);




            StorageTask uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String myUrl = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageurl", myUrl);

                        reference.updateChildren(hashMap);

                        progressDialog.dismiss();
                    }else{
                        Toast.makeText(application, StringsRepository.FAILED_CAP, Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(application, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(application, StringsRepository.NO_IMAGE_SELECTED, Toast.LENGTH_SHORT).show();
        }
    }
}
