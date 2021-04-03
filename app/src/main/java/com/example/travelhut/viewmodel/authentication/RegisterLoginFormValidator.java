package com.example.travelhut.viewmodel.authentication;

import android.widget.EditText;

import java.util.regex.Pattern;

public class RegisterLoginFormValidator {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //At least 1 digit
                    "(?=.*[a-z])" +         //At least 1 lower case letter
                    "(?=.*[A-Z])" +         //At least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //Any letter
                    "(?=\\S+$)" +           //No white spaces
                    ".{6,}" +               //At least 6 characters
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


    //This method validates the email input is in correct format
    public boolean validateEmail(EditText emailEditText) {
        String emailInput = emailEditText.getText().toString().trim();

        //Checks if email is empty string
        if (emailInput.isEmpty()) {
            emailEditText.setError(AuthViewModelStrings.FIELD_CANNOT_BE_EMPTY);
            return false;
        }

        //Validates email adheres to the pattern of an email
        else if (!EMAIL_ADDRESS.matcher(emailInput).matches()) {
            emailEditText.setError(AuthViewModelStrings.ENTER_VALID_EMAIL);
            return false;
        }
        //Returns true -> email is valid
        else {
            emailEditText.setError(null);
            return true;
        }
    }

    //This method validates the username input is in correct format
    boolean validateUsername(EditText userNameEditText) {
        String fullNameInput = userNameEditText.getText().toString().trim();

        //Checks if username is empty string
        if (fullNameInput.isEmpty()) {
            userNameEditText.setError(AuthViewModelStrings.FIELD_CANNOT_BE_EMPTY);
            return false;
        }

        //Checks username length is no longer than 20 characters
        else if (fullNameInput.length() > 20) {
            userNameEditText.setError(AuthViewModelStrings.USERNAME_TOO_LONG);
            return false;
        }

        //Returns true -> username is valid
        else {
            userNameEditText.setError(null);
            return true;
        }
    }

    //This method validates the password input is in correct format
    public boolean validatePasswordOne(EditText passwordForm) {
        String passwordInput = passwordForm.getText().toString().trim();

        //Checks if password is empty string
        if (passwordInput.isEmpty()) {
            passwordForm.setError(AuthViewModelStrings.FIELD_CANNOT_BE_EMPTY);
            return false;
        }

        //Checks if password input adheres to the pattern of a password
        else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            passwordForm.setError(AuthViewModelStrings.PASSWORD_BAD_FORMAT);
            return false;
        }

        //Returns true -> password is in correct format
        else {
            passwordForm.setError(null);
            return true;
        }
    }


    //This method validates the second password upon registering an account
    boolean validatePasswordTwo(EditText passwordForm, EditText passwordTwoForm) {
        String passwordInput = passwordForm.getText().toString().trim();
        String passwordInputTwo = passwordTwoForm.getText().toString().trim();

        //Checks if the password input is an empty string
        if (passwordInput.isEmpty()) {
            passwordTwoForm.setError(AuthViewModelStrings.FIELD_CANNOT_BE_EMPTY);
            return false;
        }

        //Checks if password input adheres to the pattern of a password
        else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            passwordTwoForm.setError(AuthViewModelStrings.PASSWORD_BAD_FORMAT);
            return false;
        }

        //Checks the second password matches the first password inputted
        else if (!passwordInput.equals(passwordInputTwo)) {
            passwordTwoForm.setError(AuthViewModelStrings.PASSWORDS_DONT_MATCH);
            return false;
        }
        //Returns true -> password is in correct format & matches
        else {
            passwordTwoForm.setError(null);
            return true;
        }
    }
}
