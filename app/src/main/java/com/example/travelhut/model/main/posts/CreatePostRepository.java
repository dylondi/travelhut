package com.example.travelhut.model.main.posts;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.example.travelhut.model.utils.StringsRepository;
import com.example.travelhut.views.main.profile.ProfileActivity;
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

    //Constructor
    public CreatePostRepository() {
        storageReference = FirebaseStorage.getInstance().getReference(StringsRepository.POSTS);
    }

    //This method will upload the post to firebase
    public void uploadImage(Uri imageUri, String fileExtension, Application application, ProgressDialog progressDialog, String description){

        if(imageUri != null){

            StorageReference fileRef = storageReference.child(System.currentTimeMillis() + StringsRepository.FULL_STOP + fileExtension);

            uploadTask = fileRef.putFile(imageUri);

            //Returns a new Task that will be completed with the result of applying the specified Continuation to this Task
            //If task completion fails -> display toast
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

                    //Retrieve post key for post to be uploaded
                    String postId = reference.push().getKey();

                    //Calling uploadPostToDatabase with our current post values
                    uploadPostToDatabase(myUrl, reference, postId, description);

                    progressDialog.dismiss();

                    //Create and start intent to navigate to the ProfileActivity upon successful task completion
                    Intent profileIntent = new Intent(application, ProfileActivity.class);
                    profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    application.startActivity(profileIntent);
                    //application.finish();
                }
                //If task is unsuccessful -> display toast
                else{
                    Toast.makeText(application, StringsRepository.FAILED_CAP, Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> Toast.makeText(application, ""+e.getMessage(), Toast.LENGTH_SHORT).show());
        }
        //Else -> display toast informing no image was selected
        else{
            Toast.makeText(application, StringsRepository.NO_IMAGE_SELECTED, Toast.LENGTH_SHORT).show();

        }
    }

    //This method uploads a HashMap object containing a post's details for Firebase Database
    private void uploadPostToDatabase(String myUrl, DatabaseReference reference, String postId, String description) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(StringsRepository.POST_ID, postId);
        hashMap.put(StringsRepository.POST_IMAGE, myUrl);
        hashMap.put(StringsRepository.DESCRIPTION, description);
        hashMap.put(StringsRepository.PUBLISHER, FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.child(postId).setValue(hashMap);
    }
}
