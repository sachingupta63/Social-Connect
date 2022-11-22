package com.example.socialconnect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

public class UpdateProfileActivity extends AppCompatActivity {
    EditText etname,etBio,etProfession,etEmail,etWeb;
    Button button;
    FirebaseDatabase database;
    DatabaseReference reference;
    DocumentReference documentReference;
    FirebaseFirestore firestore;
    String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        database=FirebaseDatabase.getInstance();
        firestore=FirebaseFirestore.getInstance();

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        currentUid=user.getUid();
        documentReference=firestore.collection("user").document(currentUid);



        etBio=findViewById(R.id.et_bio_upa);
        etname=findViewById(R.id.et_name_upa);
        etProfession=findViewById(R.id.et_profession_upa);
        etEmail=findViewById(R.id.et_email_upa);
        etWeb=findViewById(R.id.et_website_upa);

        button=findViewById(R.id.btn_save_upa);

        button.setOnClickListener((View view)->{
            updateProfile();
        });

    }

    private void updateProfile() {
        String name=etname.getText().toString();
        String bio=etBio.getText().toString();
        String prof=etProfession.getText().toString();
        String web=etWeb.getText().toString();
        String email=etEmail.getText().toString();

        final DocumentReference dtoUdate=firestore.collection("user").document(currentUid);
        
        firestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                transaction.update(dtoUdate,"name",name);
                transaction.update(dtoUdate,"prof",prof);
                transaction.update(dtoUdate,"email",email);
                transaction.update(dtoUdate,"web",web);
                transaction.update(dtoUdate,"bio",bio);

                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(UpdateProfileActivity.this, "Update", Toast.LENGTH_SHORT).show();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });


    }

    //Load Previous Data when Activity started
    @Override
    protected void onStart() {
        super.onStart();



        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()) {
                    String nameResult = task.getResult().getString("name");
                    String bioResult = task.getResult().getString("bio");
                    String emailResult = task.getResult().getString("email");
                    String webResult = task.getResult().getString("web");
                    String urlResult = task.getResult().getString("url");
                    String profResult = task.getResult().getString("prof");


                    etname.setText(nameResult);
                    etBio.setText(bioResult);
                    etEmail.setText(emailResult);
                    etWeb.setText(webResult);
                    etProfession.setText(profResult);
                }else{
                    Toast.makeText(UpdateProfileActivity.this, "No Profile Exist", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}