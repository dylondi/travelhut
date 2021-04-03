package com.example.travelhut.views.main.profile.toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.travelhut.R;
import com.example.travelhut.model.utils.StringsRepository;
import com.example.travelhut.viewmodel.main.profile.CreatePostActivityViewModel;
import com.example.travelhut.views.main.profile.ProfileActivity;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;

public class CreatePostActivity extends AppCompatActivity {

    //Instance Variables
    private Uri imageUri;
    private CreatePostActivityViewModel createPostActivityViewModel;
    private ImageView close, imageAdded, post;
    private EditText description;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        initViews();
        createPostActivityViewModel = ViewModelProviders.of(this).get(CreatePostActivityViewModel.class);

        //Discard post
        close.setOnClickListener(v -> {
            //Go to profile page
            startActivity(new Intent(CreatePostActivity.this, ProfileActivity.class));
        });


        //Confirm post
        post.setOnClickListener(v -> {
            //Upload post
            uploadImage();
        });

        //Sets aspect ratio of post to make sure it is square image
        CropImage.activity()
                .setAspectRatio(1, 1)
                .start(CreatePostActivity.this);



    }

    //Initialize Views
    private void initViews() {
        close = findViewById(R.id.create_post_back_arrow);
        imageAdded = findViewById(R.id.image_posted);
        post = findViewById(R.id.create_post_check);
        description = findViewById(R.id.description);
    }


    //Gets file extension of Uri
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

    //This method calls the upload image in the ViewModel to upload the post
    private void uploadImage(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(StringsRepository.POSTING_CAP);
        progressDialog.show();

        createPostActivityViewModel.uploadImage(imageUri, getFileExtension(this,imageUri), progressDialog, description.getText().toString());

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
