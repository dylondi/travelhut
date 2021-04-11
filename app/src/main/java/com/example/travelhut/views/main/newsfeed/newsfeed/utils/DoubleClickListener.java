package com.example.travelhut.views.main.newsfeed.newsfeed.utils;

import android.os.Handler;
import android.view.View;

public abstract class DoubleClickListener implements View.OnClickListener {

    //Instance Variables
    private static final long DOUBLE_CLICK_TIME_RANGE = 200;
    private boolean doubleClicked = false;
    long prevClick = 0;

    @Override
    public void onClick(final View v) {

        //Current time
        long currentClickTime = System.currentTimeMillis();

        //Check if two clicks were under 200 milli seconds between each other
        if (currentClickTime - prevClick < DOUBLE_CLICK_TIME_RANGE) {

            //set doubleClicked to true and call onDoubleClick()
            doubleClicked = true;
            onDoubleClick();
        } else {

            //set doubleClicked to false and call onSingleClick()
            doubleClicked = false;
            Handler handler = new Handler();

            //Post to main thread
            handler.postDelayed(() -> {

                if (!doubleClicked) {
                    onSingleClick();
                }
            }, 180);

        }
        prevClick = currentClickTime;
    }

    public abstract void onSingleClick();

    public abstract void onDoubleClick();
}