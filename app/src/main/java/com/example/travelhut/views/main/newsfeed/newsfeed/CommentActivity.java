package com.example.travelhut.views.main.newsfeed.newsfeed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.travelhut.R;
import com.example.travelhut.viewmodel.main.newsfeed.newsfeed.CommentActivityViewModel;
import com.example.travelhut.viewmodel.main.newsfeed.newsfeed.CommentActivityViewModelFactory;
import com.example.travelhut.views.main.newsfeed.NewsFeedStrings;
import com.example.travelhut.model.objects.Comment;
import com.example.travelhut.model.objects.User;
import com.example.travelhut.views.main.newsfeed.newsfeed.utils.CommentsAdapter;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    //Instance Variables
    private CommentActivityViewModel commentActivityViewModel;
    private RecyclerView recyclerView;
    private CommentsAdapter adapter;
    private List<Comment> commentList;
    private EditText writeComment;
    private ImageView profileImage;
    private TextView commentPost;
    private String postId, publisherId;
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        //The next 3 lines get the String extras set in the previous activity
        Intent intent = getIntent();
        postId = intent.getStringExtra("postid");
        publisherId = intent.getStringExtra("publisher");

        commentActivityViewModel = ViewModelProviders.of(this, new CommentActivityViewModelFactory(this.getApplication(), postId, publisherId)).get(CommentActivityViewModel.class);

        //Declare and initialize LinearLayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        //Initialize views
        recyclerView = findViewById(R.id.comments_recycler_view);
        backArrow = findViewById(R.id.comments_back_arrow);
        writeComment = findViewById(R.id.write_comment);
        profileImage = findViewById(R.id.comment_profile_image);
        commentPost = findViewById(R.id.comment_post);

        //Fixes recycler view size to avoid unnecessary changes in size when data is changed
        recyclerView.setHasFixedSize(true);

        //Setting recyclerview's LinearLayoutManager
        recyclerView.setLayoutManager(linearLayoutManager);

        //Initialize commentList and adapter, then set adapter to recyclerView
        commentList = new ArrayList<>();
        adapter = new CommentsAdapter(this, commentList);
        recyclerView.setAdapter(adapter);


        backArrow.setOnClickListener(v -> finish());

        //Post comment onClickListener
        commentPost.setOnClickListener(v -> {

            //If comment is empty
            if (writeComment.getText().toString().equals("")) {
                Toast.makeText(CommentActivity.this, NewsFeedStrings.NO_EMPTY_COMMENT, Toast.LENGTH_SHORT).show();
            }else{
                createComment();
            }
        });

        getImage();
        loadComments();
    }


    //This method calls the ViewModel to create a comment given as a param
    private void createComment(){
        commentActivityViewModel.createComment(writeComment.getText().toString());
        addNotification();
        writeComment.setText("");
    }

    //This method calls the ViewModel to add a notification for the receiving user
    private void addNotification(){
        commentActivityViewModel.addNotification(writeComment.getText().toString());
    }


    //This method gets the profile image of the user that commented by observing relevant data in the ViewModel
    private void getImage(){

        //Get LiveData object from ViewModel
        LiveData<DataSnapshot> liveData = commentActivityViewModel.getCommentImageLiveData();

        //Observe LiveData object
        liveData.observe(this, dataSnapshot -> {

                //Get user data from dataSnapshot and store in User object
                User user = dataSnapshot.getValue(User.class);

                //Load the user profile image and set to profileImage view
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(profileImage);
        });
    }


    //This method gets the comments on a post by observing a List of Comments in the ViewModel
    private void loadComments(){

        //Get LiveData object from ViewModel
        LiveData<DataSnapshot> liveData = commentActivityViewModel.getCommentsLiveData();

        //Observe LiveData object
        liveData.observe(this, dataSnapshot -> {

            commentList.clear();

            //Iterate through List of dataSnapshot children (Comments)
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    //Get Comment object from snapshot and add it to commentList
                    commentList.add(snapshot.getValue(Comment.class));
                }

                //Notify adapter of updated data set
                adapter.notifyDataSetChanged();
        });

    }
}