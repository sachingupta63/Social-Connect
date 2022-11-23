package com.example.socialconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView imageView;
    TextView nameTv,descTv;
    long animTime=2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        imageView=findViewById(R.id.splash_logo);
        nameTv=findViewById(R.id.splash_tv);
        descTv=findViewById(R.id.splash_tv_description);

        ObjectAnimator animatorLogo=ObjectAnimator.ofFloat(imageView,"y",400f);
        ObjectAnimator animatorname=ObjectAnimator.ofFloat(nameTv,"x",200f);
        animatorLogo.setDuration(animTime);
        animatorname.setDuration(animTime);
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.playTogether(animatorLogo,animatorname);
        animatorSet.start();


        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this,LoginActivity.class));
                finish();
            }
        },4000);




    }


}