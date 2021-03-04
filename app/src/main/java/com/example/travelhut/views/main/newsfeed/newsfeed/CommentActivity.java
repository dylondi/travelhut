package com.example.travelhut.views.main.newsfeed.newsfeed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.travelhut.R;
import com.example.travelhut.viewmodel.main.newsfeed.newsfeed.CommentActivityViewModel;
import com.example.travelhut.viewmodel.main.newsfeed.newsfeed.CommentActivityViewModelFactory;
import com.example.travelhut.views.main.newsfeed.NewsFeedStrings;
import com.example.travelhut.views.main.newsfeed.newsfeed.utils.Comment;
import com.example.travelhut.views.authentication.utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {


    private CommentActivityViewModel commentActivityViewModel;
    private RecyclerView recyclerView;
    private CommentsAdapter adapter;
    private List<Comment> commentList;

    private EditText writeComment;
    private ImageView profileImage;
    private TextView commentPost;

    private String postId;
    private String publisherId;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        //these next 3 lines get the String extras set in the previous activity
        Intent intent = getIntent();
        postId = intent.getStringExtra("postid");
        publisherId = intent.getStringExtra("publisher");

        commentActivityViewModel = ViewModelProviders.of(this, new CommentActivityViewModelFactory(this.getApplication(), postId, publisherId)).get(CommentActivityViewModel.class);

        //declare and initialize this activity's LinearLayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        //assign recyclerView
        recyclerView = findViewById(R.id.comments_recycler_view);
        //fixes recycler view size to avoid unnecessary changes in size when data is changed
        recyclerView.setHasFixedSize(true);
        //setting recyclerview's LinearLayoutManager
        recyclerView.setLayoutManager(linearLayoutManager);


        commentList = new ArrayList<>();
        adapter = new CommentsAdapter(this, commentList);
        recyclerView.setAdapter(adapter);

        ImageView backArrow = findViewById(R.id.comments_back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //assigning views
        writeComment = findViewById(R.id.write_comment);
        profileImage = findViewById(R.id.comment_profile_image);
        commentPost = findViewById(R.id.comment_post);

        //getting currrent firebase user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



        //post comment onClickListener
        commentPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //if comment is empty
                if (writeComment.getText().toString().equals("")) {
                    Toast.makeText(CommentActivity.this, NewsFeedStrings.NO_EMPTY_COMMENT, Toast.LENGTH_SHORT).show();
                }else{
                    createComment();
                }
            }
        });


        getImage();
        loadComments();


    }


    //this method creates a comment and pushed to firebase
    private void createComment(){
        commentActivityViewModel.createComment(writeComment.getText().toString());
        addNotification();
        writeComment.setText("");
    }

    //this method adds a notification ****NOT FINISHED****
    private void addNotification(){
        commentActivityViewModel.addNotification(writeComment.getText().toString());
    }


    //this method gets the profile image of the user that commented
    private void getImage(){
        LiveData<DataSnapshot> liveData = commentActivityViewModel.getCommentImageLiveData();

        liveData.observe(this, dataSnapshot -> {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(profileImage);
        });
    }


    //this method gets all the previous comments for a given post
    private void loadComments(){

        LiveData<DataSnapshot> liveData = commentActivityViewModel.getCommentsLiveData();

        liveData.observe(this, dataSnapshot -> {

            commentList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                adapter.notifyDataSetChanged();
        });

    }
}