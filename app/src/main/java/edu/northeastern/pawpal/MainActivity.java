package edu.northeastern.pawpal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private FirebaseDatabase mDatabase;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public static String userId;
    public static boolean is_searched_user = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //test loginActivity
//        Button buttonlogtest = findViewById(R.id.login_test);
//        buttonlogtest.setOnClickListener(view -> {
//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//        });

        BottomNavigationView bottomNavigationView;
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        return true; // TODO: navigate to Home
                    case R.id.navigation_post:
//                        startActivity(new Intent(MainActivity.this, AnimationStart.class));
                        return true; // TODO: navigate to Post
                    case R.id.navigation_nearby:
                        Intent intent = new Intent(MainActivity.this, NearbyActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_inbox:
                        return true; // TODO: navigate to Inbox
                    case R.id.navigation_profile:
                        return true; // TODO: navigate to Profile
                }
                return false;
            }

        });

        initiate();
        setInitiatePostView(savedInstanceState);
    }

    private void initiate() {

        viewPager = findViewById(R.id.viewPager);
        mDatabase = FirebaseDatabase.getInstance();

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

    private void setInitiatePostView(Bundle savedInstanceState) {
//        if (savedInstanceState != null && savedInstanceState.containsKey("init_view_pager_item")) {
//            Bundle extras = getIntent().getExtras();
//            if(extras == null) {
//                newString= null;
//            } else {
//                newString= extras.getString("STRING_I_NEED");
//            }
//            int pos = savedInstanceState.
//            viewPager.setCurrentItem(pos);
//        }

        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.containsKey("init_view_pager_item")) {
            viewPager.setCurrentItem(extras.getInt("init_view_pager_item"));
        }
    }

// TODO: load profile image
//    private Bitmap loadProfileImage(String directory) {
//
//        try {
//            File file = new File(directory, "profile.png");
//
//            return BitmapFactory.decodeStream(new FileInputStream(file));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//
//    }

    public void onChange(String uid) {
        userId = uid;
        is_searched_user = true;
        viewPager.setCurrentItem(4);
    }

    @Override
    public void onBackPressed() {

        if (viewPager.getCurrentItem() == 4) {
            viewPager.setCurrentItem(0);
            is_searched_user = false;
        } else
            super.onBackPressed();
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

        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(user.getUid())
                .update(map);
    }
}