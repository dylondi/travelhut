package com.example.travelhut.viewmodel.main.newsfeed.newsfeed;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.travelhut.model.main.newsfeed.StoryActivityRepository;
import com.example.travelhut.model.main.newsfeed.StoryUserInfoRepository;
import com.google.firebase.database.DataSnapshot;

public class StoryActivityViewModel extends AndroidViewModel {

    private StoryActivityRepository storyActivityRepository;
    private StoryUserInfoRepository storyUserInfoRepository;

    public StoryActivityViewModel(@NonNull Application application, String storyId) {
        super(application);
        storyActivityRepository = new StoryActivityRepository(storyId, application);
        storyUserInfoRepository = new StoryUserInfoRepository(storyId);
    }


    public LiveData<DataSnapshot> getStories(){
        return storyActivityRepository;
    }
    public LiveData<DataSnapshot> getStoriesUserInfo(){
        return storyUserInfoRepository;
    }

    public void addView(String storyId){
        storyActivityRepository.addView(storyId);
    }

    public void seenNumber(String storyId){
        storyActivityRepository.seenNumber(storyId);
    }

    public LiveData<Long> getSeenNumber(){
        return storyActivityRepository.getSeenNumber();
    }
    public LiveData<Boolean> getStoryDeleted(){
        return storyActivityRepository.getStoryDeleted();
    }

    public void deleteStory(String storyId){
        storyActivityRepository.deleteStory(storyId);
    }
}
