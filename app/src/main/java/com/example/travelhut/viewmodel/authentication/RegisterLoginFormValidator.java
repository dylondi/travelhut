package com.example.travelhut.viewmodel.authentication;

import android.util.Patterns;
import android.widget.EditText;

import java.util.regex.Pattern;

public class RegisterLoginFormValidator {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 6 characters
                    "$");

    public boolean validateEmail(EditText emailEditText) {
        String emailInput = emailEditText.getText().toString().trim();
        if (emailInput.isEmpty()) {
            emailEditText.setError("Field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            emailEditText.setError("Please enter a valid email address");
            return false;
        } else {
            emailEditText.setError(null);
            return true;
        }
    }

    boolean validateUsername(EditText userNameEditText) {
        String fullNameInput = userNameEditText.getText().toString().trim();
        if (fullNameInput.isEmpty()) {
            userNameEditText.setError("Field can't be empty");
            return false;
        } else if (fullNameInput.length() > 20) {
            userNameEditText.setError("Username too long");
            return false;
        } else {
            userNameEditText.setError(null);
            return true;
        }
    }

    public  boolean validatePasswordOne(EditText passwordForm) {
        String passwordInput = passwordForm.getText().toString().trim();

        if (passwordInput.isEmpty()) {
            passwordForm.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            passwordForm.setError("Password too weak");
            return false;
        } else{
            passwordForm.setError(null);
            return true;
        }
    }

    boolean validatePasswordTwo(EditText passwordForm, EditText passwordTwoForm) {
        String passwordInput = passwordForm.getText().toString().trim();
        String passwordInputTwo = passwordTwoForm.getText().toString().trim();

        if (passwordInput.isEmpty()) {
            passwordTwoForm.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            passwordTwoForm.setError("Password too weak");
            return false;
        } else if (!passwordInput.equals(passwordInputTwo)) {
            passwordTwoForm.setError("You've entered two different passwords");
            return false;
        } else {
            passwordTwoForm.setError(null);
            return true;
        }
    }
}
