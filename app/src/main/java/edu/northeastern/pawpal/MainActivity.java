package edu.northeastern.pawpal;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import androidx.recyclerview.widget.GridLayoutManager;

import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import edu.northeastern.pawpal.adapter.postAdapter;
import edu.northeastern.pawpal.model.singlePost;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private postAdapter adapter;
    private List<singlePost> postList;
    private RecyclerView recyclerView;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseStorage storage = FirebaseStorage.getInstance();


    public static String userId;
    public static boolean is_searched_user = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView;
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        return true;
                    case R.id.navigation_post:
                        Intent intentNewPost = new Intent(MainActivity.this, addNewPost.class);
                        startActivity(intentNewPost);
                        return true;
                    case R.id.navigation_nearby:
                        Intent intent = new Intent(MainActivity.this, NearbyActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_inbox:
                        Intent intent1 = new Intent(MainActivity.this, ChatListActivity.class);
                        startActivity(intent1);
                        return true;
                    case R.id.navigation_profile:
                        Intent intentP = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(intentP);
                        return true;
                }
                return false;
            }

        });

        initiate();
        loadDataFromFirebase();
    }

    private void initiate() {
        recyclerView = findViewById(R.id.recyclerView4);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        postList = new ArrayList<>();
//        Set<String> shownPostIds = new HashSet<>();
        mDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = mDatabase.getReference("users");

        adapter = new postAdapter(MainActivity.this, postList);
        recyclerView.setAdapter(adapter);
        adapter.OnPressed(new postAdapter.OnPressed() {
            @Override
            public void onLiked(int position, String postId, String uid, List<String> likeList, boolean isChecked) {

//                        DocumentReference reference = FirebaseFirestore.getInstance().collection("Users").document(uid).collection("Post Images").document(id);
                DatabaseReference reference = mDatabase.getInstance().getReference("posts").child(postId);

                if (likeList.contains(user.getUid())) {
                    likeList.remove(user.getUid()); // unlike
                } else {
                    likeList.add(user.getUid()); // like
                }
                Map<String, Object> map = new HashMap<>();

                map.put("likes", likeList);

                reference.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        adapter.notifyItemChanged(position); // 修正了更新 RecyclerView 的方法，改为 notifyItemChanged
                    }
                });
            }
        });
    }

    private void loadDataFromFirebase() {
        Set<String> shownPostIds = new HashSet<>();
        DatabaseReference postRef = mDatabase.getReference("posts");
        postRef.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    String postId = postRef.getKey();
                    System.out.println( "169 line" + postSnapshot);
                    String postId = postSnapshot.child("postId").getValue(String.class);
                    if (shownPostIds.contains(postId)) {
                        continue;
                    }
                    shownPostIds.add(postId);
//                    String username = postSnapshot.child("name").getValue(String.class);
//                    String content = postSnapshot.child("description").getValue(String.class);
////                    Log.d("test everything", userSnapshot.toString());
//                    String imageUrl = postSnapshot.child("postImageUrl").getValue(String.class);
//                    String profileUrl = postSnapshot.child("profileImageUrl").getValue(String.class);
//                    //
//                    List<String> likeCount = postSnapshot.child("likeCount").getValue((List<String>).this);
//                    Log.d("test like", Integer.toString(likeCount));
//                    String likeCount = "0";
//                    postList.add(0, new singlePost(username, content,imageUrl, profileUrl, likeCount, uid));
                    String username = postSnapshot.child("name").getValue(String.class);
                    String profileUrl = postSnapshot.child("profileImageUrl").getValue(String.class);
                    String imageUrl = postSnapshot.child("postImageUrl").getValue(String.class);
                    String uid = postSnapshot.child("uid").getValue(String.class);
                    String description = postSnapshot.child("description").getValue(String.class);
                    String id = postSnapshot.child("Id").getValue(String.class);
                    System.out.println( "169 line" + postSnapshot.child("timestamp"));
                    long timestamp = postSnapshot.child("timestamp").getValue(Long.class);

                    List<String> likeCount = new ArrayList<>();
                    for (DataSnapshot likeSnapshot : postSnapshot.child("likes").getChildren()) {
                        likeCount.add(likeSnapshot.getValue(String.class));
                    }
                    postList.add(0, new singlePost(username, postId, description,imageUrl, profileUrl, likeCount, uid));
                }
//                postAdapter adapter = new postAdapter(MainActivity.this, postList);
                adapter.notifyItemInserted(0);
                recyclerView.scrollToPosition(0);
                recyclerView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });

    }

    // TODO: refresh token
//    private void refreshToken() {
//        mMessaging.getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
//                            return;
//
//                        }
//
//                        String token = task.getResult();
//                        DatabaseReference userRef = mDatabase.getReference("users").child(user.getUid());
//                        userRef.child("FCMToken").setValue(token);
//                    }
//                });
//    }
//    public void updatePostLike(String postId, boolean isLiked, TextView likeCountTextView) {
//        DatabaseReference postRef = mDatabase.getReference("posts").child(postId);
//        postRef.runTransaction(new Transaction.Handler() {
//            @NonNull
//            @Override
//            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
//                singlePost post = mutableData.getValue(singlePost.class);
//                if (post == null) {
//                    return Transaction.success(mutableData);
//                }
//
//                // Update the like count
//                if (isLiked) {
//                    post.setLikeCount(post.getLikeCount() + 1);
//                } else {
//                    post.setLikeCount(post.getLikeCount() - 1);
//                }
//
//                // Update the TextView
//                likeCountTextView.setText(String.valueOf(post.getLikeCount()));
//
//                // Save the updated post to the database
//                mutableData.setValue(post);
//
//                return Transaction.success(mutableData);
//            }
//
//            @Override
//            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
//                // Handle the transaction completion
//                if (databaseError != null) {
//                    Log.d(TAG, "updatePostLike:onComplete: " + databaseError.getMessage());
//                }
//            }
//        });
//    }

    public void onChange(String uid) {
        userId = uid;
        is_searched_user = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatus(true);
    }

    @Override
    protected void onPause() {
        updateStatus(false);
        super.onPause();
    }

    void updateStatus(boolean status) {

        Map<String, Object> map = new HashMap<>();
        map.put("online", status);

    }
}