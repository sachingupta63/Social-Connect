package com.example.socialconnect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.socialconnect.Fragments.ProfileFragment;
import com.example.socialconnect.Model.UserProfile;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;


public class CreateProfileActivity extends AppCompatActivity {

    EditText etname,etBio,etProfession,etEmail,etWeb;
    Button btnSave;
    ImageView imgProfile;
    ProgressBar progressBar;
    Uri imgUri;
    UploadTask uploadTask;
    StorageReference storageReference;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseFirestore firestoreStorage;
    DocumentReference documentReference;
    private static final int PICK_IMAGE=1;

    UserProfile userProfile;
    String currentuserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        userProfile=new UserProfile();

        etname=findViewById(R.id.et_name_cp);
        etBio=findViewById(R.id.et_bio_cp);
        etProfession=findViewById(R.id.et_profession_cp);
        etEmail=findViewById(R.id.et_email_cp);
        etWeb=findViewById(R.id.et_website_cp);
        imgProfile=findViewById(R.id.iv_cp);

        btnSave=findViewById(R.id.btn_save_cp);

        progressBar=findViewById(R.id.progress_cp);

        database=FirebaseDatabase.getInstance();
        firestoreStorage=FirebaseFirestore.getInstance();

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        currentuserId=user.getUid();

        documentReference=firestoreStorage.collection("user").document(currentuserId);
        storageReference= FirebaseStorage.getInstance().getReference("Profile images");

        databaseReference=database.getReference("All Users");

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData();
            }
        });

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,PICK_IMAGE);

            }
        });




    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getMimeTypeFromExtension(contentResolver.getType(uri));
    }
    public void uploadData(){

        String name=etname.getText().toString();
        String bio=etBio.getText().toString();
        String web=etBio.getText().toString();
        String prof=etProfession.getText().toString();
        String email=etEmail.getText().toString();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(bio) && !TextUtils.isEmpty(web) &&
                !TextUtils.isEmpty(prof) && !TextUtils.isEmpty(email) && imgUri!=null ){
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imgUri));

            uploadTask=reference.putFile(imgUri);

            Task<Uri> urlTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }

                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri=task.getResult();

                        Map<String,String> profile=new HashMap<>();
                        profile.put("name",name);
                        profile.put("prof",prof);
                        profile.put("url",downloadUri.toString());
                        profile.put("email",email);
                        profile.put("web",web);
                        profile.put("bio",bio);
                        profile.put("uid",currentuserId);
                        profile.put("privacy","Public");

                        userProfile.setName(name);
                        userProfile.setProf(prof);
                        userProfile.setUrl(downloadUri.toString());
                        userProfile.setUid(currentuserId);

                        databaseReference.child(currentuserId).setValue(userProfile);
                        documentReference.set(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(CreateProfileActivity.this, "Profile Created", Toast.LENGTH_SHORT).show();

                                Handler handler=new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent=new Intent(CreateProfileActivity.this, ProfileFragment.class);
                                        startActivity(intent);
                                    }
                                },2000);
                            }
                        });

                    }

                }
            });
        }else{
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
        }



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PICK_IMAGE || requestCode == RESULT_OK || data != null || data.getData() != null) {
                imgUri = data.getData();
                Glide.with(this).load(imgUri).into(imgProfile);
            }
        }catch (Exception e){
            Toast.makeText(this, "No File Selected", Toast.LENGTH_SHORT).show();
        }
    }
}