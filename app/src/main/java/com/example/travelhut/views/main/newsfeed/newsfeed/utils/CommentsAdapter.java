package com.example.travelhut.views.main.newsfeed.newsfeed.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travelhut.R;
import com.example.travelhut.model.utils.StringsRepository;
import com.example.travelhut.model.objects.Comment;
import com.example.travelhut.model.objects.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder>{


    //Instance Variables
    private Context context;
    private List<Comment> comments;

    //Constructor
    public CommentsAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentsAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        //Create comment object of current comment
        Comment currentComment = comments.get(position);

        //Sets text of comment
        viewHolder.commentText.setText(currentComment.getComment());

        //Sets the user data for user that commented
        retrieveProfileData(currentComment.getAuthor() ,viewHolder.profileImageComment, viewHolder.usernameComment);

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        //Instance Variables
        public ImageView profileImageComment;
        public TextView usernameComment, commentText;

        //Constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Initialize views
            usernameComment = itemView.findViewById(R.id.comment_item_username);
            profileImageComment = itemView.findViewById(R.id.comment_item_profile_image);
            commentText = itemView.findViewById(R.id.comment_item_comment);
        }
    }


    //This method retrieves user info of use current comment
    private void retrieveProfileData(String authorId, ImageView imageView, TextView username){

        //DatabaseReference to user
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child(StringsRepository.USERS_CAP).child(authorId);

        //ValueEventListener for DatabaseReference
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Get User object from snapshot
                User user = snapshot.getValue(User.class);

                //Next two lines set profile image and username of user comment
                username.setText(user.getUsername());
                Glide.with(context).load(user.getImageurl()).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
