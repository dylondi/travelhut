package com.example.travelhut.views.main.profile.toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.travelhut.R;
import com.example.travelhut.views.main.newsfeed.newsfeed.PostAdapter;
import com.example.travelhut.views.main.newsfeed.newsfeed.utils.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class SinglePostFragment extends Fragment {


    String postid;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> posts;
    private ImageView backArrow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_single_post, container, false);

        backArrow = view.findViewById(R.id.single_post_back_arrow);

        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);

        postid = preferences.getString("postid", "none");


        recyclerView = view.findViewById(R.id.single_post_fragment_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        posts = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), posts);
        recyclerView.setAdapter(postAdapter);


        readPost();


        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.notifications_frame_layout, new NotificationsFragment()).commit();
            }
        });


        return view;
    }

    private void readPost() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                Post post = snapshot.getValue(Post.class);
                posts.add(post);

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}