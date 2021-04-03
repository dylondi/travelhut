package com.example.travelhut.views.main.newsfeed.newsfeed;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.travelhut.R;
import com.example.travelhut.model.utils.StringsRepository;
import com.example.travelhut.views.main.newsfeed.NewsFeedStrings;
import com.example.travelhut.model.objects.Post;
import com.example.travelhut.model.objects.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;


public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    //Instance Variables
    private FirebaseUser firebaseUser;
    public Context context;
    public List<Post> posts;
    private String userId, postId;

    //Constructor
    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_view, parent, false);
        return new PostsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        //Initialize current firebase user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //Get current post from posts list
        Post post = posts.get(position);

        //Initialize userId and postId
        userId = post.getPublisher();
        postId = post.getPostid();

        //Set screenWidth variable to current screen width
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        //Set image for post
        Glide.with(context).load(post.getPostimage()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                //Show ProgressBar while image is loading
                viewHolder.progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                //Hide ProgressBar
                viewHolder.progressBar.setVisibility(View.GONE);
                return false;
            }
        }).apply(new RequestOptions().override(screenWidth, screenWidth)).into(viewHolder.postImageView);

        //Checks if post description is empty or not -> makes visible or not based on value of post.getDescription()
        if (post.getDescription().isEmpty()||post.getDescription().equals("")) {
            viewHolder.descriptionTextView.setVisibility(View.GONE);
        } else {
            showDescription(viewHolder, post);
        }

        //Get data of user that published current post
        postOwnerDetails(viewHolder.ownerProfileImageView, viewHolder.usernameTextView, viewHolder.ownerTextView, post.getPublisher());

        //Check if current user already liked current post
        checkIfLikedAndGetNumOfLikes(post.getPostid(), viewHolder);

        //Get comments for current post
        loadCommentsOnPost(viewHolder, post.getPostid());

        //Set OnClickListener for like button
        viewHolder.likeImageView.setOnClickListener(view -> {

            //if not liked then like
            ifNotLikedThenLike(viewHolder, post);
        });

        //Set OnClickListener for comment
        viewHolder.commentImageView.setOnClickListener(v -> {
            navigateToComments(post);
        });

        //Set OnClickListener for commentS
        viewHolder.commentsTextView.setOnClickListener(v -> {
            navigateToComments(post);
        });

        //Set OnClickListener for image post -> used to implement double click to like
        viewHolder.postImageView.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick() {
            }

            @Override
            public void onDoubleClick() {
                ifNotLikedThenLike(viewHolder, post);
            }
        });

    }

    //This method navigates to CommentActivity
    private void navigateToComments(Post post) {

        //Create Intent to go to CommentActivity
        Intent intent = new Intent(context, CommentActivity.class);

        //Put postId and publisher as string extras
        intent.putExtra(StringsRepository.POST_ID, post.getPostid());
        intent.putExtra(StringsRepository.PUBLISHER, post.getPublisher());
        context.startActivity(intent);
    }

    private void showDescription(@NonNull ViewHolder viewHolder, Post post) {
        viewHolder.descriptionTextView.setVisibility(View.VISIBLE);
        viewHolder.descriptionTextView.setText(post.getDescription());
    }

    private void ifNotLikedThenLike(@NonNull ViewHolder holder, Post post) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(NewsFeedStrings.LIKES_CAP).child(post.getPostid())
                .child(firebaseUser.getUid());

        //If current use has not already liked current post
        if (holder.likeImageView.getTag().equals(NewsFeedStrings.LIKE)) {
            //Set user to like the post and add notification
            reference.setValue(true);
            addNotification();
        }
        //If already liked then unlike
        else {
            reference.removeValue();
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //Instance Variables
        public ImageView ownerProfileImageView, postImageView, likeImageView, commentImageView;
        public TextView usernameTextView, likesTextView, ownerTextView, descriptionTextView, commentsTextView;
        public ProgressBar progressBar;

        //Constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Initialize views
            ownerProfileImageView = itemView.findViewById(R.id.post_profile_image);
            postImageView = itemView.findViewById(R.id.image_post);
            likeImageView = itemView.findViewById(R.id.like_button);
            commentImageView = itemView.findViewById(R.id.comment_button);
            usernameTextView = itemView.findViewById(R.id.post_username);
            likesTextView = itemView.findViewById(R.id.likes);
            ownerTextView = itemView.findViewById(R.id.publisher);
            descriptionTextView = itemView.findViewById(R.id.post_description);
            commentsTextView = itemView.findViewById(R.id.comments);
            progressBar = itemView.findViewById(R.id.post_item_progress_bar);
        }
    }

    //This method retrieves all comments on current post and sets text to display number of comments
    private void loadCommentsOnPost(ViewHolder viewHolder, String postId) {

        //DatabaseReference to comments on current post
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(NewsFeedStrings.COMMENTS_CAP).child(postId);

        //ValueEventListener for DatabaseReference to update Comment textview
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                long childrenCount = snapshot.getChildrenCount();
                //If there are no comments, else if there is 1 comment, else if there are more than 1 comments -> displays a different text for each scenario
                if (childrenCount < 1) {
                    viewHolder.commentsTextView.setText("Be first to comment!");

                } else if(childrenCount==1) {
                    viewHolder.commentsTextView.setText("View " + snapshot.getChildrenCount() + " comment");
                }
                else {
                    viewHolder.commentsTextView.setText("View all " + snapshot.getChildrenCount() + " comments");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //This method checks if current user has already liked the current post
    private void checkIfLikedAndGetNumOfLikes(String postId, ViewHolder holder) {

        //DatabaseReference to likes on current post
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(NewsFeedStrings.LIKES_CAP)
                .child(postId);

        //OnClickListener for DatabaseReference
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Set number of likes to TextView
                holder.likesTextView.setText(snapshot.getChildrenCount() + StringsRepository.SPACE_LIKES);

                //If current user liked the post
                if (snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()) {
                    showPostLiked(holder);
                } else {
                    showPostNotLiked(holder);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    //Display post showing current user has not liked it
    private void showPostNotLiked(ViewHolder holder) {
        holder.likeImageView.setTag(NewsFeedStrings.LIKE);
        holder.likeImageView.setImageResource(R.drawable.ic_like);
    }


    //This method adds a notification
    private void addNotification() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StringsRepository.NOTIFICATIONS_CAP).child(userId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(StringsRepository.USER_ID, firebaseUser.getUid());
        hashMap.put(StringsRepository.IS_POST, true);
        hashMap.put(StringsRepository.TEXT, StringsRepository.LIKED_POST_NOTIFICATION_MESSAGE);
        hashMap.put(StringsRepository.POST_ID, postId);

        reference.push().setValue(hashMap);
    }


    //This method retrieves the owner information of the current post
    private void postOwnerDetails(ImageView image_profile, TextView username, TextView owner, String userid) {

        //DatabaseReference of user info
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StringsRepository.USERS_CAP).child(userid);


        //ValueEventListener for DatabaseReference
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(context).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                owner.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //This method shows that the current user has liked the current post already
    private void showPostLiked(ViewHolder holder) {
        holder.likeImageView.setImageResource(R.drawable.ic_liked);
        holder.likeImageView.setTag(NewsFeedStrings.LIKED);
    }
}
