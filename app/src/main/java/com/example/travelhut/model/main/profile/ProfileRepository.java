package com.example.travelhut.model.main.profile;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.travelhut.model.utils.StringsRepository;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import static com.example.travelhut.model.utils.StringsRepository.USERS_CAP;

public class ProfileRepository extends LiveData<DataSnapshot> {

    //Instance Variables
    private static final String LOG_TAG = "ProfileAppRepository";
    private final ProfileEventListener profileEventListener = new ProfileEventListener();
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private MutableLiveData<Boolean> isFollowing;
    private MutableLiveData<Boolean> imageUploadedMutableLiveData;
    private MutableLiveData<String> uploadFailedMessage;

    //Constructors
    //Called when dealing with current users page
    public ProfileRepository() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference(USERS_CAP).child(firebaseUser.getUid());
        imageUploadedMutableLiveData = new MutableLiveData<>();
        uploadFailedMessage = new MutableLiveData<>();
    }

    //Called when viewing another users page
    public ProfileRepository(String profileId) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference(USERS_CAP).child(profileId);
        isFollowing = new MutableLiveData<>();
        checkIfFollows(profileId);
    }

    //This method checks if the current user is following another user matching the profileId parameter
    private void checkIfFollows(String profileId) {

        //DatabaseReference to current users following accounts
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(StringsRepository.FOLLOW_CAP).child(firebaseUser.getUid()).child(StringsRepository.FOLLOWING);

        //Add Single Value Event Listener to DatabaseReference
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Check if current user follows user with profileId
                if (snapshot.child(profileId).exists()) {
                    isFollowing.setValue(true);
                } else {
                    isFollowing.setValue(false);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Method called when an observer is active
    @Override
    protected void onActive() {
        Log.d(LOG_TAG, StringsRepository.ON_ACTIVE);
        //Assign event listener to find changes in profile data
        reference.addValueEventListener(profileEventListener);
    }

    //Method called when an observers lifecycle states has not started or resumed
    @Override
    protected void onInactive() {
        Log.d(LOG_TAG, StringsRepository.ON_INACTIVE);
        //Remove event listener
        reference.removeEventListener(profileEventListener);
    }

    //Event listener to find changes in data
    private class ProfileEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //Set value for dataSnapshot
            setValue(dataSnapshot);
            if (this == null) {
                return;
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    }

    //This method updates current user's profile data
    public void updateProfile(String displayName, String username, String bio, String url) {

        //DatabaseReference to current user in database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(USERS_CAP).child(firebaseUser.getUid());

        //Create HashMap
        HashMap<String, Object> hashMap = new HashMap<>();

        //Put all updated data in HashMap
        hashMap.put(StringsRepository.DISPLAY_NAME, displayName);
        hashMap.put(StringsRepository.USERNAME, username);
        hashMap.put(StringsRepository.BIO, bio);
        hashMap.put(StringsRepository.URL, url);

        //Update database reference to contain new data
        reference.updateChildren(hashMap);
    }

    //This method is called when current user follows another user
    public void follow(String userId) {
        //Updates db -> currentUser -> following -> otherUser -> true
        FirebaseDatabase.getInstance().getReference()
                .child(StringsRepository.FOLLOW_CAP)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(StringsRepository.FOLLOWING)
                .child(userId).setValue(true);

        //Updates db -> otherUser -> followers -> currentUser -> true
        FirebaseDatabase.getInstance().getReference()
                .child(StringsRepository.FOLLOW_CAP)
                .child(userId)
                .child(StringsRepository.FOLLOWERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

        isFollowing.setValue(true);

    }

    //This method is called when current user unfollows another user
    public void unFollow(String userId) {
        //Updates db -> currentUser -> following -> otherUser -> removes value
        FirebaseDatabase.getInstance().getReference()
                .child(StringsRepository.FOLLOW_CAP)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(StringsRepository.FOLLOWING)
                .child(userId).removeValue();

        //Updates db -> otherUser -> followers -> currentUser -> removes value
        FirebaseDatabase.getInstance().getReference()
                .child(StringsRepository.FOLLOW_CAP)
                .child(userId)
                .child(StringsRepository.FOLLOWERS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();

        isFollowing.setValue(false);

    }

    //This method returns a MutableLiveData object containing a boolean isFollowing representing if the current user if following the user's account their on
    public MutableLiveData<Boolean> getIsFollowing() {
        return isFollowing;
    }

    //This method creates a notification and uploads the object to firebase
    public void followNotification(String profileId) {

        //Database reference to new notification data
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StringsRepository.NOTIFICATIONS_CAP).child(profileId);

        //HashMap to store the notification details
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(StringsRepository.USER_ID, firebaseUser.getUid());
        hashMap.put(StringsRepository.TEXT, StringsRepository.STARTED_FOLLOWING_YOU);
        hashMap.put(StringsRepository.POST_ID, "");
        hashMap.put(StringsRepository.IS_POST, false);

        //Push data to database
        reference.push().setValue(hashMap);
    }

    //This method uploads a new image to firebase when user updates profile image
    public void uploadImage(Uri imageUri, String fileExtension) {

        //Storage reference to firebase storage for image uploads
        StorageReference storageRef = FirebaseStorage.getInstance().getReference(StringsRepository.UPLOADS);

        //Check null imageUri
        if (imageUri != null) {

            //Uniquely naming the image file reference to current time stamp and the file extension
            final StorageReference fileRef = storageRef.child(System.currentTimeMillis()
                    + StringsRepository.FULL_STOP + fileExtension);

            //Create a storage task to upload the imageUri to the file reference
            StorageTask uploadTask = fileRef.putFile(imageUri);

            //Returns a new Task that will be completed with the result of applying the specified Continuation to this Task
            uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileRef.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if (task.isSuccessful()) {
                    //Get image Uri result and store as a String
                    Uri downloadUri = task.getResult();
                    String myUrl = downloadUri.toString();

                    //Get current user's database reference
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(USERS_CAP).child(firebaseUser.getUid());

                    //Create HashMap to update existing user data with new image url
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put(StringsRepository.IMAGE_URL, myUrl);

                    //update data with HashMap
                    reference.updateChildren(hashMap);

                    //Post true too imageUploadedMutableLiveData containing boolean
                    imageUploadedMutableLiveData.postValue(true);
                }
                //If task is unsuccessful -> post false value to imageUploadedMutableLiveData and post failed string message
                else {
                    imageUploadedMutableLiveData.postValue(false);
                    uploadFailedMessage.postValue(StringsRepository.FAILED_CAP);
                }

                //If task completion fails -> post false value to imageUploadedMutableLiveData and post failed string message
            }).addOnFailureListener(e -> {
                imageUploadedMutableLiveData.postValue(false);
                uploadFailedMessage.postValue(e.getMessage());
            });
        }

        //Else -> post false value to imageUploadedMutableLiveData and post failed string message
        else {
            imageUploadedMutableLiveData.postValue(false);
            uploadFailedMessage.postValue(StringsRepository.NO_IMAGE_SELECTED);
        }
    }

    //This method returns the boolean valued MutableLiveData object called imageUploadedMutableLiveData
    public MutableLiveData<Boolean> getImageUploadedMutableLiveData(){
        return imageUploadedMutableLiveData;
    }

    //This method returns the String valued MutableLiveData object called imageUploadedMutableLiveData
    public MutableLiveData<String> getUploadFailedMessage(){
        return uploadFailedMessage;
    }
}
