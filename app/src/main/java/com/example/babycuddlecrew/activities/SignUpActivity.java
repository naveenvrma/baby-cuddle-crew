package com.example.babycuddlecrew.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.babycuddlecrew.IClickListener;
import com.example.babycuddlecrew.R;
import com.example.babycuddlecrew.Utils;
import com.example.babycuddlecrew.domain.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.babycuddlecrew.Utils.APP_PREFERENCES;
import static com.example.babycuddlecrew.Utils.CAMERA;
import static com.example.babycuddlecrew.Utils.GALLERY;
import static com.example.babycuddlecrew.Utils.PERMISSION_CODE;
import static com.example.babycuddlecrew.Utils.REFERENCE_USERS;
import static com.example.babycuddlecrew.Utils.setUser;

public class SignUpActivity extends AppCompatActivity {

    private EditText txtFirstName, txtLastName, txtEmail, txtPhoneNumber, txtUsername, txtPassword, txtPassword2;
    private RadioGroup radioGroupGender, radioGroupRole;
    private RadioButton radioFemale, radioMale, radioBabysitter, radioParent;
    private Button btnSignUp;
    private boolean empty = false;
    private String gender, role;
    private CircleImageView profileimage;
    private Uri filePath;
    public String bitmap = "";

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth auth;
    FirebaseStorage storage;
    StorageReference storageReference;
    boolean fromProfileFragment = false;

    private User savedUser;
    private SharedPreferences prefs;
    private Gson gson;
    private TextView tv_Email;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Intent intent = getIntent();
        fromProfileFragment = intent.getBooleanExtra("fromProfileFragment", false);

        if (!checkPermission()) {
            requestPermission();
        }

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        //Get Firebase database instance
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        profileimage = findViewById(R.id.imgprofile);
        txtFirstName = (EditText) findViewById(R.id.txtFirstName);
        txtLastName = (EditText) findViewById(R.id.txtLastName);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtPassword2 = (EditText) findViewById(R.id.txtPassword2);
        radioGroupGender = (RadioGroup) findViewById(R.id.radioGroupGender);
        radioGroupRole = (RadioGroup) findViewById(R.id.radioGroupRole);
        radioFemale = (RadioButton) findViewById(R.id.radioFemale);
        radioMale = (RadioButton) findViewById(R.id.radioMale);
        radioBabysitter = (RadioButton) findViewById(R.id.radioBabysitter);
        radioParent = (RadioButton) findViewById(R.id.radioParent);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        if (fromProfileFragment) {

            txtEmail.setVisibility(View.INVISIBLE);
            tv_Email = findViewById(R.id.tv_Email);
            tv_Email.setVisibility(View.VISIBLE);
            prefs = getApplicationContext().getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
            gson = new GsonBuilder().create();
            getMyUser();
            Glide.with(SignUpActivity.this).load(savedUser.getProfileImage()).into(profileimage);
            txtFirstName.setText(savedUser.getFirstName());
            txtLastName.setText(savedUser.getLastName());
            txtPhoneNumber.setText(savedUser.getPhoneNumber());
            txtUsername.setText(savedUser.getUsername());
            txtPassword.setText(savedUser.getPassword());
            txtUsername.setText(savedUser.getUsername());
            tv_Email.setText(savedUser.getMail());
            txtPassword2.setText(savedUser.getPassword());
            if (savedUser.getGender().equalsIgnoreCase("Male")) {
                radioMale.setChecked(true);
            } else {
                radioFemale.setChecked(true);
            }
            if (savedUser.getRole().equalsIgnoreCase("Parent")) {
                radioParent.setChecked(true);
            } else {
                radioBabysitter.setChecked(true);
            }
            btnSignUp.setText("UPDATE");
        }


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = txtFirstName.getText().toString();
                String lastName = txtLastName.getText().toString();
                String mail = txtEmail.getText().toString();
                String phoneNumber = txtPhoneNumber.getText().toString();
                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();
                String password2 = txtPassword2.getText().toString();
                gender = checkButton(view, radioGroupGender);
                role = checkButton(view, radioGroupRole);

                if (fromProfileFragment) {
                    mail = tv_Email.getText().toString();
                    if (filePath == null) {
                        bitmap = savedUser.getProfileImage();
                    }
                }

                validateFields(firstName, lastName, mail, phoneNumber, username, password, password2);

                if (!empty) {
                    if (password.equals(password2)) {
                        User user = new User(bitmap, firstName, lastName, mail, gender, role, username, password, phoneNumber, "");
                        //  databaseHelper.insertUser(user);
                        if (fromProfileFragment) {
                            //Update User
                            user.setProfileImage(bitmap);
                            user.setUserId(savedUser.getUserId());
                            if (user.getPassword().equals(savedUser.getPassword())) {
                                userRef.setValue(user);
                                setUser(user,SignUpActivity.this);
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                updatePassword(user, savedUser.getPassword());
                            }
                        } else {
                            authUser(mail, password, user);
                        }
                    } else {
                        toastMessage("Passwords must be the same!");
                    }
                }
                empty = false;
            }
        });

        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 OpenImages();

            }
        });
        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                radioFemale = (RadioButton) findViewById(R.id.radioFemale);
                radioMale = (RadioButton) findViewById(R.id.radioMale);
                if (radioGroupGender.getCheckedRadioButtonId() == -1) {
                    radioFemale.setError("You need to select one field");
                    radioMale.setError("You need to select one field");
                    empty = true;
                } else {
                    radioFemale.setError(null);
                    radioMale.setError(null);
                }
            }
        });

        radioGroupRole.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                radioBabysitter = (RadioButton) findViewById(R.id.radioBabysitter);
                radioParent = (RadioButton) findViewById(R.id.radioParent);
                if (radioGroupRole.getCheckedRadioButtonId() == -1) {
                    radioBabysitter.setError("You need to select one field");
                    radioParent.setError("You need to select one field");
                    empty = true;
                } else {
                    radioBabysitter.setError(null);
                    radioParent.setError(null);
                }
            }
        });
    }

    private String checkButton(View v, RadioGroup radioGroup) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(radioId);
        try {
            String textButton = radioButton.getText().toString();
            return textButton;
        } catch (Exception e) {
            return "";
        }
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void validateFields(String firstName, String lastName, String mail, String phoneNumber, String username,
                                String password, String password2) {
        if (firstName.trim().equalsIgnoreCase("")) {
            txtFirstName.setError("This field can not be blank");
            empty = true;
        }
        if (lastName.trim().equalsIgnoreCase("")) {
            txtLastName.setError("This field can not be blank");
            empty = true;
        }
        if (mail.trim().equalsIgnoreCase("")) {
            txtEmail.setError("This field can not be blank");
            empty = true;
        } else {
            if (!fromProfileFragment) {
                if (!txtEmail.getText().toString().trim().contains("@") ||
                        !txtEmail.getText().toString().trim().contains(".com")) {
                    txtEmail.setError("Not valid mail");
                    empty = true;
                }
            }
        }
        if (phoneNumber.trim().equalsIgnoreCase("")) {
            txtPhoneNumber.setError("This field can not be blank");
            empty = true;
        }
        if (username.trim().equalsIgnoreCase("")) {
            txtUsername.setError("This field can not be blank");
            empty = true;
        }
        if (password.trim().equalsIgnoreCase("")) {
            txtPassword.setError("This field can not be blank");
            empty = true;
        } else {
            boolean isDigit = false;
            boolean isCapital = false;
            boolean isLower = false;
            for (char c : password.toCharArray()) {
                if (Character.isDigit(c)) {
                    isDigit = true;
                } else if (Character.isUpperCase(c)) {
                    isCapital = true;
                } else if (Character.isLowerCase(c)) {
                    isLower = true;
                }
            }
            if (password.length() < 6 || !isDigit || !isCapital || !isLower) {
                txtPassword.setError("Password must contain 6 characters or more and at least one digit and one big letter");
                empty = true;
            }
        }
        if (password2.trim().equalsIgnoreCase("")) {
            txtPassword2.setError("This field can not be blank");
            empty = true;
        } else {
            boolean isDigit = false;
            boolean isCapital = false;
            boolean isLower = false;
            for (char c : password2.toCharArray()) {
                if (Character.isDigit(c)) {
                    isDigit = true;
                } else if (Character.isUpperCase(c)) {
                    isCapital = true;
                } else if (Character.isLowerCase(c)) {
                    isLower = true;
                }
            }
            if (password2.length() < 6 || !isDigit || !isCapital || !isLower) {
                txtPassword2.setError("Password must contain 6 characters or more and at least one digit and one big letter");
                empty = true;
            }
        }
        RadioButton radioFemale = (RadioButton) findViewById(R.id.radioFemale);
        RadioButton radioMale = (RadioButton) findViewById(R.id.radioMale);
        if (radioGroupGender.getCheckedRadioButtonId() == -1) {
            radioFemale.setError("You need to select one field");
            radioMale.setError("You need to select one field");
            empty = true;
        } else {
            radioFemale.setError(null);
            radioMale.setError(null);
        }

        RadioButton radioBabysitter = (RadioButton) findViewById(R.id.radioBabysitter);
        RadioButton radioParent = (RadioButton) findViewById(R.id.radioParent);
        if (radioGroupRole.getCheckedRadioButtonId() == -1) {
            radioBabysitter.setError("You need to select one field");
            radioParent.setError("You need to select one field");
            empty = true;
        } else {
            radioBabysitter.setError(null);
            radioParent.setError(null);
        }
    }

    private void addUser(final User user) {
        DatabaseReference presentersReference = mFirebaseDatabase.getReference(REFERENCE_USERS);
        String presenterId = auth.getCurrentUser().getUid();
        user.setUserId(presenterId);
        presentersReference.child(presenterId).setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    toastMessage("User created succesfuly");
                    Log.e("TAG", "succesfully added");
                    finish();
                } else {
                    // Failed to add presenter
                    Log.e("TAG", "Failed to add", databaseError.toException());
                }
            }
        });
    }

    private void authUser(final String email, final String password, final User user) {

        Utils.uploadImage(filePath, SignUpActivity.this, storageReference, new IClickListener() {
            @Override
            public void onFavClickListener(int pos) {

            }

            @Override
            public void onItemClickListener(int pos) {

            }

            @Override
            public void onUploadCompleteListener(final String filepath) {

                Log.d("filepath", filepath);
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    toastMessage("Authentication failed." + task.getException());
                                } else {
                                    bitmap = filepath;
                                    user.setProfileImage(bitmap);
                                    addUser(user);
                                }
                            }
                        });
            }
        });
    }

    private void updatePassword(final User userVal, String oldPwd) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider.getCredential(userVal.getMail(), oldPwd);
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(userVal.getPassword()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        toastMessage("Password updated");
                                        userRef.setValue(userVal);
                                        setUser(userVal,SignUpActivity.this);
                                        setResult(RESULT_OK);
                                        finish();
                                    } else {
                                        toastMessage("Error password not updated");
                                    }
                                }
                            });
                        } else {
                            toastMessage("Error auth failed");
                        }
                    }
                });
    }

    public boolean checkPermission() {

        int result = ContextCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.CAMERA);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;

    }

    public void requestPermission() {

        ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSION_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case PERMISSION_CODE:

                if (grantResults.length > 0) {

                    boolean storage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameras = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (storage && cameras) {

                        Toast.makeText(SignUpActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(SignUpActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                                showMsg("You need to allow access to the permissions", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSION_CODE);
                                        }
                                    }
                                });
                                return;
                            }
                        }
                    }
                }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == CAMERA) {
                openCameraResult(data);
            } else if (requestCode == GALLERY) {
                openGalleryResult(data);
            }
        }

    }

    private void showMsg(String s, DialogInterface.OnClickListener listener) {

        new AlertDialog.Builder(SignUpActivity.this)
                .setMessage(s)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void OpenImages() {

        final CharSequence[] option = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle("Select Action");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (option[which].equals("Camera")) {
                    CameraIntent();
                }
                if (option[which].equals("Gallery")) {
                    GalleryIntent();

                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void GalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), Utils.GALLERY);
    }

    private void CameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, Utils.CAMERA);
    }

    public void openGalleryResult(Intent data) {

        Bitmap bitmap = null;

        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(SignUpActivity.this.getContentResolver(), data.getData());
                filePath = Utils.getImageUri(SignUpActivity.this, bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        profileimage.setImageBitmap(bitmap);
    }

    public void openCameraResult(Intent data) {

        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        filePath = Utils.getImageUri(SignUpActivity.this, bitmap);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File paths = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
//        Toast.makeText(SignUpActivity.this, "Path -> " + paths.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        Log.d("tag", "File Path -> " + paths.getName());

        try {
            FileOutputStream fos = new FileOutputStream(paths);
            paths.createNewFile();

            if (!paths.exists()) {
                paths.mkdir();
            }

            fos.write(bytes.toByteArray());
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        profileimage.setImageBitmap(bitmap);
    }

    public User getMyUser() {
        if (savedUser == null) {
            savedUser = Utils.getCurrentUser(SignUpActivity.this);
        }
        if (savedUser != null) {
            userRef = mDatabaseReference.child(REFERENCE_USERS).child(savedUser.getUserId());
        }
        return savedUser;
    }

}


