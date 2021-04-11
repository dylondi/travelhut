package com.example.travelhut.views.authentication;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import com.example.travelhut.R;
import com.example.travelhut.views.main.newsfeed.NewsFeedActivity;
import com.example.travelhut.views.main.newsfeed.newsfeed.CommentActivity;
import com.example.travelhut.views.main.profile.ProfileActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static java.lang.Thread.sleep;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.StringContains.containsString;

public class RegisterLoginActivityTest {

    @Test
    public void testSuccessfulLogin() throws InterruptedException {

        //String auth values
        String email = "testRegister@test.ie";
        String password = "Test12";

        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(0));


        //Input UserName -> Close Keyboard
        onView(withId(R.id.emailLogin)).perform(typeText(email), closeSoftKeyboard());
        //Input Password -> Close Keyboard
        onView(withId(R.id.passwordLogin)).perform(typeText(password), closeSoftKeyboard());
        //If successful should bring to NewsFeedActivity.
        Intents.init();
        try {
            onView(withId(R.id.loginBtn)).perform(click());
            //Wait for intent to launch *** Removing the sleep will look for an intent matcher instantly and throw null
            sleep(2000);
            intended(hasComponent(NewsFeedActivity.class.getName()));

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            Intents.release(); // Release main activity so test can finish

        }
    }


    @Test
    public void testSuccessfulRegisterAndLogin() throws InterruptedException {

        //String auth values
        String email = "testRegister@test.ie";
        String username = "testRegister";
        String password = "Test12";

        //Delete user if already exists
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                user.delete();
            }
        });

        //Go to register fragment
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(1));


        //Input UserName -> Close Keyboard
        onView(withId(R.id.usernameRegister)).perform(typeText(username), closeSoftKeyboard());
        //Input Email -> Close Keyboard
        onView(withId(R.id.emailRegister)).perform(typeText(email), closeSoftKeyboard());
        //Input Password -> Close Keyboard
        onView(withId(R.id.passwordRegister)).perform(typeText(password), closeSoftKeyboard());
        //Re-enter Password -> Close Keyboard
        onView(withId(R.id.passwordTwoRegister)).perform(typeText(password), closeSoftKeyboard());
        //Click register button
        onView(withId(R.id.registerBtn)).perform(click());


        Thread.sleep(1500);
        //Input UserName -> Close Keyboard
        onView(withId(R.id.emailLogin)).perform(typeText(email), closeSoftKeyboard());
        //Input Password -> Close Keyboard
        onView(withId(R.id.passwordLogin)).perform(typeText(password), closeSoftKeyboard());


        //If successful should bring to main activity.
        Intents.init();
        try {
            onView(withId(R.id.loginBtn)).perform(click());
            //Wait for intent to launch *** Removing the sleep will look for an intent matcher instantly and throw null
            sleep(2000);
            intended(hasComponent(NewsFeedActivity.class.getName()));

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            Intents.release(); // Release main activity so test can finish

        }
    }


    @Test
    public void testAuthSearchUserFollowComment() throws InterruptedException {

        //String auth values
        String email = "testRegister@test.ie";
        String username = "testRegister";
        String password = "Test12";

        //Check if user already exists in database, if so -> delete user
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                //Firebase User and DatabaseReference of follows related to that user
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference follows = FirebaseDatabase.getInstance().getReference("Follow").child(user.getUid());

                //Delete user and remove follows
                user.delete();
                follows.removeValue();
            }
        });

        //Go to Register Fragment of ViewPager
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(1));

        //Input UserName -> Close Keyboard
        onView(withId(R.id.usernameRegister)).perform(typeText(username), closeSoftKeyboard());
        //Input Email -> Close Keyboard
        onView(withId(R.id.emailRegister)).perform(typeText(email), closeSoftKeyboard());
        //Input Password -> Close Keyboard
        onView(withId(R.id.passwordRegister)).perform(typeText(password), closeSoftKeyboard());
        //Re-enter Password -> Close Keyboard
        onView(withId(R.id.passwordTwoRegister)).perform(typeText(password), closeSoftKeyboard());
        //Click register button
        onView(withId(R.id.registerBtn)).perform(click());

        //Wait 1.5 seconds before proceeding to allow for registration with Firebase
        Thread.sleep(1500);

        //Attempt to login
        //Input UserName -> Close Keyboard
        onView(withId(R.id.emailLogin)).perform(typeText(email), closeSoftKeyboard());
        //Input Password -> Close Keyboard
        onView(withId(R.id.passwordLogin)).perform(typeText(password), closeSoftKeyboard());


        //Initialize intents
        Intents.init();
        try {
            //Click login button
            onView(withId(R.id.loginBtn)).perform(click());

            //Wait for intent to launch, if sleep is removed will look for intent matcher immediately and return null
            sleep(2000);

            //Check the intended class is active
            intended(hasComponent(NewsFeedActivity.class.getName()));

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            Intents.release(); // Release News Feed Activity

        }

        //Perform click on search icon
        onView(withId(R.id.search_icon)).perform(click());

        //Choose first user in search view and go to their profile
        onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //Click follow button on searched user's page
        onView(withId(R.id.follow_button_profile_fragment)).perform(click());

        //Perform a check to see if the follow button displays the text "following"
        onView(withId(R.id.follow_button_profile_fragment)).check(matches(withText(containsString("following"))));

        //Scroll to first item in posts RecyclerView
        onView(withId(R.id.profile_fragment_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, scrollTo()));

        //Initialize intents
        Intents.init();

        //Open comments on the first post in the RecyclerView
        onView(withId(R.id.profile_fragment_recycler_view)).perform(
                        RecyclerViewActions.actionOnItemAtPosition(
                                0,
                                new ViewAction() {
                                    @Override
                                    public Matcher<View> getConstraints() {
                                        return null;
                                    }

                                    @Override
                                    public String getDescription() {
                                        return "";
                                    }

                                    @Override
                                    public void perform(UiController uiController, View view) {
                                        View button = view.findViewById(R.id.comment_button);
                                        button.performClick();
                                    }
                                })
                );
        //Check the intended class is active
        intended(hasComponent(CommentActivity.class.getName()));

        //Performs typeText on comment EditText
        onView(withId(R.id.write_comment)).perform(typeText("test"), closeSoftKeyboard());

        //Performs click to post  comment
        onView(withId(R.id.comment_post)).perform(click());

        //perform click on back arrow to
        onView(withId(R.id.comments_back_arrow)).perform(click());

        //Go to profile page
        onView(withId(R.id.ic_profile)).perform(click());

        //Check the intended class is active
        intended(hasComponent(ProfileActivity.class.getName()));

        // Release main activity so test can finish
        Intents.release();
    }


    //This method allows for switching between fragments in viewpager
    @NonNull
    private static ViewAction selectTabAtPosition(final int position) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isDisplayed(), isAssignableFrom(TabLayout.class));
            }

            @Override
            public String getDescription() {
                return "with tab at index" + position;
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

}