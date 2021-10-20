package com.example.babycuddlecrew.activities.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babycuddlecrew.Constants;
import com.example.babycuddlecrew.IClickListener;
import com.example.babycuddlecrew.R;
import com.example.babycuddlecrew.Utils;
import com.example.babycuddlecrew.activities.PostDetailActivity;
import com.example.babycuddlecrew.adapters.PostAdapter;
import com.example.babycuddlecrew.domain.Post;
import com.example.babycuddlecrew.domain.User;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.babycuddlecrew.Utils.REFERENCE_USERS;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements IClickListener {

    View view;

    RecyclerView recyclerView;
    private ArrayList<Post> allPostsList;
    private PostAdapter postAdapter;
    public static final String REFERENCE_POSTS = "posts";
    private DatabaseReference postsQuery;
    private DatabaseReference mDatabase;
    private AdView adView;

    public HomeFragment() {
        // Required empty public constructor
    }

    private FragmentActivity getHomeFragActivity() {
        return getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mDatabase = FirebaseDatabase.getInstance().getReference();
        postsQuery = mDatabase.child(REFERENCE_POSTS);
        allPostsList = new ArrayList<>();
        setPostAdapter();
        getAllPostsList();

        //ad
        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        adView = view.findViewById(R.id.ad_view);
        // Create an ad request.
        AdRequest adRequest = new AdRequest.Builder().build();
        // Start loading the ad in the background.
        adView.loadAd(adRequest);
        return view;
    }

    private void setPostAdapter() {
        postAdapter = new PostAdapter(getActivity(), allPostsList, this);
        recyclerView.setAdapter(postAdapter);
    }


    @Override
    public void onFavClickListener(int pos) {
        ArrayList<String> favoritePosts = Utils.getCurrentUserFavoritePosts(getHomeFragActivity());
        String clickedPostId = allPostsList.get(pos).getPostId();
        if (favoritePosts.contains(clickedPostId)) {
            favoritePosts.remove(clickedPostId);
        } else {
            favoritePosts.add(clickedPostId);
        }
        DatabaseReference userRef = mDatabase.child(REFERENCE_USERS).child(Utils.getCurrentUserId(getHomeFragActivity()));
        userRef.child("favoritePosts").setValue(Utils.getStringFavPosts(favoritePosts));
        updateUserData(userRef, pos);
    }

    @Override
    public void onItemClickListener(int pos) {
        Intent intent = new Intent(getHomeFragActivity(), PostDetailActivity.class);
        intent.putExtra("selected_post", allPostsList.get(pos));
        startActivity(intent);
    }

    @Override
    public void onUploadCompleteListener(String filepath) {

    }

    public void updateUserData(DatabaseReference userRef, final int pos) {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Utils.setUser(user, getHomeFragActivity());
                postAdapter.notifyItemChanged(pos);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Item not found: ", "this item is not in the list");
            }
        });
    }

    public void getAllPostsList() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(REFERENCE_POSTS);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (allPostsList == null) {
                    allPostsList = new ArrayList<>();
                }
                allPostsList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null && !post.getCategory().equalsIgnoreCase(Utils.getCurrentUserCategory(getContext()))) {
                        allPostsList.add(post);
                    }
                }
                if (postAdapter == null) {
                    setPostAdapter();
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });
    }

    public void getPostsListByCategory(String category) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(REFERENCE_POSTS);
        Query postQuery = mDatabase.orderByChild("category").equalTo(category);
        postQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (allPostsList == null) {
                    allPostsList = new ArrayList<>();
                }
                allPostsList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    allPostsList.add(post);
                }
                if (postAdapter == null) {
                    setPostAdapter();
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_tool, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.babySitter:
                getPostsListByCategory(Constants.BABY_SITTER);
                break;
            case R.id.parent:
                getPostsListByCategory(Constants.PARENT);
                break;
            case R.id.cooking:
                getPostsListByCategory(Constants.COOKING);
                break;
            case R.id.houseKeeping:
                getPostsListByCategory(Constants.HOUSE_KEEPING);
                break;
            case R.id.clear:
            default:
                getAllPostsList();
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
