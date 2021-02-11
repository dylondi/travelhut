package com.example.travelhut.views.main.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.travelhut.R;
import com.example.travelhut.model.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;

public class EditProfileFragment extends Fragment {

    private static final String TAG = "EditProfileFragment";
    private ImageView editProfileImage;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        editProfileImage = view.findViewById(R.id.edit_profile_image);
        setProfileImage();


        ImageView backArrow = view.findViewById(R.id.edit_profile_back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: intent back to ProfileActivity.");
                getActivity().finish();
            }
        });
        return view;
    }


    private void setProfileImage(){
String imageUrl = "https://www.sportsfile.com/winshare/w540/Library/SF722/523374.jpg";
        UniversalImageLoader.setImage(imageUrl, editProfileImage, null, "");
    }
}
