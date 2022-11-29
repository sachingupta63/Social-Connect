package com.example.socialconnect;

import androidx.annotation.NonNull;
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
import com.example.socialconnect.Model.StoryModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class StoryActivity extends AppCompatActivity {

    ImageView imageView;
    Button button;
    EditText editText;
    ProgressBar progressBar;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference AllStoryRef,UserStoryRef;
    UploadTask uploadTask;
    String postUri,url,name,type,curUid;
    FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
    StorageReference storageReference;

    FirebaseFirestore db=FirebaseFirestore.getInstance();
    DocumentReference documentReference;

    StoryModel storyModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        storyModel=new StoryModel();
        button=findViewById(R.id.btn_story_upload);
        imageView=findViewById(R.id.iv_story);
        editText=findViewById(R.id.et_story_up);
        progressBar=findViewById(R.id.pb_storyUp);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        curUid=user.getUid();

        documentReference=db.collection("user").document(curUid);

        storageReference=firebaseStorage.getReference("story");
        AllStoryRef=database.getReference("AllStory");
        UserStoryRef=database.getReference("story").child(curUid);

        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            postUri=bundle.getString("url");
        }else{
            Toast.makeText(this, "No Uri Received", Toast.LENGTH_SHORT).show();
        }

        Glide.with(this).load(postUri).into(imageView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadStory();
            }
        });

    }

    private void uploadStory() {
        String caption=editText.getText().toString();

        Calendar cdate=Calendar.getInstance();
        SimpleDateFormat currdate=new SimpleDateFormat("dd-MM-yyyy");
        final String savedate=currdate.format(cdate.getTime());

        Calendar ctime=Calendar.getInstance();
        SimpleDateFormat curTime=new SimpleDateFormat("HH:mm:ss");
        final String savetime=curTime.format(ctime.getTime());

        String time=savedate+":"+savetime;

        Uri uri=Uri.parse(postUri);

        if(TextUtils.isEmpty(caption) || uri==null){
            Toast.makeText(this, "All fields Required", Toast.LENGTH_SHORT).show();
        }else{
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(uri));

            uploadTask=reference.putFile(uri);

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

                        long timeEnd=System.currentTimeMillis()+8640000;
                        storyModel.setCaption(caption);
                        storyModel.setName(name);
                        storyModel.setUrl(url);
                        storyModel.setPostUri(downloadUri.toString());
                        storyModel.setUid(curUid);
                        storyModel.setTimeUpload(time);
                        storyModel.setTimeEnd(timeEnd);

                        if(uri.toString().contains("png") || uri.toString().contains("jpg")){
                            type="iv";
                        }
                        storyModel.setType(type);

                        //User Story
                        String key=UserStoryRef.push().getKey();
                        UserStoryRef.child(key).setValue(storyModel);

                        //All Story
                        AllStoryRef.child(curUid).setValue(storyModel);

                        progressBar.setVisibility(View.GONE );

                        Toast.makeText(StoryActivity.this, "Story Uploaded", Toast.LENGTH_SHORT).show();


                    }

                }
            });
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getMimeTypeFromExtension(contentResolver.getType(uri));
    }

    @Override
    protected void onStart() {
        super.onStart();
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()) {
                    name = task.getResult().getString("name");
                    url= task.getResult().getString("url");




                }else{
                    Toast.makeText(StoryActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}