package com.example.socialconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialconnect.Fragments.AskFragment;
import com.example.socialconnect.Fragments.HomeFragment;
import com.example.socialconnect.Fragments.ProfileFragment;
import com.example.socialconnect.Fragments.RequestFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;

    FrameLayout frameLayout;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();


        frameLayout=findViewById(R.id.main_frame_layout);
        bottomNavigationView=findViewById(R.id.bottomNavigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,new HomeFragment()).commit();


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selected=null;
                switch (item.getItemId()){
                    case R.id.profile_bottom_nav:
                        selected=new ProfileFragment();
                        break;
                    case R.id.home_bottom_nav:
                        selected=new HomeFragment();
                        break;
                    case R.id.ask_bottom_nav:
                        selected=new AskFragment();
                        break;
                    case R.id.request_bottom_nav:
                        selected=new RequestFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,selected).commit();

                return true;
            }
        });






    }
}