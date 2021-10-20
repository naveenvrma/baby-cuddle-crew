package com.example.babycuddlecrew.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.babycuddlecrew.R;
import com.example.babycuddlecrew.Utils;
import com.example.babycuddlecrew.domain.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static com.example.babycuddlecrew.Utils.REFERENCE_USERS;

public class LogInActivity extends AppCompatActivity {

    private EditText txtUsername, txtPassword;
    private Button btnLogIn,btnsignup;
    private CheckBox cbViewPassword;
    private Gson gson;
    Intent intent;
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        intent = getIntent();
        data = intent.getStringExtra("Category");
        gson = new GsonBuilder().create();
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnLogIn = (Button) findViewById(R.id.btnLogIn);
        btnLogIn.setText("LOGIN AS "+data);
        cbViewPassword = (CheckBox) findViewById(R.id.cbViewPassword);

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();
                if (username.length() > 0 && password.length() > 0)
                    authenticateUser(username, password);
                else
                    toastMessage("Please enter all the details");
            }
        });

        btnsignup = findViewById(R.id.btnsignup);
        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signupintent = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(signupintent);
                finish();
            }
        });

        cbViewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbViewPassword.isChecked()) {
                    txtPassword.setTransformationMethod(null);
                } else {
                    txtPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void authenticateUser(final String email, String password) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        //authenticate user
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LogInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            // there was an error
                            toastMessage("Please enter valid credentials");
                        } else {
                            findUser(email);
                        }
                    }
                });
    }

    private void findUser(String email) {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myDatabaseReference = mFirebaseDatabase.getReference(REFERENCE_USERS);
        Query deleteQuery = myDatabaseReference.orderByChild("mail").equalTo(email);
        deleteQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datas : snapshot.getChildren()) {
                    User user = datas.getValue(User.class);
                    Log.d("Item found: ", user.getFirstName());
                    toastMessage("You are logged in!");
                    Utils.setUser(user, getApplicationContext());
                    if (data.equalsIgnoreCase(user.getRole())) {
                        Intent searchActivityIntent = new Intent(LogInActivity.this, AccountActivity.class);
                        startActivity(searchActivityIntent);
                        setResult(RESULT_OK);
                        finish();
                    }
                    else {
                        toastMessage("User not registered as "+data);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Item not found: ", "this item is not in the list");
            }
        });

    }
}
