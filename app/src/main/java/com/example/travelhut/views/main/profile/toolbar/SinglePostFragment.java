package com.example.travelhut.views.main.profile.toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.travelhut.R;
import com.example.travelhut.viewmodel.main.profile.toolbar.SinglePostFragmentViewModel;
import com.example.travelhut.viewmodel.main.profile.toolbar.SinglePostFragmentViewModelFactory;
import com.example.travelhut.views.main.newsfeed.newsfeed.PostsAdapter;
import com.example.travelhut.views.main.newsfeed.newsfeed.utils.Post;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;


public class SinglePostFragment extends Fragment {


    String postid;
    private RecyclerView recyclerView;
    private PostsAdapter postsAdapter;
    private List<Post> posts;
    private ImageView backArrow;
    private SinglePostFragmentViewModel singlePostFragmentViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_single_post, container, false);

        backArrow = view.findViewById(R.id.single_post_back_arrow);

        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);

        postid = preferences.getString("postid", "none");
        singlePostFragmentViewModel = ViewModelProviders.of(this, new SinglePostFragmentViewModelFactory(getActivity().getApplication(), postid)).get(SinglePostFragmentViewModel.class);



        recyclerView = view.findViewById(R.id.single_post_fragment_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        posts = new ArrayList<>();
        postsAdapter = new PostsAdapter(getContext(), posts);
        recyclerView.setAdapter(postsAdapter);


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


        LiveData<DataSnapshot> liveData = singlePostFragmentViewModel.getPostLiveData();

        liveData.observe(getViewLifecycleOwner(), databaseReference -> {
                posts.clear();
                Post post = databaseReference.getValue(Post.class);
                posts.add(post);

                postsAdapter.notifyDataSetChanged();
        });
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                posts.clear();
//                Post post = snapshot.getValue(Post.class);
//                posts.add(post);
//
//                postAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }
}