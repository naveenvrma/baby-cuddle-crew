package com.example.babycuddlecrew.activities.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.babycuddlecrew.Constants;
import com.example.babycuddlecrew.IClickListener;
import com.example.babycuddlecrew.R;
import com.example.babycuddlecrew.Utils;
import com.example.babycuddlecrew.activities.SelectLocationActivity;
import com.example.babycuddlecrew.domain.Post;
import com.github.informramiz.daypickerlibrary.views.DayPickerDialog;
import com.github.informramiz.daypickerlibrary.views.DayPickerView;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static com.example.babycuddlecrew.Utils.CAMERA;
import static com.example.babycuddlecrew.Utils.GALLERY;
import static com.example.babycuddlecrew.Utils.PERMISSION_CODE;
import static com.example.babycuddlecrew.Utils.REFERENCE_POSTS;

public class PostFragment extends Fragment {

    private ImageView imageView;
    private MaterialSpinner spinnerCategory, spinnerDays;
    private TextView selectLoc, tvAdress;
    public int LAUNCH_MAPS_ACTIVITY = 99;
    private EditText et_Title, et_Description, et_phoneNumber;
    private TextView tvFromTime, tvToTime;
    private TimePickerDialog picker;
    private String selectedCategory = "", selected_Days = "", bitmap = "";
    private long fromTime = 0, toTime = 0;
    private Bitmap selectedImage;
    private LatLng selectedLatLng;
    private View view;
    private Button btn_postAd;

    private FirebaseDatabase mFirebaseDatabase;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    boolean empty = false;
    private String customdays = "";

    private FragmentActivity getPostActivity() {
        return getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_post, container, false);
        imageView = (ImageView) view.findViewById(R.id.profile_image);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        if (!checkPermission()) {
            requestPermission();
        }

        et_Title = view.findViewById(R.id.et_Title);
        et_Description = view.findViewById(R.id.et_Description);
        et_phoneNumber = view.findViewById(R.id.et_phoneNumber);
        btn_postAd = view.findViewById(R.id.btn_postAd);
        btn_postAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPost();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenImages();
            }
        });
        spinnerCategory = (MaterialSpinner) view.findViewById(R.id.spinner_category);
        spinnerCategory.setItems(Utils.getCategoryItems(getPostActivity()));
        spinnerCategory.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                selectedCategory = item;
            }
        });

        spinnerDays = (MaterialSpinner) view.findViewById(R.id.spinner_day);
        spinnerDays.setItems(Constants.SELECT_DAYS, Constants.WEEKENDS, Constants.WEEKDAYS, Constants.EVERYDAY, Constants.CUSTOM);
        spinnerDays.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                selected_Days = item;
                if (item.equalsIgnoreCase("Custom")) {

                    DayPickerDialog.Builder builder = new DayPickerDialog.Builder(getPostActivity())
                            .setMultiSelectionAllowed(true)
                            .setOnDaysSelectedListener(new DayPickerDialog.OnDaysSelectedListener() {
                                @Override
                                public void onDaysSelected(DayPickerView dayPickerView, boolean[] selectedDays) {
                                    //do something with selected days
                                    for (int i = 0; i < selectedDays.length; i++) {
                                        if (selectedDays[i]) {
                                            switch (i) {
                                                case 0:
                                                    customdays = customdays + "Sun ".concat(",");
                                                    break;
                                                case 1:
                                                    customdays = customdays + "Mon ".concat(",");
                                                    break;
                                                case 2:
                                                    customdays = customdays + "Tue ".concat(",");
                                                    break;
                                                case 3:
                                                    customdays = customdays + "Wed ".concat(",");
                                                    break;
                                                case 4:
                                                    customdays = customdays + "Thu ".concat(",");
                                                    break;
                                                case 5:
                                                    customdays = customdays + "Fri ".concat(",");
                                                    break;
                                                case 6:
                                                    customdays = customdays + "Sat ".concat(",");
                                                    break;
                                                default:
                                                    customdays = customdays;
                                                    break;
                                            }
                                        }
                                        Log.d("Selected", dayPickerView.getSelectedDays()[i] + "");
                                    }
                                    selected_Days = removeLastChar(customdays);
                                    customdays = "";
                                }
                            });
                    builder.build().show();
                }
            }
        });

        selectLoc = view.findViewById(R.id.tv_Location);
        selectLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getPostActivity(), SelectLocationActivity.class);
                i.putExtra("from", "PostFragment");
                startActivityForResult(i, LAUNCH_MAPS_ACTIVITY);
            }
        });

        tvAdress = view.findViewById(R.id.tv_Address);
        tvAdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (tvAdress.getText().length() <= 0) {
                    Toast.makeText(getPostActivity(), "Please select location", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvFromTime = view.findViewById(R.id.tv_fromTime);
        tvFromTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(getPostActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.HOUR_OF_DAY, sHour);
                                calendar.set(Calendar.MINUTE, sMinute);
                                fromTime = calendar.getTimeInMillis();
                                tvFromTime.setText("From :  " + sHour + ":" + sMinute);

                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        tvToTime = view.findViewById(R.id.tv_toTime);
        tvToTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(getPostActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.HOUR_OF_DAY, sHour);
                                calendar.set(Calendar.MINUTE, sMinute);
                                toTime = calendar.getTimeInMillis();
                                tvToTime.setText("To :  " + sHour + ":" + sMinute);

                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });
        return view;
    }

    private void createPost() {
        String title = et_Title.getText().toString().trim();
        String description = et_Description.getText().toString().trim();
        String phone = et_phoneNumber.getText().toString().trim();

        if (validateFields(filePath, title, description, selectedCategory, selected_Days, fromTime, toTime,
                tvAdress.getText().toString(), phone)) {
            Post post = new Post(title, description, selectedCategory, selectedLatLng.latitude, selectedLatLng.longitude, bitmap, fromTime, toTime, selected_Days, phone, false);
            addUser(filePath, post);
        }

    }

    public boolean checkPermission() {

        int result = ContextCompat.checkSelfPermission(getPostActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getPostActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getPostActivity(), Manifest.permission.CAMERA);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;

    }

    public void requestPermission() {

        ActivityCompat.requestPermissions(getPostActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSION_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case PERMISSION_CODE:

                if (grantResults.length > 0) {

                    boolean storage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameras = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (storage && cameras) {

                        Toast.makeText(getPostActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(getPostActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();

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

    private void showMsg(String s, DialogInterface.OnClickListener listener) {

        new AlertDialog.Builder(getPostActivity())
                .setMessage(s)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void OpenImages() {

        final CharSequence[] option = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getPostActivity());
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
        startActivityForResult(Intent.createChooser(intent, "Select File"), GALLERY);
    }

    private void CameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
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

        if (requestCode == LAUNCH_MAPS_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                double lat = data.getDoubleExtra("lat", 0);
                double lng = data.getDoubleExtra("lng", 0);
                setAddress(result, lat, lng);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public void openGalleryResult(Intent data) {

        Bitmap bitmap = null;

        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getPostActivity().getContentResolver(), data.getData());
                filePath = Utils.getImageUri(getActivity(), bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        selectedImage = bitmap;
        imageView.setImageBitmap(bitmap);

    }

    public void openCameraResult(Intent data) {

        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        filePath = Utils.getImageUri(getActivity(), bitmap);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File paths = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
        Toast.makeText(getPostActivity(), "Path -> " + paths.getAbsolutePath(), Toast.LENGTH_SHORT).show();
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
        selectedImage = bitmap;
        imageView.setImageBitmap(bitmap);
    }

    public void setAddress(String result, double lat, double lng) {
        tvAdress.setText(result);
        selectedLatLng = new LatLng(lat, lng);
    }


    private void addUser(Uri filepath, final Post post) {

        Utils.uploadImage(filepath, getActivity(), storageReference, new IClickListener() {

            @Override
            public void onFavClickListener(int pos) {

            }

            @Override
            public void onItemClickListener(int pos) {

            }

            @Override
            public void onUploadCompleteListener(String filepath) {

                String imageUrl = filepath;
                post.setImage(imageUrl);

                DatabaseReference presentersReference = mFirebaseDatabase.getReference(REFERENCE_POSTS);
                String presenterId = UUID.randomUUID().toString();
                post.setPostId(presenterId);
                post.setUserId(Utils.getCurrentUserId(getPostActivity()));
                presentersReference.child(presenterId).setValue(post, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            Fragment fragment = new HomeFragment();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.mainFrame, fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        } else {
                            // Failed to add presenter
                            Log.e("TAG", "Failed to add", databaseError.toException());
                        }
                    }
                });
            }
        });
    }

    private boolean validateFields(Uri image, String title, String description, String category, String selectedDays, Long fromTime,
                                   Long toTime, String location, String phoneNumber) {
        if (image == null) {
            showToast("Add photo to the post");
            return false;
        } else if (title.isEmpty()) {
            showToast("Title cannot be empty");
            return false;
        } else if (description.isEmpty()) {
            showToast("Description cannot be empty");
            return false;
        } else if (category.isEmpty()) {
            showToast("Category not selected");
            return false;
        } else if (selectedDays.isEmpty()) {
            showToast("Days not selected");
            return false;
        } else if (fromTime == 0) {
            showToast("Select from time");
            return false;
        } else if (toTime == 0) {
            showToast("Select to time");
            return false;
        } else if (location.isEmpty()) {
            showToast("Location not selected");
            return false;
        } else if (phoneNumber.isEmpty()) {
            showToast("Phone Number cannot be empty");
            return false;
        }
        return true;
    }

    private void showToast(String msg) {
        Toast.makeText(getPostActivity(), msg, Toast.LENGTH_LONG).show();
    }

    public String removeLastChar(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == ',') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }
}
