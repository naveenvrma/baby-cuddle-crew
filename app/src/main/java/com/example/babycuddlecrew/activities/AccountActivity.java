package com.example.babycuddlecrew.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.babycuddlecrew.R;
import com.example.babycuddlecrew.activities.fragments.HomeFragment;
import com.example.babycuddlecrew.activities.fragments.PostFragment;
import com.example.babycuddlecrew.activities.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AccountActivity extends AppCompatActivity {

    private BottomNavigationView navBarBottom;
    private HomeFragment homeFragment;
    private PostFragment postFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        navBarBottom = (BottomNavigationView) findViewById(R.id.navBarBottom);

        homeFragment = new HomeFragment();

        postFragment = new PostFragment();

        profileFragment = new ProfileFragment();

        setFragment(homeFragment);

        navBarBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navItemHome:
                        navBarBottom.setItemBackgroundResource(R.color.colorGreen);
                        setFragment(homeFragment);
                        return true;
                    case R.id.navItemPost:
                        navBarBottom.setItemBackgroundResource(R.color.colorBlue);
                        setFragment(new PostFragment());
                        return true;
                    case R.id.navItemProfile:
                        navBarBottom.setItemBackgroundResource(R.color.colorRed);
                        setFragment(profileFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
