package com.example.travelhut.views.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.travelhut.R;
import com.example.travelhut.viewmodel.authentication.RegisterViewModel;
import com.example.travelhut.views.main.newsfeed.NewsFeedActivity;
import com.google.firebase.auth.FirebaseUser;

public class RegisterFragment extends Fragment {

    private EditText emailEditText, usernameEditText, passwordEditText, passwordTwoEditText;
    private Button registerButton;

    private RegisterViewModel registerViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerViewModel = ViewModelProviders.of(this).get(RegisterViewModel.class);
//        registerViewModel.getUserMutableLiveData().observe(this, firebaseUser -> {
//            if(firebaseUser != null){
//
//            }
//        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_register, container, false);

        emailEditText = root.findViewById(R.id.emailRegister);
        usernameEditText = root.findViewById(R.id.usernameRegister);
        passwordEditText = root.findViewById(R.id.passwordRegister);
        passwordTwoEditText = root.findViewById(R.id.passwordTwoRegister);
        registerButton = root.findViewById(R.id.registerBtn);

        registerButton.setOnClickListener(view -> {

            boolean isEmailValid = registerViewModel.validateEmail(emailEditText);
            boolean isUsernameValid = registerViewModel.validateUsername(usernameEditText);
            boolean isPasswordValid = registerViewModel.validatePasswordOne(passwordEditText);
            boolean isPasswordTwoValid = registerViewModel.validatePasswordTwo(passwordEditText, passwordTwoEditText);

            if(isEmailValid && isUsernameValid && isPasswordValid && isPasswordTwoValid)
            {
                registerViewModel.register(emailEditText.getText().toString(), usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });

        return root;
    }
}
