package com.example.socialconnect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.socialconnect.Fragments.ProfileFragment;
import com.example.socialconnect.Model.PostModel;
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

public class PostActivity extends AppCompatActivity {

    ImageView imageView;
    ProgressBar progressBar;
    private Uri selectedUri;
    private static final int PICK_FILE=1;
    UploadTask uploadTask;
    EditText etDesc;
    Button btnChooseFile,btnUploadFile;
    VideoView videoView;
    String url,name;
    StorageReference storageReference;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference db1,db2,db3;

    MediaController mediaController;
    String type;

    PostModel postModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mediaController=new MediaController(this);

        progressBar=findViewById(R.id.pb_post);
        imageView=findViewById(R.id.iv_post);
        videoView=findViewById(R.id.vv_post);
        btnChooseFile=findViewById(R.id.btn_choose_file_post);
        btnUploadFile=findViewById(R.id.btn_upload_file_post);
        etDesc=findViewById(R.id.et_desc_post);

        postModel=new PostModel();

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String curUid=user.getUid();

        storageReference= FirebaseStorage.getInstance().getReference("UserPosts");


        db1=database.getReference("AllImages").child(curUid);
        db2=database.getReference("AllVideos").child(curUid);
        db3=database.getReference("AllPosts");



        btnChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFile();

            }
        });

        btnUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doPost();
            }
        });
    }

    private void chooseFile() {
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/* video/*");
        startActivityForResult(intent,PICK_FILE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
try {


    if (requestCode == PICK_FILE || resultCode == RESULT_OK || data != null || data.getData() != null) {
        selectedUri = data.getData();
        if (selectedUri.toString().contains("jpg") || selectedUri.toString().contains("png")) {
            Glide.with(this).load(selectedUri).into(imageView);
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.INVISIBLE);
            type = "iv";
        } else if (selectedUri.toString().contains("mp3") || selectedUri.toString().contains("mp4")) {
            videoView.setMediaController(mediaController);
            videoView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            videoView.setVideoURI(selectedUri);
            videoView.start();
            type = "vv";

        } else {
            Toast.makeText(this, "Not Supported such type of file", Toast.LENGTH_SHORT).show();
        }
    }
}catch (Exception e){

}
    }

    //selected file jpeg || mpeg
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getMimeTypeFromExtension(contentResolver.getType(uri));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String curUid=user.getUid();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference documentReference=db.collection("user").document(curUid);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()) {
                    name = task.getResult().getString("name");
                    url= task.getResult().getString("url");

                }else{
                    Toast.makeText(PostActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void doPost(){

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String curUid=user.getUid();

        String desc=etDesc.getText().toString();

        Calendar cdate=Calendar.getInstance();
        SimpleDateFormat currdate=new SimpleDateFormat("dd-MM-yyyy");
        final String savedate=currdate.format(cdate.getTime());

        Calendar ctime=Calendar.getInstance();
        SimpleDateFormat curTime=new SimpleDateFormat("HH:mm:ss");
        final String savetime=curTime.format(ctime.getTime());

        String time=savedate+":"+savetime;

        if(!TextUtils.isEmpty(desc) && selectedUri!=null){

            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(selectedUri));

            uploadTask=reference.putFile(selectedUri);

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

                        if(type.equals("iv")){
                            postModel.setDesc(desc);
                            postModel.setName(name);
                            postModel.setPostUri(downloadUri.toString());
                            postModel.setTime(time);
                            postModel.setUid(curUid);
                            postModel.setUrl(url);
                            postModel.setType(type);

                            String id=db1.push().getKey();
                            db1.child(id).setValue(postModel);

                            String id1=db3.push().getKey();
                            db3.child(id1).setValue(postModel);

                            Toast.makeText(PostActivity.this, "POst Uploaded", Toast.LENGTH_SHORT).show();


                        }else if(type.equals("vv")){
                            postModel.setDesc(desc);
                            postModel.setName(name);
                            postModel.setPostUri(downloadUri.toString());
                            postModel.setTime(time);
                            postModel.setUid(curUid);
                            postModel.setUrl(url);
                            postModel.setType(type);

                            //for image
                            String id3=db2.push().getKey();
                            db2.child(id3).setValue(postModel);

                            //fpr both
                            String id4=db3.push().getKey();
                            db3.child(id4).setValue(postModel);
                            Toast.makeText(PostActivity.this, "Post Uploaded", Toast.LENGTH_SHORT).show();

                        }else{
                            Toast.makeText(PostActivity.this, "error", Toast.LENGTH_SHORT).show();
                        }

                        progressBar.setVisibility(View.INVISIBLE);


                    }

                }
            });
        }else{
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
        }


    }
}