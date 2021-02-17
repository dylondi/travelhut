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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.travelhut.R;
import com.example.travelhut.viewmodel.authentication.LoginViewModel;
import com.example.travelhut.views.main.newsfeed.NewsFeedActivity;
import com.google.firebase.auth.FirebaseUser;


public class LoginFragment extends Fragment {

    private EditText emailLogin, passwordLogin;
    private TextView forgotPassword;
    private Button loginButton;
    private float v = 0;

    private LoginViewModel loginViewModel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_login, container, false);

        //assigning views
        emailLogin = root.findViewById(R.id.emailLogin);
        passwordLogin = root.findViewById(R.id.passwordLogin);
        forgotPassword = root.findViewById(R.id.forgotPassword);
        loginButton = root.findViewById(R.id.loginBtn);

        //set position for views for splash
        emailLogin.setTranslationY(300);
        passwordLogin.setTranslationY(300);
        forgotPassword.setTranslationY(300);
        loginButton.setTranslationY(300);

        //sets the view to be transparent
        emailLogin.setAlpha(v);
        passwordLogin.setAlpha(v);
        forgotPassword.setAlpha(v);
        loginButton.setAlpha(v);


        //animates the movement of these views
        emailLogin.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400);
        passwordLogin.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400);
        forgotPassword.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400);
        loginButton.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400);


        //onClickListener for login button to call the login method in the LoginViewModel
        loginButton.setOnClickListener(view -> {
            String email = emailLogin.getText().toString().trim();
            String password = passwordLogin.getText().toString().trim();

            boolean isEmailValid = loginViewModel.validateEmail(emailLogin);
            boolean isPasswordValid = loginViewModel.validatePasswordOne(passwordLogin);

            if (isEmailValid && isPasswordValid) {
                loginViewModel.login(email, password);
                loginViewModel.getUserMutableLiveData().observe(getViewLifecycleOwner(), firebaseUser -> {
                    if (firebaseUser != null) {
                        Intent myIntent = new Intent(getActivity(), NewsFeedActivity.class);
                        startActivity(myIntent);
                    }
                });
            }
        });

        return root;
    }
}
