package com.example.travelhut.views.main.newsfeed;

import android.app.Activity;
import android.app.Instrumentation;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.example.travelhut.R;
import com.example.travelhut.views.main.newsfeed.newsfeed.AddStoryActivity;
import com.example.travelhut.views.main.profile.AccountSettingsActivity;
import com.example.travelhut.views.main.profile.toolbar.EditProfileActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class NewsFeedActivityTest {

    @Rule
    public ActivityTestRule<NewsFeedActivity> newsFeedActivityActivityTestRule = new ActivityTestRule<>(NewsFeedActivity.class);

    private NewsFeedActivity newsFeedActivity = null;

    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(AddStoryActivity.class.getName(), null, false);


    @Before
    public void setUp() throws Exception {
        newsFeedActivity = newsFeedActivityActivityTestRule.getActivity();

    }


    //not finished
    @Test
    public void testLaunchUserSearch(){
        assertNotNull(newsFeedActivity.findViewById(R.id.search_icon));
        assertNull(onView(withId(R.id.recycler_view)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))));
        onView(withId(R.id.search_icon)).perform(click());


//        Activity addStoryActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 5000);
//
//        assertNotNull(addStoryActivity);
//
//        addStoryActivity.finish();
    }


    @After
    public void tearDown() throws Exception {
    }
}