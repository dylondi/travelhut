package com.example.travelhut.views.main.newsfeed.newsfeed;

import android.os.Handler;
import android.view.View;

public abstract class DoubleClickListener implements View.OnClickListener {

    //Instance Variables
    private static final long DOUBLE_CLICK_TIME_RANGE = 200;
    private boolean doubleClicked = false;
    long prevClick = 0;

    @Override
    public void onClick(final View v) {
        long currentClickTime = System.currentTimeMillis();
        if (currentClickTime - prevClick < DOUBLE_CLICK_TIME_RANGE) {
            doubleClicked = true;
            onDoubleClick();
        } else {
            doubleClicked = false;
            Handler handler = new Handler();
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