package com.example.travelhut.views.main.profile;

import android.app.Activity;
import android.app.Instrumentation;

import androidx.test.rule.ActivityTestRule;

import com.example.travelhut.R;
import com.example.travelhut.views.authentication.RegisterLoginActivity;
import com.example.travelhut.views.main.profile.toolbar.EditProfileActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

public class AccountSettingsActivityTest {

    @Rule
    public ActivityTestRule<AccountSettingsActivity> accountSettingsActivityActivityTestRule = new ActivityTestRule<>(AccountSettingsActivity.class);

    private AccountSettingsActivity accountSettingsActivity = null;

    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(EditProfileActivity.class.getName(), null, false);
    Instrumentation.ActivityMonitor monitorSignOut = getInstrumentation().addMonitor(RegisterLoginActivity.class.getName(), null, false);


    @Before
    public void setUp() throws Exception {
        accountSettingsActivity = accountSettingsActivityActivityTestRule.getActivity();

    }

    @Test
    public void testLaunchOfEditProfileActivity(){
        assertNotNull(accountSettingsActivity.findViewById(R.id.acc_settings_settings_btn));

        onView(withId(R.id.acc_settings_settings_btn)).perform(click());

        Activity editProfileActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 5000);

        assertNotNull(editProfileActivity);

        editProfileActivity.finish();
    }
    @Test
    public void testSignOut(){
        assertNotNull(accountSettingsActivity.findViewById(R.id.acc_settings_logout_btn));

        onView(withId(R.id.acc_settings_logout_btn)).perform(click());

        Activity registerLoginActivity = getInstrumentation().waitForMonitorWithTimeout(monitorSignOut, 5000);

        assertNotNull(registerLoginActivity);

        registerLoginActivity.finish();
    }






    @After
    public void tearDown() throws Exception {
        accountSettingsActivity = null;
    }
}