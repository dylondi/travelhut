package com.example.travelhut.views.authentication.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.travelhut.views.authentication.LoginFragment;
import com.example.travelhut.views.authentication.RegisterFragment;


//Adapter class to allow viewpager to switch between register and login fragments
public class RegisterLoginAdapter extends FragmentPagerAdapter {

    //Instance Variable
    private int totalTabs;

    //Constructor
    public RegisterLoginAdapter(FragmentManager fm, int totalTabs) {
        super(fm);
        this.totalTabs = totalTabs;
    }

    @Override
    public int getCount() {
        return totalTabs;
    }

    //This method creates a new Fragment for register|login ViewPager based on the inputted position value
    public Fragment getItem(int position) {
        switch (position) {
            //First position in ViewPager
            case 0:
                //Return new LoginFragment
                return new LoginFragment();
            //Second position in ViewPager
            case 1:
                //Return new RegisterFragment
                return new RegisterFragment();
            default:
                return null;
        }
    }
}
