package com.example.travelhut.views.main.trips;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.travelhut.R;
import com.example.travelhut.views.main.planner.trip_fragments.CurrentTripsFragment;
import com.example.travelhut.views.main.planner.trip_fragments.FutureTripsFragment;
import com.example.travelhut.views.main.planner.trip_fragments.PreviousTripsFragment;

public class TripsSectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private final Context mContext;

    public TripsSectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = new PreviousTripsFragment();
                break;
            case 1:
                fragment = new CurrentTripsFragment();
                break;
            case 2:
                fragment = new FutureTripsFragment();
                break;

        }
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 3;
    }
}