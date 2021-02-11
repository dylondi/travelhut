package com.example.travelhut.viewmodel.main.profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.travelhut.model.AuthAppRepository;
import com.example.travelhut.model.UsersAppRepository;
import com.google.firebase.auth.FirebaseAuth;

public class SignOutFragmentViewModel extends ViewModel {

    private MutableLiveData<Boolean> loggedOutMutableLiveData;
    private UsersAppRepository usersAppRepository;

    public SignOutFragmentViewModel() {
        usersAppRepository = new UsersAppRepository();
        loggedOutMutableLiveData = usersAppRepository.getLoggedOutMutableLiveData();

    }

    public MutableLiveData<Boolean> getLoggedOutMutableLiveData() {
        return loggedOutMutableLiveData;
    }

    public void logout(){
        usersAppRepository.logout();
    }
}
