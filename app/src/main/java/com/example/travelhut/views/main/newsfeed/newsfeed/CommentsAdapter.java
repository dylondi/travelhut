package com.example.travelhut.views.main.newsfeed.newsfeed;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private FirebaseUser firebaseUser;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //Initialize firebaseUser to current user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //Create comment object of current comment
        Comment comment = comments.get(position);

        //Sets text of comment
        holder.comment.setText(comment.getComment());

        //Sets the user data for user that commented
        getUserInfo(holder.profileImage, holder.username, comment.getPublisher());

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        //Instance Variables
        public ImageView profileImage;
        public TextView username, comment;

        //Constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Initialize views
            profileImage = itemView.findViewById(R.id.comment_item_profile_image);
            username = itemView.findViewById(R.id.comment_item_username);
            comment = itemView.findViewById(R.id.comment_item_comment);
        }
    }


    //This method retrieves user info of use current comment
    private void getUserInfo(ImageView imageView, TextView username, String publisherId){

        //DatabaseReference to user
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(StringsRepository.USERS_CAP).child(publisherId);

        //ValueEventListener for DatabaseReference
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                //Next two lines set profile image and username of user comment
                Glide.with(context).load(user.getImageurl()).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
