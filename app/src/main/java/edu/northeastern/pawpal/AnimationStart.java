package edu.northeastern.pawpal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class AnimationStart extends AppCompatActivity {

    ImageView kele;
//    TextView slogan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_start);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();

        kele = findViewById(R.id.logo);

        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setStartOffset(1000);
        fadeIn.setDuration(1000);
        fadeIn.setFillAfter(true);
        kele.startAnimation(fadeIn);

        //set animations
        //kele.setAnimation(keleStart);

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // wait 2 seconds after animation end before jumping into main activity
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

//                if (user == null) {
                    startActivity(new Intent(AnimationStart.this, LoginActivity.class));

//                } else {
//                    startActivity(new Intent(AnimationStart.this, MainActivity.class));

//                }
                        finish();
                    }
                }, 2000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                onBoardingScreen = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);
//                boolean isFirstTime = onBoardingScreen.getBoolean("firstTime", true);
//                // TODO: insert login users
////                if (user == null) {
////                    startActivity(new Intent(SplashActivity.this, ReplacerActivity.class));
////
////                } else {
////                    startActivity(new Intent(SplashActivity.this, ExploreActivity.class));
////
////                }
//                finish();
//            }
//        }, 2500);
    }
}