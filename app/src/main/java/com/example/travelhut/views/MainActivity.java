package com.example.travelhut.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.travelhut.R;
import com.example.travelhut.viewmodel.MainActivityViewModel;
import com.example.travelhut.views.authentication.LoginActivity;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private TextView loggedInUserTextView;
    private Button logoutButton;

    private MainActivityViewModel mainActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loggedInUserTextView = findViewById(R.id.user);
        logoutButton = findViewById(R.id.logoutBtn);
        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        mainActivityViewModel.getUserMutableLiveData().observe(this, firebaseUser -> {
            if(firebaseUser != null){
                loggedInUserTextView.setText("Logged in user: " + firebaseUser.getEmail());
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivityViewModel.logout();
            }
        });

        mainActivityViewModel.getLoggedOutMutableLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loggedOut) {
                if(loggedOut){
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        });

    }
    @Override
    public void onBackPressed() {
        // Simply Do noting!
    }

}