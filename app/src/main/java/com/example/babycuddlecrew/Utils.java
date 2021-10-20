package com.example.babycuddlecrew;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.babycuddlecrew.domain.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

public class Utils {
    public static int CAMERA = 1, GALLERY = 2;
    public static final int PERMISSION_CODE = 111;
    public static final int UPDATE_USER = 123;
    public static final String REFERENCE_USERS = "users";
    public static final String REFERENCE_POSTS = "posts";
    public static final String PREF_LOGIN_USER = "pref_login_user";
    public static final String APP_PREFERENCES = "app_preferences";

    public static void uploadImage(Uri filePath, final Context context, StorageReference storageReference, final IClickListener clickListener) {
        if (filePath != null) {
            final ProgressDialog progress = new ProgressDialog(context);
            progress.setTitle("Uploading....");
            progress.show();

            final StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progress.dismiss();
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
//                            Toast.makeText(context, "" + uri, Toast.LENGTH_LONG).show();
                            Log.d("uri", "" + uri);
                            clickListener.onUploadCompleteListener(uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progress.dismiss();
                    Toast.makeText(context, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progres_time = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progress.setMessage("Uploaded " + (int) progres_time + " %");
                }
            });
        }
    }

    public static Uri getImageUri(Activity inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static User getCurrentUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        User user = new Gson().fromJson(prefs.getString(PREF_LOGIN_USER, null), User.class);
        return user;
    }

    public static String getCurrentUserId(Context context) {
        String id = "";
        SharedPreferences prefs = context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        User user = new Gson().fromJson(prefs.getString(PREF_LOGIN_USER, null), User.class);
        if (user != null) {
            id = user.getUserId();
        }
        return id;
    }

    public static String getCurrentUserCategory(Context context) {
        String category = "";
        SharedPreferences prefs = context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        User user = new Gson().fromJson(prefs.getString(PREF_LOGIN_USER, null), User.class);
        if (user != null) {
            category = user.getRole();
        }
        return category;
    }

    public static ArrayList<String> getCurrentUserFavoritePosts(Context context) {
        ArrayList<String> favoritePosts = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        User user = new Gson().fromJson(prefs.getString(PREF_LOGIN_USER, null), User.class);
        if (user != null) {
            String[] fp = user.getFavoritePosts().split(",");
            favoritePosts = new ArrayList<String>(Arrays.asList(fp));
        }
        return favoritePosts;
    }

    public static String getStringFavPosts(ArrayList<String> posts) {
        String favPosts = "";
        if (posts != null) {
            for (int i = 0; i < posts.size(); i++) {
                String postId = posts.get(i);
                favPosts = !favPosts.isEmpty() ? favPosts.concat(",").concat(postId) : favPosts.concat(postId);
            }
        }
        return favPosts;
    }

    public static List<String> getCategoryItems(Context context) {
        List<String> catItems = new ArrayList<>();
        switch (getCurrentUserCategory(context)) {
            case Constants.PARENT:
                catItems.add(0, Constants.SELECT_CATEGORY);
                catItems.add(1, Constants.BABY_SITTER);
                catItems.add(2, Constants.HOUSE_KEEPING);
                catItems.add(3, Constants.COOKING);
                break;
            case Constants.BABY_SITTER:
                catItems.add(0, Constants.SELECT_CATEGORY);
                catItems.add(1, Constants.PARENT);
                catItems.add(2, Constants.HOUSE_KEEPING);
                catItems.add(3, Constants.COOKING);
                break;
            default:
                catItems.add(0, Constants.SELECT_CATEGORY);
                catItems.add(1, Constants.PARENT);
                catItems.add(2, Constants.PARENT);
                catItems.add(3, Constants.HOUSE_KEEPING);
                catItems.add(4, Constants.COOKING);
                break;
        }
        return catItems;
    }

    public static void setUser(User obj, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        if (obj == null) {
            prefs.edit().putString(Utils.PREF_LOGIN_USER, "").apply();
        } else {
            prefs.edit().putString(Utils.PREF_LOGIN_USER, new Gson().toJson(obj)).apply();
        }
    }

    public static String getTimeFromMillis(long time){
        String strTime = "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        strTime = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)).concat(":")
                .concat(String.valueOf(calendar.get(Calendar.MINUTE)));
        return strTime;
    }
}
