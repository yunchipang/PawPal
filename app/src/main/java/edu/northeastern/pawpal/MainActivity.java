package edu.northeastern.pawpal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
//import android.widget.Button;
//import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

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
                        return true; // TODO: navigate to Home
                    case R.id.navigation_post:
                        startActivity(new Intent(MainActivity.this, AnimationStart.class));
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
    }
}