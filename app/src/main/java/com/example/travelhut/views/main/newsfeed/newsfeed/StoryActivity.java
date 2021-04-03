package com.example.travelhut.views.main.newsfeed.newsfeed;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.travelhut.R;
import com.example.travelhut.model.utils.StringsRepository;
import com.example.travelhut.viewmodel.main.newsfeed.newsfeed.StoryActivityViewModel;
import com.example.travelhut.viewmodel.main.newsfeed.newsfeed.StoryActivityViewModelFactory;
import com.example.travelhut.model.objects.User;
import com.example.travelhut.model.objects.Story;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    //Instance Variables
    private int storyCounter = 0;
    private long pressTime = 0L;
    private long limit = 500L;
    private StoriesProgressView storiesProgressView;
    private ImageView storyImage, storyProfileImage, deleteStory;
    private TextView storyUsername, storyViews;
    private LinearLayout viewsLayout;
    private List<String> images, storyIds;
    private String userId;
    private View reverse, skip;
    private StoryActivityViewModel storyActivityViewModel;

    //OnTouchListener to allow story time to pause while user touches screen
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        initViews();

        //Get userId from intent
        userId = getIntent().getStringExtra(StringsRepository.USER_ID);
        storyActivityViewModel = ViewModelProviders.of(this, new StoryActivityViewModelFactory(this.getApplication(), userId)).get(StoryActivityViewModel.class);


        //Hide views
        viewsLayout.setVisibility(View.GONE);
        deleteStory.setVisibility(View.GONE);

        //If the story is owned by current user
        if (userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            viewsLayout.setVisibility(View.VISIBLE);
            deleteStory.setVisibility(View.VISIBLE);
        }

        getStories();
        userInfo();

        //Set OnClickListener for reverse view (left half of screen) to go to previous story if one exists
        reverse.setOnClickListener(v -> storiesProgressView.reverse());

        //Set OnTouchListener for reverse view to allow user to pause timer by holding on the screen
        reverse.setOnTouchListener(onTouchListener);

        //Set OnClickListener for skip view (right half of screen) to go to next story if one exists
        skip.setOnClickListener(v -> storiesProgressView.skip());

        //Set OnTouchListener for skip view to allow user to pause timer by holding on the screen
        skip.setOnTouchListener(onTouchListener);

        //Set OnClickListener for deleteStory
        deleteStory.setOnClickListener(view -> {

            //Call ViewModel to delete current story
            storyActivityViewModel.deleteStory(storyIds.get(storyCounter));

            //Finish activity
            finish();
        });

    }

    private void initViews() {
        storiesProgressView = findViewById(R.id.stories);
        storyImage = findViewById(R.id.image);
        storyProfileImage = findViewById(R.id.story_photo);
        storyUsername = findViewById(R.id.story_username);
        viewsLayout = findViewById(R.id.r_seen);
        storyViews = findViewById(R.id.seen_number);
        deleteStory = findViewById(R.id.story_delete);
        reverse = findViewById(R.id.reverse);
        skip = findViewById(R.id.skip);
    }

    @Override
    public void onNext() {
        Glide.with(getApplicationContext()).load(images.get(++storyCounter)).into(storyImage);

        //Add a view to the story and update seen number on screen
        updateSeenNumber(true);
    }

    @Override
    public void onPrev() {
        if ((storyCounter - 1) < 0) return;
        Glide.with(getApplicationContext()).load(images.get(--storyCounter)).into(storyImage);

        //Update seen number on screen
        updateSeenNumber(false);
    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }

    private void getStories(){

        //Initialize Lists
        images = new ArrayList<>();
        storyIds = new ArrayList<>();

        //Get LiveData object from ViewModel
        LiveData<DataSnapshot> liveData = storyActivityViewModel.getStories();

        //Observe  LiveData object
        liveData.observe(this, dataSnapshot -> {

            //Clear Lists
            images.clear();
            storyIds.clear();

            //Iterate through stories from DatabaseReference
            for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                //Data from snapshot is converted to a Story object
                Story story = snapshot.getValue(Story.class);

                //Get current time
                long timecurrent = System.currentTimeMillis();

                //If story is active. ie if it has not been published for 24 hours already
                if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {

                    //Add imageUrl to images List and add storyId to storyIds List
                    images.add(story.getImageurl());
                    storyIds.add(story.getStoryid());
                }
            }


            if(storyIds.size()>0&&images.size()>0) {
                //Configure storiesProgressView -> num of stories, story duration time, set story listener and start story with the counter variable
                storiesProgressView.setStoriesCount(images.size());
                storiesProgressView.setStoryDuration(6000L);
                storiesProgressView.setStoriesListener(StoryActivity.this);
                storiesProgressView.startStories(storyCounter);

                //Load image from given story and load into storyImage View
                Glide.with(getApplicationContext()).load(images.get(storyCounter)).into(storyImage);

                //Add a view to the story and update seen number on screen
                updateSeenNumber(true);
            }

        });
    }

    //This method adds a view to the stories view's if the addView boolean is true, and then updates the seen number on screen
    private void updateSeenNumber(boolean addView) {

        if(addView) {
            //Add view to this story
            storyActivityViewModel.addView(storyIds.get(storyCounter));
        }

        //Get number of views ViewModel
        storyActivityViewModel.seenNumber(storyIds.get(storyCounter));
        LiveData<Long> liveDataSeenNumber = storyActivityViewModel.getSeenNumber();
        liveDataSeenNumber.observe(this, seenNumber -> {
            storyViews.setText(""+seenNumber);
        });
    }

    private void userInfo(){

        //Get LiveData object from StoryActivityViewModel
        LiveData<DataSnapshot> liveData = storyActivityViewModel.getStoriesUserInfo();

        //Observe this LiveData object
        liveData.observe(this, dataSnapshot -> {

                //Data from snapshot is converted to a User object
                User user = dataSnapshot.getValue(User.class);

                //Profile picture and username of story owner is set
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(storyProfileImage);
                storyUsername.setText(user.getUsername());
        });
    }
}