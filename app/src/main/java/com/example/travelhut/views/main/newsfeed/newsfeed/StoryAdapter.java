package com.example.travelhut.views.main.newsfeed.newsfeed;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.travelhut.R;
import com.example.travelhut.views.authentication.utils.User;
import com.example.travelhut.views.main.newsfeed.newsfeed.utils.Story;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder>{

    private Context mContext;
    private List<Story> stories;

    public StoryAdapter(Context mContext, List<Story> stories) {
        this.mContext = mContext;
        this.stories = stories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == 0) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.add_story_item, parent, false);
            return new StoryAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.story_item, parent, false);
            return new StoryAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final Story story = stories.get(position);
         userInfo(holder, story.getUserid(), position);


        if(holder.getAdapterPosition() != 0){
            seenStory(holder, story.getUserid());
        }
        if(holder.getAdapterPosition() == 0){
            myStory(holder.postStoryText, holder.storyPlus, false);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.getAdapterPosition() == 0){
                    myStory(holder.postStoryText, holder.storyImage, true);
                }else{
                    Intent intent = new Intent(mContext, StoryActivity.class);
                    intent.putExtra("userid", story.getUserid());
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView storyImage, storyPlus, storyImageViewed;
        public TextView username, postStoryText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            storyImage = itemView.findViewById(R.id.profile_image_story);
            storyPlus = itemView.findViewById(R.id.post_story_icon);
            storyImageViewed = itemView.findViewById(R.id.profile_image_story_viewed);
            username = itemView.findViewById(R.id.story_username);
            postStoryText = itemView.findViewById(R.id.add_story_text);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return 0;
        }
        return 1;
    }


    private void userInfo(final ViewHolder viewHolder, String userid, final int pos){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
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


    private void myStory(final TextView textView, ImageView imageView, boolean click){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                long timecurrent = System.currentTimeMillis();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Story story = dataSnapshot.getValue(Story.class);
                    if(timecurrent > story.getTimestart() && timecurrent < story.getTimeend()){
                        count++;
                    }
                }

                if(click){


                    if(count > 0){
//                        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
//                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "View Story",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(mContext, StoryActivity.class);
                                        intent.putExtra("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        mContext.startActivity(intent);
//                                        dialog.dismiss();
//                                    }
//                                });
//                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Story",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        Intent intent = new Intent(mContext, AddStoryActivity.class);
//                                        mContext.startActivity(intent);
//                                        dialog.dismiss();
//                                    }
//                                });
//                        alertDialog.show();
                    }
                    else{
                        Intent intent = new Intent(mContext, AddStoryActivity.class);
                        mContext.startActivity(intent);
                    }

                }
                else{
                    if(count > 0){
                        textView.setText("My Story");
                        imageView.setVisibility(View.GONE);
                    }else{
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


    private void seenStory(final ViewHolder viewHolder, String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                     if(!dataSnapshot.child("views")
                     .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                     .exists() && System.currentTimeMillis() < dataSnapshot.getValue(Story.class).getTimeend()){
                         i++;
                     }
                }

                if(i > 0){
                    viewHolder.storyImage.setVisibility(View.VISIBLE);
                    viewHolder.storyImageViewed.setVisibility(View.GONE);
                }else{
                    viewHolder.storyImage.setVisibility(View.GONE);
                    viewHolder.storyImageViewed.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
