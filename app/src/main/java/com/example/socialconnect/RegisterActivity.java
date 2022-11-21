package com.example.socialconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    EditText emailEt,passEd,conf_pass;
    Button register_btn,login_btn;
    ProgressBar register_progress;
    CheckBox register_chk;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEt=findViewById(R.id.register_email_id);
        passEd=findViewById(R.id.register_password_id);
        conf_pass=findViewById(R.id.register_confirm_password_id);
        register_btn=findViewById(R.id.register_signup_button);
        login_btn=findViewById(R.id.register_login_button);
        register_progress=findViewById(R.id.register_progress);
        register_chk=findViewById(R.id.register_checkbox);

        auth=FirebaseAuth.getInstance();

        register_chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    passEd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    conf_pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                }else{
                    passEd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    conf_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=emailEt.getText().toString();
                String pass=passEd.getText().toString();
                String conf_password=conf_pass.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(conf_password)){

                    if(pass.equals(conf_password)){
                        register_progress.setVisibility(View.VISIBLE);
                        auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    sendToaMain();
                                    register_progress.setVisibility(View.INVISIBLE);
                                }else{
                                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    register_progress.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }else{
                        register_progress.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegisterActivity.this, "Password and Confirm Password Is Not Matching", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(RegisterActivity.this, "Please Enter Detail", Toast.LENGTH_SHORT).show();
                }
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                //finish();
            }
        });


    }


    private void sendToaMain(){
        startActivity(new Intent(RegisterActivity.this,MainActivity.class));
        finish();
    }

    @Override
    protected  void onStart() {
        super.onStart();

        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser!=null){
            sendToaMain();
        }

    }

}