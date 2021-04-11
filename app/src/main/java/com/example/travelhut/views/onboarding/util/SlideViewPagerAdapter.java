package com.example.travelhut.views.onboarding.util;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.travelhut.R;
import com.example.travelhut.views.authentication.RegisterLoginActivity;
import com.example.travelhut.views.onboarding.OnboardingScreensActivity;

public class SlideViewPagerAdapter extends PagerAdapter {

    //Instance Variables
    private Context context;

    //Constructor
    public SlideViewPagerAdapter(Context context) {
        this.context = context;
    }

    //This method returns number of items in adapter
    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        //Get view from layoutInfalter
        View view = layoutInflater.inflate(R.layout.slide_screen, container, false);

        //Initialize views
        ImageView background = view.findViewById(R.id.onboarding_bg1);
        ImageView ind1 = view.findViewById(R.id.indicator_1);
        ImageView ind2 = view.findViewById(R.id.indicator_2);
        ImageView ind3 = view.findViewById(R.id.indicator_3);
        ImageView ind4 = view.findViewById(R.id.indicator_4);
        ImageView ind5 = view.findViewById(R.id.indicator_5);
        TextView text = view.findViewById(R.id.onboarding_text);
        ImageView nextArrow = view.findViewById(R.id.next);
        ImageView backArrow = view.findViewById(R.id.back);
        Button getStartedBtn = view.findViewById(R.id.getStartedBtn);

        //Set OnClickListener for getStartedBtn
        getStartedBtn.setOnClickListener(view1 -> {

            //Create intent to navigate to register/login pages
            Intent intent = new Intent(context, RegisterLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        //Set OnClickListener for nextArrow
        nextArrow.setOnClickListener(view12 -> OnboardingScreensActivity.viewPager.setCurrentItem(position+1));

        //Set OnClickListener for backArrow
        backArrow.setOnClickListener(view13 -> OnboardingScreensActivity.viewPager.setCurrentItem(position-1));

        //Switch statement to configure the correct data with the correct page of the onboarding screens
        switch (position){
            case 0:
                background.setImageResource(R.drawable.onboarding1);
                ind1.setImageResource(R.drawable.selected);
                ind2.setImageResource(R.drawable.unselected);
                ind3.setImageResource(R.drawable.unselected);
                ind4.setImageResource(R.drawable.unselected);
                ind5.setImageResource(R.drawable.unselected);

                text.setText("Search popular tourist locations worldwide.");
                backArrow.setVisibility(View.GONE);
                nextArrow.setVisibility(View.VISIBLE);
                break;
            case 1:
                background.setImageResource(R.drawable.onboarding2);
                ind1.setImageResource(R.drawable.unselected);
                ind2.setImageResource(R.drawable.selected);
                ind3.setImageResource(R.drawable.unselected);
                ind4.setImageResource(R.drawable.unselected);
                ind5.setImageResource(R.drawable.unselected);

                text.setText("View and edit past, current and future trips.");
                backArrow.setVisibility(View.VISIBLE);
                nextArrow.setVisibility(View.VISIBLE);
                break;
            case 2:
                background.setImageResource(R.drawable.onboarding3);
                ind1.setImageResource(R.drawable.unselected);
                ind2.setImageResource(R.drawable.unselected);
                ind3.setImageResource(R.drawable.selected);
                ind4.setImageResource(R.drawable.unselected);
                ind5.setImageResource(R.drawable.unselected);

                text.setText("Create and post about holidays and trips with friends and view posts from others.");
                backArrow.setVisibility(View.VISIBLE);
                nextArrow.setVisibility(View.VISIBLE);
                break;
            case 3:
                background.setImageResource(R.drawable.onboarding4);
                ind1.setImageResource(R.drawable.unselected);
                ind2.setImageResource(R.drawable.unselected);
                ind3.setImageResource(R.drawable.unselected);
                ind4.setImageResource(R.drawable.selected);
                ind5.setImageResource(R.drawable.unselected);

                text.setText("Create, edit and view a personal daily planner.");
                backArrow.setVisibility(View.VISIBLE);
                nextArrow.setVisibility(View.VISIBLE);
                break;
            case 4:
                background.setImageResource(R.drawable.onboarding5);
                ind1.setImageResource(R.drawable.unselected);
                ind2.setImageResource(R.drawable.unselected);
                ind3.setImageResource(R.drawable.unselected);
                ind4.setImageResource(R.drawable.unselected);
                ind5.setImageResource(R.drawable.selected);

                text.setText("View your user profile.");
                backArrow.setVisibility(View.VISIBLE);
                nextArrow.setVisibility(View.GONE);
                break;
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
