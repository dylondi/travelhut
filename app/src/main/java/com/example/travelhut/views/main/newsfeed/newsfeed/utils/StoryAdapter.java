package com.example.travelhut.views.main.newsfeed.newsfeed.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travelhut.R;
import com.example.travelhut.model.utils.StringsRepository;
import com.example.travelhut.model.objects.User;
import com.example.travelhut.model.objects.Story;
import com.example.travelhut.views.main.newsfeed.newsfeed.StoryActivity;
import com.example.travelhut.views.main.newsfeed.newsfeed.UploadStoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {

    //Instance Variables
    private Context mContext;
    private List<Story> stories;

    //Constructor
    public StoryAdapter(Context mContext, List<Story> stories) {
        this.mContext = mContext;
        this.stories = stories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //If current user has no stories
        View view;
        if (viewType == 0) {

            //Initialize view to be add_story_item
            view = LayoutInflater.from(mContext).inflate(R.layout.add_story_view, parent, false);
        } else {

            //Initialize view to be story_item
            view = LayoutInflater.from(mContext).inflate(R.layout.story_view, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        //Current story
        final Story story = stories.get(position);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //Get user info
        currentStoryUserData(holder, story.getUserid(), position);

        //If current story is not current user's story
        if (holder.getAdapterPosition() != 0) {
            checkIfSeenStory(holder, firebaseUser, story.getUserid());
        }

        //If current story is current user's story
        if (holder.getAdapterPosition() == 0) {
            myStory(holder.postStoryText, holder.storyPlus, firebaseUser, false);
        }


        //Set OnClickListener for itemView
        holder.itemView.setOnClickListener(v -> {

            //If story clicked is current user's story
            if (holder.getAdapterPosition() == 0) {
                myStory(holder.postStoryText, holder.storyImage, firebaseUser, true);
            } else {
                //Create Intent for StoryActivity, put storyId as String extra and start activity
                Intent intent = new Intent(mContext, StoryActivity.class);
                intent.putExtra(StringsRepository.USER_ID, story.getUserid());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //Instance Variables
        public ImageView storyImage, storyPlus, storyImageViewed;
        public TextView username, postStoryText;
        public FrameLayout storyFrame, storySeenFrame;

        //Constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            storyImage = itemView.findViewById(R.id.profile_image_story);
            storyPlus = itemView.findViewById(R.id.post_story_icon);
            storyImageViewed = itemView.findViewById(R.id.profile_image_story_viewed);
            username = itemView.findViewById(R.id.story_username);
            postStoryText = itemView.findViewById(R.id.add_story_text);
            storyFrame = itemView.findViewById(R.id.story_frame);
            storySeenFrame = itemView.findViewById(R.id.story_frame_seen);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }
        return 1;
    }



    //This method gets user info of current story to set image and username
    private void currentStoryUserData(ViewHolder viewHolder, String userId, final int pos) {

        //DatabaseReference for user of current story
        DatabaseReference authorUserDataRef = FirebaseDatabase.getInstance().getReference(StringsRepository.USERS_CAP).child(userId);

        //SingleValueEventListener for DatabaseReference
        authorUserDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Get User object
                User user = dataSnapshot.getValue(User.class);

                //Set profile image and username
                Glide.with(mContext).load(user.getImageurl()).into(viewHolder.storyImage);
                if (pos != 0) {
                    Glide.with(mContext).load(user.getImageurl()).into(viewHolder.storyImageViewed);
                    viewHolder.username.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //This method configures Views for current user's story
    private void myStory(final TextView textView, ImageView imageView, FirebaseUser firebaseUser, boolean wasStoryClicked) {

        //DatabaseReference of current user's stories
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StringsRepository.STORY_CAP)
                .child(firebaseUser.getUid());

        //SingleValueEventListener for DatabaseReference
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int count = 0;
                long timecurrent = System.currentTimeMillis();

                //Iterate through current user's stories
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    //Get Story object
                    Story story = dataSnapshot.getValue(Story.class);

                    //If story is active
                    if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                        count++;
                    }
                }

                //If user clicked on own story
                if (wasStoryClicked) {

                    //Declare Intent object
                    Intent intent;

                    //If user has any active stories
                    if (count > 0) {
                        //Initialize intent and put String extra with current user's firebase Id
                        intent = new Intent(mContext, StoryActivity.class);
                        intent.putExtra(StringsRepository.USER_ID, FirebaseAuth.getInstance().getCurrentUser().getUid());
                    } else {
                        //Initialize intent with AddStoryActivity
                        intent = new Intent(mContext, UploadStoryActivity.class);
                    }

                    //Start Activity with intent
                    mContext.startActivity(intent);

                }
                //Else if not clicked
                else {
                    //If user has any active stories
                    if (count > 0) {
                        textView.setText("My Story");
                        imageView.setVisibility(View.GONE);
                    } else {
                        textView.setText("Add Story");
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //This method checks if the current story has already been viewed, if so -> update views to indicate
    private void checkIfSeenStory(final ViewHolder viewHolder, FirebaseUser firebaseUser, String userId) {

        //DatabaseReference to current story
        DatabaseReference storyRef = FirebaseDatabase.getInstance().getReference(StringsRepository.STORY_CAP)
                .child(userId);

        //ValueEventListener for DatabaseReference
        storyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                boolean storyViewed = true;

                //Iterate through stories in dataSnapshot
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    //If current user has not viewed the current story and the story has not expired yet
                    if (!dataSnapshot.child(StringsRepository.VIEWS).child(firebaseUser.getUid()).exists()
                            && System.currentTimeMillis() < dataSnapshot.getValue(Story.class).getTimeend()) {
                        storyViewed = false;
                    }
                }

                //If story is not already viewed
                if (!storyViewed) {
                    storyNotSeenView(viewHolder);
                } else {
                    storySeenView(viewHolder);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void storySeenView(ViewHolder viewHolder) {
        //Make unseen view visible (light blue circle surrounding story)
        viewHolder.storyFrame.setVisibility(View.GONE);
        viewHolder.storySeenFrame.setVisibility(View.VISIBLE);
    }

    private void storyNotSeenView(ViewHolder viewHolder) {
        //Make unseen view visible (dark blue circle surrounding story)
        viewHolder.storyFrame.setVisibility(View.VISIBLE);
        viewHolder.storySeenFrame.setVisibility(View.GONE);
    }
}
