package com.example.travelhut.views.main.profile;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.travelhut.viewmodel.authentication.LoginViewModel;
import com.example.travelhut.viewmodel.main.profile.SignOutFragmentViewModel;
import com.example.travelhut.views.authentication.RegisterLoginActivity;
import com.example.travelhut.views.main.newsfeed.CameraFragment;
import com.example.travelhut.views.main.newsfeed.MessagesFragment;
import com.example.travelhut.views.main.newsfeed.SearchFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SectionsStateAdapter extends FragmentStateAdapter {


    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final HashMap<Fragment, Integer> mFragments = new HashMap<>();
    private final HashMap<String, Integer> mFragmentNumbers = new HashMap<>();
    private final HashMap<Integer, String> mFragmentNames = new HashMap<>();
    private SignOutFragmentViewModel signOutFragmentViewModel = new SignOutFragmentViewModel();
    private Context mContext;

    public SectionsStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Context mContext) {
        super(fragmentManager, lifecycle);
        this.mContext = mContext;
    }


//    public SectionsStateAdapter(@NonNull Fragment fragment) {
//        super(fragment);
//    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return new EditProfileFragment();
            default:
                return new SignOutFragment();
//
//
//                signOutFragmentViewModel.logout();
//
//        signOutFragmentViewModel.getLoggedOutMutableLiveData().observe(, new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean loggedOut) {
//                if(loggedOut){
//                    Intent intent = new Intent(mContext, RegisterLoginActivity.class);
//                    mContext.startActivity(intent);
//                }
//            }
//        });

        }
    }

    @Override
    public int getItemCount() {
        return mFragmentList.size();
    }


    public void addFragment(Fragment fragment, String fragmentName){
        mFragmentList.add(fragment);
        mFragments.put(fragment, mFragmentList.size()-1);
        mFragmentNumbers.put(fragmentName, mFragmentList.size()-1);
        mFragmentNames.put(mFragmentList.size()-1, fragmentName);
    }

    public Integer getFragmentNumber(String fragmentName){
        if(mFragmentNumbers.containsKey(fragmentName)){
            return mFragmentNumbers.get(fragmentName);
        }else{
            return null;
        }
    }

    public Integer getFragmentNumber(Fragment fragment){
        if(mFragmentNumbers.containsKey(fragment)){
            return mFragmentNumbers.get(fragment);
        }else{
            return null;
        }
    }


    public String getFragmentName(Integer fragmentNumber){
        if(mFragmentNames.containsKey(fragmentNumber)){
            return mFragmentNames.get(fragmentNumber);
        }else{
            return null;
        }
    }
}
