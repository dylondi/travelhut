package com.example.travelhut.views.main.profile;

import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import com.example.travelhut.R;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

public class ProfileActivityTest {


//    @Rule
//    public ActivityTestRule<HomeActivity> homeScreen = new ActivityTestRule<>(HomeActivity.class);
//
//    /* Test to see if a user logs in successfully that he is brought to main activity screen */
//    @Test
//    public void toMaps() throws InterruptedException {
//        // Press Maps button
//        Intents.init();
//        try{
//            onView(withId(R.id.toMaps)).perform(click());
//            //Wait for intent to launch *** Removing the sleep will look for an intent matcher instantly and throw null
//            sleep(2000);
//            intended(hasComponent(MapsActivity.class.getName()));
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally{
//            Intents.release(); // Release main activity so test can finish
//
//        }
//    }
//}