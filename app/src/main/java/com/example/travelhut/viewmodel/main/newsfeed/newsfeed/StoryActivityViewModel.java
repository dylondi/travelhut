package com.example.travelhut.viewmodel.main.newsfeed.newsfeed;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.travelhut.model.main.newsfeed.StoryActivityRepository;
import com.example.travelhut.model.main.newsfeed.StoryUserInfoRepository;
import com.google.firebase.database.DataSnapshot;

public class StoryActivityViewModel extends AndroidViewModel {

    //Instance Variables
    private StoryActivityRepository storyActivityRepository;
    private StoryUserInfoRepository storyUserInfoRepository;

    //Constructor
    public StoryActivityViewModel(@NonNull Application application, String storyId) {
        super(application);
        storyActivityRepository = new StoryActivityRepository(storyId, application);
        storyUserInfoRepository = new StoryUserInfoRepository(storyId);
    }

    //This method returns LiveData object from storyActivityRepository
    public LiveData<DataSnapshot> getStories(){
        return storyActivityRepository;
    }

    //This method returns LiveData object from storyUserInfoRepository
    public LiveData<DataSnapshot> getStoriesUserInfo(){
        return storyUserInfoRepository;
    }

    //This method return LiveData object containing seen number for given story
    public LiveData<Long> getSeenNumber(){
        return storyActivityRepository.getSeenNumber();
    }

    //This method notifies storyActivityRepository to add a view to current story
    public void addView(String storyId){
        storyActivityRepository.addView(storyId);
    }

    //This method notifies storyActivityRepository to increase total number of views on given story
    public void seenNumber(String storyId){
        storyActivityRepository.seenNumber(storyId);
    }

    //This method notifies storyActivityRepository to delete a story
    public void deleteStory(String storyId){
        storyActivityRepository.deleteStory(storyId);
    }
}
