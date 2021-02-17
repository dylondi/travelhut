package com.example.travelhut.views.authentication;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.test.rule.ActivityTestRule;

import com.example.travelhut.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import static org.junit.Assert.*;

public class LoginFragmentTest {


    @Rule
    public ActivityTestRule<TestActivity> mActivityTestRule = new ActivityTestRule<TestActivity>(TestActivity.class);

    private TestActivity mActivity = null;



    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
        mActivityTestRule = null;
    }

    private String in = "dylondirusciotest@gmail.com";
    private String in1 = "Test12";




    public void testLaunch(){
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.test_container);
        assertNotNull(rlContainer);

        LoginFragment fragment = new LoginFragment();


        mActivity.getFragmentManager().beginTransaction().add(rlContainer.getId(), fragment).commitAllowingStateLoss();

        View view = fragment.getView().findViewById(R.id.fragment_login);
    }

}