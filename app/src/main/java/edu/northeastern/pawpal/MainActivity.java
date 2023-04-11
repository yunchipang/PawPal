package edu.northeastern.pawpal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button nearbyButton = findViewById(R.id.nearbyButton);
        nearbyButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NearbyActivity.class);
            startActivity(intent);
        });
    }
}