package com.example.travelhut.views.onboarding;

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
    Context ctx;

    public SlideViewPagerAdapter(Context ctx) {
        this.ctx = ctx;
    }

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
        LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_screen, container, false);

        ImageView background = view.findViewById(R.id.onboarding_bg1);
        ImageView ind1 = view.findViewById(R.id.indicator_1);
        ImageView ind2 = view.findViewById(R.id.indicator_2);
        ImageView ind3 = view.findViewById(R.id.indicator_3);
        ImageView ind4 = view.findViewById(R.id.indicator_4);
        ImageView ind5 = view.findViewById(R.id.indicator_5);

        TextView text = view.findViewById(R.id.onboarding_text);

        ImageView next = view.findViewById(R.id.next);
        ImageView back = view.findViewById(R.id.back);
        Button getStartedBtn = view.findViewById(R.id.getStartedBtn);
        getStartedBtn.setOnClickListener(view1 -> {
            Intent intent = new Intent(ctx, RegisterLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
        });

        next.setOnClickListener(view12 -> OnboardingScreensActivity.viewPager.setCurrentItem(position+1));

        back.setOnClickListener(view13 -> OnboardingScreensActivity.viewPager.setCurrentItem(position-1));


        switch (position){
            case 0:
                background.setImageResource(R.drawable.onboarding1);
                ind1.setImageResource(R.drawable.selected);
                ind2.setImageResource(R.drawable.unselected);
                ind3.setImageResource(R.drawable.unselected);
                ind4.setImageResource(R.drawable.unselected);
                ind5.setImageResource(R.drawable.unselected);

                text.setText("Search popular tourist locations worldwide.");
                back.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                break;
            case 1:
                background.setImageResource(R.drawable.onboarding2);
                ind1.setImageResource(R.drawable.unselected);
                ind2.setImageResource(R.drawable.selected);
                ind3.setImageResource(R.drawable.unselected);
                ind4.setImageResource(R.drawable.unselected);
                ind5.setImageResource(R.drawable.unselected);

                text.setText("View and edit past, current and future trips.");
                back.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
                break;
            case 2:
                background.setImageResource(R.drawable.onboarding3);
                ind1.setImageResource(R.drawable.unselected);
                ind2.setImageResource(R.drawable.unselected);
                ind3.setImageResource(R.drawable.selected);
                ind4.setImageResource(R.drawable.unselected);
                ind5.setImageResource(R.drawable.unselected);

                text.setText("Create and post about holidays and trips with friends and view posts from others.");
                back.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
                break;
            case 3:
                background.setImageResource(R.drawable.onboarding4);
                ind1.setImageResource(R.drawable.unselected);
                ind2.setImageResource(R.drawable.unselected);
                ind3.setImageResource(R.drawable.unselected);
                ind4.setImageResource(R.drawable.selected);
                ind5.setImageResource(R.drawable.unselected);

                text.setText("Create, edit and view a personal daily planner.");
                back.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
                break;
            case 4:
                background.setImageResource(R.drawable.onboarding5);
                ind1.setImageResource(R.drawable.unselected);
                ind2.setImageResource(R.drawable.unselected);
                ind3.setImageResource(R.drawable.unselected);
                ind4.setImageResource(R.drawable.unselected);
                ind5.setImageResource(R.drawable.selected);

                text.setText("View your user profile.");
                back.setVisibility(View.VISIBLE);
                next.setVisibility(View.GONE);
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
