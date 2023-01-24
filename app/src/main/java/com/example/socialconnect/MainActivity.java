package com.example.socialconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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

    Fragment active;
    FrameLayout frameLayout;
    BottomNavigationView bottomNavigationView;
    FragmentManager fm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth=FirebaseAuth.getInstance();


        HomeFragment homeFragment=new HomeFragment();
        ProfileFragment profileFragment=new ProfileFragment();
        RequestFragment requestFragment=new RequestFragment();
        AskFragment askFragment=new AskFragment();


        active=homeFragment;

        frameLayout=findViewById(R.id.main_frame_layout);
        bottomNavigationView=findViewById(R.id.bottomNavigation);

        fm=getSupportFragmentManager();

        fm.beginTransaction().add(R.id.main_frame_layout,homeFragment).commit();
        fm.beginTransaction().add(R.id.main_frame_layout,profileFragment).hide(profileFragment).commit();
        fm.beginTransaction().add(R.id.main_frame_layout,requestFragment).hide(requestFragment).commit();
        fm.beginTransaction().add(R.id.main_frame_layout,askFragment).hide(askFragment).commit();


        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selected=null;
                switch (item.getItemId()){
                    case R.id.profile_bottom_nav:
                        fm.beginTransaction().hide(active).show(profileFragment).commit();
                        active=profileFragment;
                        break;
                    case R.id.home_bottom_nav:
                        fm.beginTransaction().hide(active).show(homeFragment).commit();
                        active=homeFragment;
                        break;
                    case R.id.ask_bottom_nav:
                        fm.beginTransaction().hide(active).show(askFragment).commit();
                        active=askFragment;
                        break;
                    case R.id.request_bottom_nav:
                        fm.beginTransaction().hide(active).show(requestFragment).commit();
                        active=requestFragment;
                        break;
                }


                return true;
            }
        });






    }
}