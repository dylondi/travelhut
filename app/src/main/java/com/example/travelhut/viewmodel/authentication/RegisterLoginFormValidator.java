package com.example.travelhut.viewmodel.authentication;

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

    private static final Pattern EMAIL_ADDRESS
            = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    public boolean validateEmail(EditText emailEditText) {
        String emailInput = emailEditText.getText().toString().trim();
        if (emailInput.isEmpty()) {
            emailEditText.setError(AuthViewModelStrings.FIELD_CANNOT_BE_EMPTY);
            return false;
        } else if (!EMAIL_ADDRESS.matcher(emailInput).matches()) {
            emailEditText.setError(AuthViewModelStrings.ENTER_VALID_EMAIL);
            return false;
        } else {
            emailEditText.setError(null);
            return true;
        }
    }

    boolean validateUsername(EditText userNameEditText) {
        String fullNameInput = userNameEditText.getText().toString().trim();
        if (fullNameInput.isEmpty()) {
            userNameEditText.setError(AuthViewModelStrings.FIELD_CANNOT_BE_EMPTY);
            return false;
        } else if (fullNameInput.length() > 20) {
            userNameEditText.setError(AuthViewModelStrings.USERNAME_TOO_LONG);
            return false;
        } else {
            userNameEditText.setError(null);
            return true;
        }
    }

    public  boolean validatePasswordOne(EditText passwordForm) {
        String passwordInput = passwordForm.getText().toString().trim();

        if (passwordInput.isEmpty()) {
            passwordForm.setError(AuthViewModelStrings.FIELD_CANNOT_BE_EMPTY);
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            passwordForm.setError(AuthViewModelStrings.PASSWORD_BAD_FORMAT);
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
            passwordTwoForm.setError(AuthViewModelStrings.FIELD_CANNOT_BE_EMPTY);
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            passwordTwoForm.setError(AuthViewModelStrings.PASSWORD_BAD_FORMAT);
            return false;
        } else if (!passwordInput.equals(passwordInputTwo)) {
            passwordTwoForm.setError(AuthViewModelStrings.PASSWORDS_DONT_MATCH);
            return false;
        } else {
            passwordTwoForm.setError(null);
            return true;
        }
    }
}
