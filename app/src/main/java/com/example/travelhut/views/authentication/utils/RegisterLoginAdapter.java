package com.example.travelhut.views.authentication.utils;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.travelhut.views.authentication.LoginFragment;
import com.example.travelhut.views.authentication.RegisterFragment;


//Adapter class to allow viewpager to switch between register and login fragments
public class RegisterLoginAdapter extends FragmentPagerAdapter {

    private Context context;
    int totalTabs;

    public RegisterLoginAdapter(FragmentManager fm, Context context, int totalTabs){
        super(fm);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    @Override
    public int getCount() {
        return totalTabs;
    }

    //this method gets the fragment chosen by user on authentication... register|login
    public Fragment getItem(int position){
        switch(position){
            case 0:
                //create and return LoginFragment
                LoginFragment loginFragment = new LoginFragment();
                return loginFragment;
            case 1:
                //create and return RegisterFragment
                RegisterFragment registerFragment = new RegisterFragment();
                return registerFragment;
            default:
                return null;
        }
    }
}
