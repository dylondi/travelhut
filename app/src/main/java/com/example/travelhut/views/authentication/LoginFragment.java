package com.example.travelhut.views.authentication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.travelhut.R;
import com.example.travelhut.viewmodel.authentication.LoginViewModel;
import com.example.travelhut.views.main.newsfeed.NewsFeedActivity;


public class LoginFragment extends Fragment {


    //Instance Variables
    private EditText emailLogin, passwordLogin;
    private TextView forgotPassword;
    private Button loginButton;
    private float v = 0;
    private LoginViewModel loginViewModel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize LoginViewModel
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_login, container, false);

        //Initialize views
        initViews(root);

        //Sets translation values
        setTranslationValues();

        //Sets the view to be transparent
        setAlphaViews();

        //Animates the movement of these views
        animateViews();

        //OnClickListener for login button to call the login method in the LoginViewModel
        loginButton.setOnClickListener(view -> {

            //Get email and password Strings
            String email = emailLogin.getText().toString().trim();
            String password = passwordLogin.getText().toString().trim();

            //Validate email and password
            boolean isEmailValid = loginViewModel.validateEmail(emailLogin);
            boolean isPasswordValid = loginViewModel.validatePasswordOne(passwordLogin);

            //If validation was successful -> login user
            if (isEmailValid && isPasswordValid) {

                //Send login request to ViewModel
                loginViewModel.login(email, password);

                //Observe a boolean from ViewModel representing if user is logged out or not
                loginViewModel.getLoggedOutMutableLiveData().observe(getViewLifecycleOwner(), loggedOut -> {

                    if(loggedOut){

                        //Observe the login failed message and display in toast for user to see
                        loginViewModel.getLoginFailedMessageMutableLiveData().observe(getViewLifecycleOwner(), message -> {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        });
                    }
                });

                //Observe the current FirebaseUser from the ViewModel
                loginViewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), firebaseUser -> {

                    //If a current user exists (if logged in)
                    if (firebaseUser != null) {

                        //Navigate to News Feed
                        Intent myIntent = new Intent(getActivity(), NewsFeedActivity.class);
                        startActivity(myIntent);
                    }
                });

            }
        });

        return root;
    }

    //This method animates views for login fragment
    private void animateViews() {
        emailLogin.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400);
        passwordLogin.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400);
        forgotPassword.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400);
        loginButton.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400);
    }

    //This method sets opacity of views to 0
    private void setAlphaViews() {
        emailLogin.setAlpha(v);
        passwordLogin.setAlpha(v);
        forgotPassword.setAlpha(v);
        loginButton.setAlpha(v);
    }

    //This method sets translation values for views
    private void setTranslationValues() {
        emailLogin.setTranslationY(300);
        passwordLogin.setTranslationY(300);
        forgotPassword.setTranslationY(300);
        loginButton.setTranslationY(300);
    }

    //This method initializes view objects
    private void initViews(ViewGroup root) {
        emailLogin = root.findViewById(R.id.emailLogin);
        passwordLogin = root.findViewById(R.id.passwordLogin);
        forgotPassword = root.findViewById(R.id.forgotPassword);
        loginButton = root.findViewById(R.id.loginBtn);
    }
}
