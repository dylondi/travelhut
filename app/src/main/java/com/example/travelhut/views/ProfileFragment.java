package com.example.travelhut.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.travelhut.R;
import com.example.travelhut.model.StringsRepository;
import com.example.travelhut.views.authentication.utils.User;
import com.example.travelhut.views.main.newsfeed.NewsFeedFragment;
import com.example.travelhut.views.main.newsfeed.newsfeed.PostAdapter;
import com.example.travelhut.views.main.newsfeed.newsfeed.utils.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class ProfileFragment extends Fragment {

    ImageView profileImage, backArrow;
    TextView followers, following, username, bio, url, headerName;
    Button followButton;

    FirebaseUser firebaseUser;
    String profileid;

    private PostAdapter postAdapter;
    private List<Post> postList;
    public RecyclerView recyclerView;
    private NewsFeedFragment newsFeedFragment;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        profileImage = view.findViewById(R.id.profile_image_fragment);
        followers = view.findViewById(R.id.num_followers_profile_fragment);
        following = view.findViewById(R.id.num_following_profile_fragment);
        username = view.findViewById(R.id.display_name_profile_fragment);
        bio = view.findViewById(R.id.bio_profile_fragment);
        url = view.findViewById(R.id.url_profile_fragment);
        followButton = view.findViewById(R.id.follow_button_profile_fragment);
        recyclerView = view.findViewById(R.id.profile_fragment_recycler_view);
        headerName = view.findViewById(R.id.profile_fragment_name);
        backArrow = view.findViewById(R.id.profile_fragment_back_arrow);
//        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);

        userInfo();
        getFollowers();

        getProfileFeed();
        if(profileid.equals(firebaseUser.getUid())){
            followButton.setText("edit profile");
        }else{
            checkFollow();
        }

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(followButton.getText().toString().equals("follow")){
                    FirebaseDatabase.getInstance().getReference()
                            .child(StringsRepository.FOLLOW_CAP)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(StringsRepository.FOLLOWING)
                            .child(profileid).setValue(true);

                    //updates db -> otherUser -> followers -> currentUser -> true
                    FirebaseDatabase.getInstance().getReference()
                            .child(StringsRepository.FOLLOW_CAP)
                            .child(profileid)
                            .child(StringsRepository.FOLLOWERS)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

                    addNotification();
                }else if(followButton.getText().toString().equals("following")){
                    FirebaseDatabase.getInstance().getReference()
                            .child(StringsRepository.FOLLOW_CAP)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(StringsRepository.FOLLOWING)
                            .child(profileid).removeValue();

                    //updates db -> otherUser -> followers -> currentUser -> removes value
                    FirebaseDatabase.getInstance().getReference()
                            .child(StringsRepository.FOLLOW_CAP)
                            .child(profileid)
                            .child(StringsRepository.FOLLOWERS)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                }
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newsFeedFragment = new NewsFeedFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, (Fragment) newsFeedFragment).commit();
            }
        });


        return view;
    }


    private void addNotification(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "started following you");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);

        reference.push().setValue(hashMap);
    }


    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(getContext() == null){
                    return;
                }

                User user = snapshot.getValue(User.class);
                headerName.setText(user.getUsername());
                Glide.with(getContext()).load(user.getImageurl()).into(profileImage);
                username.setText(user.getDisplayname());
                url.setText(user.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow")
                .child(firebaseUser.getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(profileid).exists()){
                    followButton.setText("following");
                }else{
                    followButton.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow")
                .child(profileid).child("followers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference referenceTwo = FirebaseDatabase.getInstance().getReference()
                .child("Follow")
                .child(profileid).child("following");

        referenceTwo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getNumOfPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if(post.getPublisher().equals(profileid)){
                        i++;
                    }
                }

               // posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getProfileFeed(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)){
                        postList.add(post);
                    }
                }
                //Collections.reverse(postList);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}