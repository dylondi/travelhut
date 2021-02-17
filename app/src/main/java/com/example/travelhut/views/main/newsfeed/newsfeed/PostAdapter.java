package com.example.travelhut.views.main.newsfeed.newsfeed;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.example.travelhut.views.main.newsfeed.NewsFeedStrings;
import com.example.travelhut.views.main.newsfeed.newsfeed.utils.Post;
import com.example.travelhut.views.authentication.utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    private static final String TAG = "PostAdapter";

    public Context mContext;
    public List<Post> mPost;

    private FirebaseUser firebaseUser;


    //constructor
    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);

        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //current firebase user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //get current post from mPost list
        Post post = mPost.get(position);

        //set image for post
        Glide.with(mContext).load(post.getPostimage()).into(holder.post_image);

        //checks if post description is empty or not -> makes visible or not based on string
        if(post.getDescription().equals("")){
            holder.description.setVisibility(View.GONE);
        }else{
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }

        //get data of user that published current post
        publisherInfo(holder.profile_image, holder.username, holder.publisher, post.getPublisher());

        //check if current user already liked current post
        checkIfLiked(post.getPostid(), holder.like);

        //get number of likes on current post
        getNumOfLikes(holder.likes, post.getPostid());

        //get comments for current post
        getComments(post.getPostid(), holder.comments);


        //OnClickListener for like button
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if not liked then like
                if (holder.like.getTag().equals(NewsFeedStrings.LIKE)) {
                    FirebaseDatabase.getInstance().getReference().child(NewsFeedStrings.LIKES_CAP).child(post.getPostid())
                            .child(firebaseUser.getUid()).setValue(true);
                    addNotification(post.getPublisher(), post.getPostid());
                }
                //if already liked then unlike
                else {
                    FirebaseDatabase.getInstance().getReference().child(NewsFeedStrings.LIKES_CAP).child(post.getPostid())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        //comment OnClickListener
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postid", post.getPostid());
                intent.putExtra("publisher", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        //comments OnClickListener
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postid", post.getPostid());
                intent.putExtra("publisher", post.getPublisher());
                mContext.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView profile_image, post_image, like, comment;
        public TextView username, likes, publisher, description, comments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //assigning views
            profile_image = itemView.findViewById(R.id.post_profile_image);
            post_image = itemView.findViewById(R.id.image_post);
            like = itemView.findViewById(R.id.like_button);
            comment = itemView.findViewById(R.id.comment_button);
            username = itemView.findViewById(R.id.post_username);
            likes = itemView.findViewById(R.id.likes);
            publisher = itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.post_description);
            comments = itemView.findViewById(R.id.comments);
        }
    }

    //this method retrieved all comments on current post
    private void getComments(String postid, TextView comments){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(NewsFeedStrings.COMMENTS_CAP).child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount()<1){
                    comments.setText("");

                }else {
                    comments.setText("View all " + snapshot.getChildrenCount() + " comments");
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //this method checks if current user has already liked the current post
    private void checkIfLiked(String postId, ImageView imageView){

        //current firebase user
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //db ref
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(NewsFeedStrings.LIKES_CAP)
                .child(postId);

        //ref OnClickListener
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag(NewsFeedStrings.LIKED);
                }else{
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag(NewsFeedStrings.LIKE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });
    }

    //add notification *****NOT FINISHED*****
    private void addNotification(String userid, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "liked your post");
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);

        reference.push().setValue(hashMap);
    }


    //this method gets number of likes on current post
    private void getNumOfLikes(final TextView likes_text, String postId){

        //db ref
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(NewsFeedStrings.LIKES_CAP)
                .child(postId);

        //ref ValueEventListener
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes_text.setText(snapshot.getChildrenCount() + " likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //this method retrieves the publisher information of the current post
    private void publisherInfo(ImageView image_profile, TextView username, TextView publisher, String userid){

        //db ref
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StringsRepository.USERS_CAP).child(userid);


        //ref ValueEventListener
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                publisher.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}
