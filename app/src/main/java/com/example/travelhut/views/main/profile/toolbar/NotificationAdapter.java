package com.example.travelhut.views.main.profile.toolbar;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    //Instance Variables
    private Context mContext;
    private List<Notification> mNotifications;

    //Constructor
    public NotificationAdapter(Context mContext, List<Notification> mNotifications) {
        this.mContext = mContext;
        this.mNotifications = mNotifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //Get current notification
        final Notification notification = mNotifications.get(position);

        holder.text.setText(notification.getText());

        getUserInfo(holder.profileImage, holder.username, notification.getUserid());

        //If notification is post
        if (notification.isIspost()) {

            //Show and get post image
            holder.postImage.setVisibility(View.VISIBLE);
            getPostImage(holder.postImage, notification.getPostid());
        } else {
            //Set post image to be invisible
            holder.postImage.setVisibility(View.GONE);
        }

        holder.postImage.setOnClickListener(v -> {
            SharedPreferences.Editor editor = mContext.getSharedPreferences(StringsRepository.PREFS, Context.MODE_PRIVATE).edit();
            editor.putString("postid", notification.getPostid());
            editor.apply();

            ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.notifications_frame_layout, new SinglePostFragment()).commit();
        });
    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public ImageView profileImage, postImage;
        public TextView username, text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            profileImage = itemView.findViewById(R.id.notification_profile_image);
            postImage = itemView.findViewById(R.id.notification_post_image);
            username = itemView.findViewById(R.id.notification_username);
            text = itemView.findViewById(R.id.notification_comment);
        }
    }

    private void getUserInfo(ImageView imageView, TextView username, String publisherId) {

        //DatabaseReference for publisher user's info
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StringsRepository.USERS_CAP).child(publisherId);

        //ValueEventListener for DatabaseReference
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Get User object from snapshot
                User user = snapshot.getValue(User.class);

                //Set user image and username
                Glide.with(mContext).load(user.getImageurl()).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getPostImage(final ImageView imagePost, String postId) {

        //DatabaseReference to post
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StringsRepository.POSTS_CAP).child(postId);

        //ValueEventListener for DatabaseReference
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Get Post object from snapshot
                Post post = snapshot.getValue(Post.class);

                //Set post image
                Glide.with(mContext).load(post.getPostimage()).into(imagePost);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
