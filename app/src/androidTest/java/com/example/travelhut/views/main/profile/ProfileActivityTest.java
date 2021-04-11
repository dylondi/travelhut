package com.example.travelhut.views.main.profile;

import android.app.Activity;
import android.app.Instrumentation;

import androidx.test.rule.ActivityTestRule;

import com.example.travelhut.R;
import com.example.travelhut.views.main.newsfeed.newsfeed.AddStoryActivity;
import com.example.travelhut.views.main.profile.toolbar.CreatePostActivity;
import com.example.travelhut.views.main.profile.toolbar.Notification;
import com.example.travelhut.views.main.profile.toolbar.NotificationsActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class ProfileActivityTest {


    @Rule
    public ActivityTestRule<ProfileActivity> profileActivityActivityTestRule = new ActivityTestRule<>(ProfileActivity.class);

    private ProfileActivity profileActivity = null;

    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(AccountSettingsActivity.class.getName(), null, false);
    Instrumentation.ActivityMonitor monitorPost = getInstrumentation().addMonitor(CreatePostActivity.class.getName(), null, false);
    Instrumentation.ActivityMonitor monitorNotifications = getInstrumentation().addMonitor(NotificationsActivity.class.getName(), null, false);

    @Before
    public void setUp() throws Exception {

        profileActivity = profileActivityActivityTestRule.getActivity();
    }

    @Test
    public void testLaunchOfAccountSettingsActivity(){
         assertNotNull(profileActivity.findViewById(R.id.overflow_menu));

        onView(withId(R.id.overflow_menu)).perform(click());

        Activity accountSettingsActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 5000);

        assertNotNull(accountSettingsActivity);

        accountSettingsActivity.finish();
    }



    @Test
    public void testLaunchOfAddStoryActivity(){
        assertNotNull(profileActivity.findViewById(R.id.add_post));

        onView(withId(R.id.add_post)).perform(click());

        Activity addPostActivity = getInstrumentation().waitForMonitorWithTimeout(monitorPost, 5000);

        assertNotNull(addPostActivity);

        addPostActivity.finish();
    }

    @Test
    public void testLaunchOfNotificationsActivity(){
        assertNotNull(profileActivity.findViewById(R.id.notifications));

        onView(withId(R.id.notifications)).perform(click());

        Activity notificationsActivity = getInstrumentation().waitForMonitorWithTimeout(monitorNotifications, 5000);

        assertNotNull(notificationsActivity);

        notificationsActivity.finish();
    }


    @After
    public void tearDown() throws Exception {
        profileActivity = null;
    }
}