package com.example.babycuddlecrew.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.babycuddlecrew.R;
import com.example.babycuddlecrew.Utils;
import com.example.babycuddlecrew.domain.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static com.example.babycuddlecrew.Utils.APP_PREFERENCES;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private Gson gson;
    private User savedUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        prefs = getApplicationContext().getSharedPreferences(APP_PREFERENCES,MODE_PRIVATE);
        gson = new GsonBuilder().create();

        new Handler().postDelayed(new Runnable() {
            // Using handler with postDelayed called runnable run method
            @Override
            public void run() {

                if (getMyUser() != null) {
                    Intent intent = new Intent(SplashActivity.this, AccountActivity.class);
                    intent.putExtra("user", getMyUser());
                    startActivity(intent);
                } else {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    // close this activity
                }
                finish();
            }

        }, 5 * 1000); // wait for 5 seconds
    }

    public User getMyUser() {
        if (savedUser == null) {
            String savedValue = prefs.getString(Utils.PREF_LOGIN_USER, "");
            if (savedValue.equals("")) {
                savedUser = null;
            } else {
                savedUser = gson.fromJson(savedValue, User.class);
            }
        }
        return savedUser;
    }
}
