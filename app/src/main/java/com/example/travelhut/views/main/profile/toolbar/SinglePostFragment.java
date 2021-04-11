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
import com.example.travelhut.model.utils.StringsRepository;
import com.example.travelhut.viewmodel.main.profile.toolbar.SinglePostFragmentViewModel;
import com.example.travelhut.viewmodel.main.profile.toolbar.SinglePostFragmentViewModelFactory;
import com.example.travelhut.views.main.newsfeed.newsfeed.utils.PostsAdapter;
import com.example.travelhut.model.objects.Post;
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        //Initialize views
        backArrow = view.findViewById(R.id.single_post_back_arrow);
        recyclerView = view.findViewById(R.id.single_post_fragment_recycler_view);

        //Get postid from SharedPreferences
        SharedPreferences preferences = getContext().getSharedPreferences(StringsRepository.PREFS, Context.MODE_PRIVATE);
        postid = preferences.getString(StringsRepository.POST_ID, "none");

        singlePostFragmentViewModel = ViewModelProviders.of(this, new SinglePostFragmentViewModelFactory(getActivity().getApplication(), postid)).get(SinglePostFragmentViewModel.class);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        posts = new ArrayList<>();
        postsAdapter = new PostsAdapter(getContext(), posts);
        recyclerView.setAdapter(postsAdapter);

        readPost();

        backArrow.setOnClickListener(v -> ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.notifications_frame_layout, new NotificationsFragment()).commit());


        return view;
    }

    //Get LiveData object and observe to receive post object
    private void readPost() {

        LiveData<DataSnapshot> liveData = singlePostFragmentViewModel.getPostLiveData();

        liveData.observe(getViewLifecycleOwner(), databaseReference -> {
            posts.clear();
            Post post = databaseReference.getValue(Post.class);
            posts.add(post);

            postsAdapter.notifyDataSetChanged();
        });
    }
}