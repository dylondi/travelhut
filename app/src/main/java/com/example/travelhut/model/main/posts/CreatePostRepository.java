package com.example.travelhut.model.main.posts;

import android.app.Application;
import android.app.ProgressDialog;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.travelhut.model.utils.StringsRepository;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;

public class CreatePostRepository extends LiveData<DataSnapshot> {

    //Instance Variables
    private StorageReference storageReference;
    private StorageTask uploadTask;
    private MutableLiveData<Boolean> imageUploadedMutableLiveData;
    private MutableLiveData<String> uploadFailedMessage;

    //Constructor
    public CreatePostRepository() {
        storageReference = FirebaseStorage.getInstance().getReference(StringsRepository.POSTS);
        imageUploadedMutableLiveData = new MutableLiveData<>();
        uploadFailedMessage = new MutableLiveData<>();
    }

    //This method will upload the post to firebase
    public void uploadImage(Uri imageUri, String fileExtension, String description){

        //Check null imageUri
        if(imageUri != null){

            //Unique name(using timestamp) assigned for the image reference stored in our database
            StorageReference fileRef = storageReference.child(System.currentTimeMillis() + StringsRepository.FULL_STOP + fileExtension);

            //Initialize storageTask by setting the image Uri Object to the storage reference
            uploadTask = fileRef.putFile(imageUri);

            //Returns a new Task that will be completed with the result of applying the specified Continuation to this Task
            uploadTask.continueWithTask((Continuation) task -> {
                if(!task.isComplete()){
                    throw task.getException();
                }
                return fileRef.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {

                //If Image is uploaded to Firebase Storage successfully
                if(task.isSuccessful()){

                    //Get image uri and assign to String variable
                    Uri downloadUri = task.getResult();
                    String myUrl = downloadUri.toString();

                    //Retrieve the database reference to all users posts
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StringsRepository.POSTS_CAP);

                    //Retrieve post key for post to be uploaded to
                    String postId = reference.push().getKey();

                    //Calling uploadPostToDatabase with our current post values
                    uploadPostToDatabase(myUrl, reference, postId, description);

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

    //This method uploads a HashMap object containing a post's details for Firebase Database
    private void uploadPostToDatabase(String myUrl, DatabaseReference reference, String postId, String description) {

        //Create HashMap
        HashMap<String, Object> hashMap = new HashMap<>();

        //Put all story data into HashMap
        hashMap.put(StringsRepository.POST_ID, postId);
        hashMap.put(StringsRepository.POST_IMAGE, myUrl);
        hashMap.put(StringsRepository.DESCRIPTION, description);
        hashMap.put(StringsRepository.PUBLISHER, FirebaseAuth.getInstance().getCurrentUser().getUid());

        //Set the value of the HashMap to the DatabaseReference referencing an empty reference
        reference.child(postId).setValue(hashMap);
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
