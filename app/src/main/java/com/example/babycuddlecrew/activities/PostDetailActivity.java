package com.example.babycuddlecrew.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.babycuddlecrew.R;
import com.example.babycuddlecrew.Utils;
import com.example.babycuddlecrew.domain.Post;
import com.example.babycuddlecrew.domain.User;
import com.example.babycuddlecrew.logic.LocationAddress;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.example.babycuddlecrew.Utils.REFERENCE_USERS;

public class PostDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView post_image;
    private TextView userName, title, desc, days, timings, address, showonMap;
    private Button btnContact;
    private Post selectedPost;
    public static final int REQUEST_PHONE_CALL = 201;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        post_image = findViewById(R.id.post_image);
        userName = findViewById(R.id.userName);
        title = findViewById(R.id.title);
        desc = findViewById(R.id.desc);
        days = findViewById(R.id.days);
        timings = findViewById(R.id.timings);
        address = findViewById(R.id.address);
        showonMap = findViewById(R.id.showonMap);
        btnContact = findViewById(R.id.btnContact);

        showonMap.setOnClickListener(this);
        btnContact.setOnClickListener(this);

        getBundleExtras();
        setPostData();
        if (!checkPermission()) {
            requestPermission();
        }
    }

    private void setPostData() {
        if (selectedPost != null) {
            Glide.with(this).load(selectedPost.getImage()).into(post_image);
            findUser(selectedPost.getUserId());
            title.setText(selectedPost.getTitle());
            desc.setText(selectedPost.getDescription());
            days.setText(selectedPost.getDays());
            timings.setText(Utils.getTimeFromMillis(selectedPost.getTimeFrom()).concat(" to ")
                    .concat(Utils.getTimeFromMillis(selectedPost.getTimeTo())));
            LocationAddress.getAddressFromLocation(selectedPost.getLatitude(), selectedPost.getLongitude(),
                    getApplicationContext(), new AddressHandler());
        }
    }

    @SuppressLint("HandlerLeak")
    private class AddressHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            if (message.what == 1) {
                Bundle bundle = message.getData();
                address.setText(bundle.getString("address"));
            }
        }
    }

    private void getBundleExtras() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey("selected_post")) {
                selectedPost = bundle.getParcelable("selected_post");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnContact:
                if (checkPermission()){
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + selectedPost.getPhoneNumber()));
                    startActivity(intent);
                }
                break;
            case R.id.showonMap:
                Intent i = new Intent(PostDetailActivity.this, SelectLocationActivity.class);
                i.putExtra("from", "PostDetailActivity");
                i.putExtra("fromLat", selectedPost.getLatitude());
                i.putExtra("fromLng", selectedPost.getLongitude());
                startActivity(i);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_CALL:
                if (grantResults.length > 0) {

                }
        }

    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
    }

    private void findUser(String userId) {
        DatabaseReference myDatabaseReference = FirebaseDatabase.getInstance().getReference(REFERENCE_USERS);
        Query usersQuery = myDatabaseReference.orderByChild("userId").equalTo(userId);
        usersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datas : snapshot.getChildren()) {
                    User user = datas.getValue(User.class);
                    userName.setText("Post created by: " + user.getFirstName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Item not found: ", "this item is not in the list");
            }
        });

    }
}
