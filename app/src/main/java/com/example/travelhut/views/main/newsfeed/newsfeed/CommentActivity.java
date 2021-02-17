package com.example.travelhut.views.main.newsfeed.newsfeed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.travelhut.views.main.newsfeed.NewsFeedStrings;
import com.example.travelhut.views.main.newsfeed.newsfeed.utils.Comment;
import com.example.travelhut.views.authentication.utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private List<Comment> commentList;

    EditText writeComment;
    ImageView profileImage;
    TextView commentPost;

    String postId;
    String publisherId;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

//        Toolbar toolbar = findViewById(R.id.comments_toolbar);
//
//        setSupportActionBar(toolbar);

        //declare and initialize this activity's LinearLayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        //assign recyclerView
        recyclerView = findViewById(R.id.comments_recycler_view);
        //fixes recycler view size to avoid unnecessary changes in size when data is changed
        recyclerView.setHasFixedSize(true);
        //setting recyclerview's LinearLayoutManager
        recyclerView.setLayoutManager(linearLayoutManager);


        commentList = new ArrayList<>();
        adapter = new CommentAdapter(this, commentList);
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

        //getting currrent firebase 0user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //these next 3 lines get the String extras set in the previous activity
        Intent intent = getIntent();
        postId = intent.getStringExtra("postid");
        publisherId = intent.getStringExtra("publisher");

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
        readComments();


    }


    //this method creates a comment and pushed to firebase
    private void createComment(){

        //database ref
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", writeComment.getText().toString());
        hashMap.put("publisher", firebaseUser.getUid());

        reference.push().setValue(hashMap);
        addNotification();
        writeComment.setText("");
    }

    //this method adds a notification ****NOT FINISHED****
    private void addNotification(){

        //database ref
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "commented: " + writeComment.getText().toString());
        hashMap.put("postid", postId);
        hashMap.put("ispost", true);

        reference.push().setValue(hashMap);
    }


    //this method gets the profile image of the user that commented
    private void getImage(){

        //database ref
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        //ref valueEventListener
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(profileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //this method gets all the previous comments for a given post
    private void readComments(){

        //database ref
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postId);

        //ref valueEventListener
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    commentList.add(comment);
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}