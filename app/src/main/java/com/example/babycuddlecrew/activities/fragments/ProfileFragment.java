package com.example.babycuddlecrew.activities.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.babycuddlecrew.R;
import com.example.babycuddlecrew.Utils;
import com.example.babycuddlecrew.activities.MainActivity;
import com.example.babycuddlecrew.activities.SignUpActivity;
import com.example.babycuddlecrew.domain.User;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.example.babycuddlecrew.Utils.APP_PREFERENCES;
import static com.example.babycuddlecrew.Utils.PREF_LOGIN_USER;
import static com.example.babycuddlecrew.Utils.UPDATE_USER;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    View view;
    User user;

    TextView txtUserRole, txtFirstName, txtLastName, txtGender, txtMail, txtPhoneNumber;
    CircleImageView profileImage;
    Button signout,editProfile;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        user = Utils.getCurrentUser(getActivity());

        txtUserRole = view.findViewById(R.id.txtUserRole);
        txtFirstName = (TextView) view.findViewById(R.id.txtFirstName);
        txtLastName = (TextView) view.findViewById(R.id.txtLastName);
        txtGender = (TextView) view.findViewById(R.id.txtGender);
        txtMail = (TextView) view.findViewById(R.id.txtMail);
        txtPhoneNumber = (TextView) view.findViewById(R.id.txtPhoneNumber);
        profileImage = (CircleImageView) view.findViewById(R.id.imgprofile);
        signout = (Button) view.findViewById(R.id.btn_signout);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeSharedPrefrenceData(getContext(), APP_PREFERENCES, PREF_LOGIN_USER);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        editProfile = (Button) view.findViewById(R.id.btn_edit);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SignUpActivity.class);
                intent.putExtra("fromProfileFragment",true);
                startActivityForResult(intent,UPDATE_USER);
            }
        });
        setUserData();
        return view;
    }

    private void setUserData() {
        user = Utils.getCurrentUser(getActivity());
        if (user != null) {
            Glide.with(getActivity()).load(user.getProfileImage()).into(profileImage);
            txtUserRole.setText("I am a " + user.getRole());
            txtFirstName.setText(user.getFirstName());
            txtLastName.setText(user.getLastName());
            txtGender.setText(user.getGender());
            txtMail.setText(user.getMail());
            txtPhoneNumber.setText(user.getPhoneNumber());
        }
    }

    public static void removeSharedPrefrenceData(Context context, String projectPrefName, String contentPrefName) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(projectPrefName,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor prefEditor = prefs.edit();
            prefEditor.remove(contentPrefName);
            prefEditor.clear();
            prefEditor.commit();
        } catch (Exception e) {
            Log.i("", "Exception : " + e.toString());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == UPDATE_USER) {
                setUserData();
            }
        }
    }
}
