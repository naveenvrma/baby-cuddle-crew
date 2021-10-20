package com.example.babycuddlecrew.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.babycuddlecrew.Constants;
import com.example.babycuddlecrew.R;

import static com.example.babycuddlecrew.Utils.CAMERA;
import static com.example.babycuddlecrew.Utils.GALLERY;

public class MainActivity extends AppCompatActivity {

    private Button btnSignUp, btnLogIn_babysitter, btnLogIn_parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogIn_babysitter = (Button) findViewById(R.id.btnLogIn_Babysitter);
        btnLogIn_parent = (Button) findViewById(R.id.btnLogIn_Parent);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        btnLogIn_babysitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logInIntent = new Intent(MainActivity.this, LogInActivity.class);
                logInIntent.putExtra("Category", Constants.BABY_SITTER);
                startActivityForResult(logInIntent,300);
            }
        });

        btnLogIn_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logInIntent = new Intent(MainActivity.this, LogInActivity.class);
                logInIntent.putExtra("Category", Constants.PARENT);
                startActivityForResult(logInIntent, 300);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpIntent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 300) {
               finish();
            }
        }
    }
}
