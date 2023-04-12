package edu.northeastern.pawpal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class AnimationStart extends AppCompatActivity {

    Animation keleStart;
    ImageView kele;
//    TextView slogan;
    SharedPreferences onBoardingScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_start);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();

        kele = findViewById(R.id.logo);

        keleStart = AnimationUtils.loadAnimation(this, R.anim.kele_ani);

        //set animations
        kele.setAnimation(keleStart);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onBoardingScreen = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);
                boolean isFirstTime = onBoardingScreen.getBoolean("firstTime", true);
                // TODO: insert login users
//                if (user == null) {
//                    startActivity(new Intent(SplashActivity.this, ReplacerActivity.class));
//
//                } else {
//                    startActivity(new Intent(SplashActivity.this, ExploreActivity.class));
//
//                }
                finish();
            }
        }, 2500);


    }
}