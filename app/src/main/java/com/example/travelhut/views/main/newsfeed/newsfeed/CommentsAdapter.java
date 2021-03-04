package com.example.travelhut.views.main.newsfeed.newsfeed;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travelhut.R;
import com.example.travelhut.model.StringsRepository;
import com.example.travelhut.views.main.newsfeed.newsfeed.utils.Comment;
import com.example.travelhut.views.authentication.utils.User;
import com.example.travelhut.views.main.newsfeed.NewsFeedActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder>{


    private Context context;
    private List<Comment> comments;
    private FirebaseUser firebaseUser;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //gets current firebase user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //create comment object of current comment
        Comment comment = comments.get(position);

        //sets text of comment
        holder.comment.setText(comment.getComment());

        //sets the user data for user who commented
        getUserInfo(holder.profileImage, holder.username, comment.getPublisher());

        //OnClickListener for comment
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewsFeedActivity.class);
                intent.putExtra("publisherid", comment.getPublisher());
                context.startActivity(intent);
            }
        });

        //OnClickListener for user profile image
        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewsFeedActivity.class);
                intent.putExtra("publisherid", comment.getPublisher());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView profileImage;
        public TextView username, comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.comment_item_profile_image);
            username = itemView.findViewById(R.id.comment_item_username);
            comment = itemView.findViewById(R.id.comment_item_comment);
        }
    }


    //this method retrieves user info of use current comment
    private void getUserInfo(ImageView imageView, TextView username, String publisherId){

        //db ref
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(StringsRepository.USERS_CAP).child(publisherId);

        //ValueEventListener for ref
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                //two lines set profile image and username of user comment
                Glide.with(context).load(user.getImageurl()).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
