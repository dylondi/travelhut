package com.example.travelhut.views.authentication;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import com.example.travelhut.R;
import com.example.travelhut.views.main.newsfeed.NewsFeedActivity;
import com.example.travelhut.views.main.profile.AccountSettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static java.lang.Thread.sleep;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.*;

public class RegisterLoginActivityTest {


    @Rule
    public ActivityTestRule<RegisterLoginActivity> registerLoginActivityActivityTestRule = new ActivityTestRule<>(RegisterLoginActivity.class);

    private RegisterLoginActivity registerLoginActivity = null;


    @Before
    public void setUp() throws Exception {
        registerLoginActivity = registerLoginActivityActivityTestRule.getActivity();

    }


    @Test
    public void testSuccessfulLogin() throws InterruptedException {

        String email = "test@test.ie";
        String password = "Test12";

        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(0));


        //Input UserName -> Close Keyboard
        onView(withId(R.id.emailLogin)).perform(typeText(email),closeSoftKeyboard());
        //Input Password -> Close Keyboard
        onView(withId(R.id.passwordLogin)).perform(typeText(password),closeSoftKeyboard());
        //If sucessful should bring to main activity.
        Intents.init();
        try{
            onView(withId(R.id.loginBtn)).perform(click());
            //Wait for intent to launch *** Removing the sleep will look for an intent matcher instantly and throw null
            sleep(2000);
            intended(hasComponent(NewsFeedActivity.class.getName()));

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally{
            Intents.release(); // Release main activity so test can finish

        }
    }


    @Test
    public void testSuccessfulRegister() throws InterruptedException {

        String email = "testRegister@test.ie";
        String username = "testRegister";
        String password = "Test12";

        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(0));
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.delete();
            }
        });

        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(1));


        //Input UserName -> Close Keyboard
        onView(withId(R.id.usernameRegister)).perform(typeText(username),closeSoftKeyboard());
        //Input Email -> Close Keyboard
        onView(withId(R.id.emailRegister)).perform(typeText(email),closeSoftKeyboard());
        //Input Password -> Close Keyboard
        onView(withId(R.id.passwordRegister)).perform(typeText(password),closeSoftKeyboard());
        onView(withId(R.id.passwordTwoRegister)).perform(typeText(password),closeSoftKeyboard());
        onView(withId(R.id.registerBtn)).perform(click());


        Thread.sleep(1500);
        //Input UserName -> Close Keyboard
        onView(withId(R.id.emailLogin)).perform(typeText(email),closeSoftKeyboard());
        //Input Password -> Close Keyboard
        onView(withId(R.id.passwordLogin)).perform(typeText(password),closeSoftKeyboard());


        //If sucessful should bring to main activity.
        Intents.init();
        try{
            onView(withId(R.id.loginBtn)).perform(click());
            //Wait for intent to launch *** Removing the sleep will look for an intent matcher instantly and throw null
            sleep(2000);
            intended(hasComponent(NewsFeedActivity.class.getName()));

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally{
            Intents.release(); // Release main activity so test can finish

        }
    }


    @NonNull
    private static ViewAction selectTabAtPosition(final int position) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isDisplayed(), isAssignableFrom(TabLayout.class));
            }

            @Override
            public String getDescription() {
                return "with tab at index" + String.valueOf(position);
            }

            @Override
            public void perform(UiController uiController, View view) {
                if (view instanceof TabLayout) {
                    TabLayout tabLayout = (TabLayout) view;
                    TabLayout.Tab tab = tabLayout.getTabAt(position);

                    if (tab != null) {
                        tab.select();
                    }
                }
            }
        };
    }

    @After
    public void tearDown() throws Exception {
    }
}