package com.example.travelhut.views.main.profile.toolbar.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travelhut.R;
import com.example.travelhut.model.utils.StringsRepository;
import com.example.travelhut.model.objects.Notification;
import com.example.travelhut.model.objects.User;
import com.example.travelhut.model.objects.Post;
import com.example.travelhut.views.main.profile.toolbar.SinglePostFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    //Instance Variables
    private Context context;
    private List<Notification> notifications;

    //Constructor
    public NotificationsAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new NotificationsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        //Get current notification
        Notification currentNotification = notifications.get(position);

        getNotificationCreator(viewHolder.profileImage, viewHolder.notificationUsername, currentNotification.getUserid());

        viewHolder.notificationText.setText(currentNotification.getText());

        //If notification is post
        if (currentNotification.isIspost()) {

            //Show and get post image
            viewHolder.postImage.setVisibility(View.VISIBLE);
            getPostRelatedToNotification(viewHolder.postImage, currentNotification.getPostid());
        } else {
            //Set post image to be invisible
            viewHolder.postImage.setVisibility(View.GONE);
        }

        viewHolder.postImage.setOnClickListener(v -> {
            SharedPreferences.Editor editor = context.getSharedPreferences(StringsRepository.PREFS, Context.MODE_PRIVATE).edit();
            editor.putString("postid", currentNotification.getPostid());
            editor.apply();

            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.notifications_frame_layout, new SinglePostFragment()).commit();
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView notificationUsername, notificationText;
        public ImageView profileImage, postImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            notificationUsername = itemView.findViewById(R.id.notification_username);
            notificationText = itemView.findViewById(R.id.notification_comment);
            profileImage = itemView.findViewById(R.id.notification_profile_image);
            postImage = itemView.findViewById(R.id.notification_post_image);

        }
    }

    private void getNotificationCreator(ImageView creatorProfileImage, TextView creatorUsername, String authorId) {

        //DatabaseReference for publisher user's info
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(StringsRepository.USERS_CAP).child(authorId);

        //ValueEventListener for DatabaseReference
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Get User object from snapshot
                User user = snapshot.getValue(User.class);

                //Set user image and username
                Glide.with(context).load(user.getImageurl()).into(creatorProfileImage);
                creatorUsername.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getPostRelatedToNotification(ImageView imagePost, String postId) {

        //DatabaseReference to post
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference(StringsRepository.POSTS_CAP).child(postId);

        //ValueEventListener for DatabaseReference
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Get Post object from snapshot
                Post post = snapshot.getValue(Post.class);

                //Set post image
                Glide.with(context).load(post.getPostimage()).into(imagePost);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
