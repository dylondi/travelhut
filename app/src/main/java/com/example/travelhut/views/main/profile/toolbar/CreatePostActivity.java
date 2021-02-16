package com.example.travelhut.views.main.profile.toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travelhut.R;
import com.example.travelhut.model.StringsRepository;
import com.example.travelhut.views.main.profile.ProfileActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.HashMap;

public class CreatePostActivity extends AppCompatActivity {

    private static final String TAG = "CreatePostActivity";
    Uri imageUri;
    String myUrl = "";
    StorageTask uploadTask;
    StorageReference storageReference;

    ImageView close, imageAdded, post;

    EditText description;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        close = findViewById(R.id.create_post_back_arrow);
        imageAdded = findViewById(R.id.image_posted);
        post = findViewById(R.id.create_post_check);
        description = findViewById(R.id.description);

        storageReference = FirebaseStorage.getInstance().getReference(StringsRepository.POSTS);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreatePostActivity.this, ProfileActivity.class));
            }
        });


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        CropImage.activity()
                .setAspectRatio(1, 1)
                .start(CreatePostActivity.this);



    }


    public static String getFileExtension(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }


//    private String getFileExtension(Uri uri){
//
//        ContentResolver cR = getContentResolver();
//        MimeTypeMap mime = MimeTypeMap.getSingleton();
//        String type = mime.getExtensionFromMimeType(cR.getType(uri));
//    }

    private void uploadImage(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(StringsRepository.POSTING_CAP);
        progressDialog.show();


        Log.d(TAG, "uploadImage: file extension: " + getFileExtension(this, imageUri));
        if(imageUri != null){
            Log.d(TAG, "uploadImage: imageUri: " + imageUri.toString());
//            StorageReference fileRef = storageReference.child(System.currentTimeMillis()
//            + "." + getFileExtension(imageUri));

            StorageReference fileRef = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(this,imageUri));




            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isComplete()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StringsRepository.POSTS_CAP);

                        String postId = reference.push().getKey();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put(StringsRepository.POST_ID, postId);
                        hashMap.put(StringsRepository.POST_IMAGE, myUrl);
                        hashMap.put(StringsRepository.DESCRIPTION, description.getText().toString());
                        hashMap.put(StringsRepository.PUBLISHER, FirebaseAuth.getInstance().getCurrentUser().getUid());

                        reference.child(postId).setValue(hashMap);

                        progressDialog.dismiss();

                        startActivity(new Intent(CreatePostActivity.this, ProfileActivity.class));
                        finish();
                    }else{
                        Toast.makeText(CreatePostActivity.this, StringsRepository.FAILED_CAP, Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreatePostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(CreatePostActivity.this, StringsRepository.NO_IMAGE_SELECTED, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            imageAdded.setImageURI(imageUri);
        }else{
            Toast.makeText(this, StringsRepository.SOMETHING_GONE_WRONG, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CreatePostActivity.this, ProfileActivity.class));
            finish();
        }
    }
}
