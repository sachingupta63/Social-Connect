package com.example.socialconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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

public class LoginActivity extends AppCompatActivity {
    EditText emailEt,passEt;
    Button register_btn,login_btn;
    ProgressBar progress;
    CheckBox checkbox;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        emailEt=findViewById(R.id.login_email_id);
        passEt=findViewById(R.id.login_password_id);
        register_btn=findViewById(R.id.login_signup_button);
        login_btn=findViewById(R.id.login_login_button);
        progress=findViewById(R.id.login_progress);
        checkbox=findViewById(R.id.login_checkbox);

        auth=FirebaseAuth.getInstance();
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    passEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    passEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=emailEt.getText().toString();
                String pass=passEt.getText().toString();
                login_btn.setClickable(false);

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)){


                        progress.setVisibility(View.VISIBLE);
                        auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    sendToaMain();
                                    progress.setVisibility(View.INVISIBLE);
                                }else{
                                    progress.setVisibility(View.INVISIBLE);
                                    login_btn.setClickable(true);
                                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                }else{
                    login_btn.setClickable(true);
                    Toast.makeText(LoginActivity.this, "Please Enter Detail", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendToaMain(){
        startActivity(new Intent(LoginActivity.this,MainActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            sendToaMain();
        }
    }
}