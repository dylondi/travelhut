package com.example.travelhut.views.authentication;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.travelhut.R;
import com.example.travelhut.model.utils.StringsRepository;
import com.example.travelhut.viewmodel.authentication.RegisterViewModel;

public class RegisterFragment extends Fragment {

    //Instance Variables
    private EditText emailEditText, usernameEditText, passwordEditText, passwordTwoEditText;
    private Button registerButton;
    private RegisterViewModel registerViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerViewModel = ViewModelProviders.of(this).get(RegisterViewModel.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_register, container, false);

        //Initialize views
        initViews(root);


        //OnClickListener for register button which calls the register method in the RegisterViewModel
        registerButton.setOnClickListener(view -> {

            //Boolean validators of the form info inputted by user
            boolean isEmailValid = registerViewModel.validateEmail(emailEditText);
            boolean isUsernameValid = registerViewModel.validateUsername(usernameEditText);
            boolean isPasswordValid = registerViewModel.validatePasswordOne(passwordEditText);
            boolean isPasswordTwoValid = registerViewModel.validatePasswordTwo(passwordEditText, passwordTwoEditText);

            //If all inputs are valid
            if (isEmailValid && isUsernameValid && isPasswordValid && isPasswordTwoValid) {

                //Calls ViewModel to send a register request
                registerViewModel.register(emailEditText.getText().toString(), usernameEditText.getText().toString(), passwordEditText.getText().toString());

                //Observe boolean value of registeredMutableLiveData object in ViewModel
                registerViewModel.getRegisteredMutableLiveData().observe(getViewLifecycleOwner(), registered -> {

                    if (registered) {
                        //Create toast informing the new user has been created successfully, then switch the viewPager to index 0 (login page)
                        Toast.makeText(getActivity(), StringsRepository.USER_CREATED, Toast.LENGTH_SHORT).show();
                        RegisterLoginActivity.viewPager.setCurrentItem(0);
                    } else {
                        //Observe regFailedMessageMutableLiveData object which contains the failed registration message
                        registerViewModel.getRegFailedMessageMutableLiveData().observe(getViewLifecycleOwner(), message -> {
                            //Create toast displaying this message to the user
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
        });
        return root;
    }

    //This methods initializes the view objects
    private void initViews(ViewGroup root) {
        emailEditText = root.findViewById(R.id.emailRegister);
        usernameEditText = root.findViewById(R.id.usernameRegister);
        passwordEditText = root.findViewById(R.id.passwordRegister);
        passwordTwoEditText = root.findViewById(R.id.passwordTwoRegister);
        registerButton = root.findViewById(R.id.registerBtn);
    }
}
