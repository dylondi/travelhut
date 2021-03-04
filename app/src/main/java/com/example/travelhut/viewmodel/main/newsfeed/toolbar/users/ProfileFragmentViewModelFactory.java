package com.example.travelhut.viewmodel.main.newsfeed.toolbar.users;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.travelhut.viewmodel.main.profile.toolbar.SinglePostFragmentViewModel;

public class ProfileFragmentViewModelFactory  implements ViewModelProvider.Factory{
        private Application mApplication;
        private String mProfileId;


        public ProfileFragmentViewModelFactory(Application application, String profileId) {
            mApplication = application;
            mProfileId = profileId;
        }


        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new ProfileFragmentViewModel(mApplication, mProfileId);
        }
}
