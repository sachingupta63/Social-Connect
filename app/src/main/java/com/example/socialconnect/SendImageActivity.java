package com.example.socialconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.socialconnect.Fragments.ProfileFragment;
import com.example.socialconnect.Model.MessageModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SendImageActivity extends AppCompatActivity {

    String url,receiver_name,receiver_uid,sender_uid;
    ImageView imageView;
    Uri imgUri;
    ProgressBar progressBar;
    Button button;
    TextView textView;
    UploadTask uploadTask;

    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    DatabaseReference rootref1,rootref2;
    FirebaseDatabase database=FirebaseDatabase.getInstance();

    MessageModel messageModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_image);

        messageModel=new MessageModel();
        storageReference=firebaseStorage.getInstance().getReference("MessageImages");
        imageView=findViewById(R.id.iv_sendImage);
        button=findViewById(R.id.btn_sendImage);
        progressBar=findViewById(R.id.progress_bar_sendImage);
        textView=findViewById(R.id.tv_dont);

        Bundle extras=getIntent().getExtras();

        if(extras!=null){

            url=extras.getString("url");
            receiver_name=extras.getString("name");
            receiver_uid= extras.getString("ruid");
            sender_uid= extras.getString("suid");


        }else{
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }

        Glide.with(this).load(url).into(imageView);
        imgUri=Uri.parse(url);
        rootref1=database.getReference("message").child(sender_uid).child(receiver_uid);
        //retrieve
        rootref2=database.getReference("message").child(receiver_uid).child(sender_uid);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendImage();
                textView.setVisibility(View.VISIBLE);

            }
        });
    }

    private void sendImage() {
        if(imgUri!=null){
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
                    if(task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        Calendar cdate=Calendar.getInstance();
                        SimpleDateFormat currdate=new SimpleDateFormat("dd-MM-yyyy");
                        final String savedate=currdate.format(cdate.getTime());

                        Calendar ctime=Calendar.getInstance();
                        SimpleDateFormat curTime=new SimpleDateFormat("HH:mm:ss");
                        final String savetime=curTime.format(ctime.getTime());

                        messageModel.setMessage(downloadUri.toString());
                        messageModel.setTime(savetime);
                        messageModel.setDate(savedate);
                        messageModel.setSendUid(sender_uid);
                        messageModel.setReceiverUid(receiver_uid);
                        messageModel.setType("iv");

                        String id = rootref1.push().getKey();
                        rootref1.child(id).setValue(messageModel);

                        String id1 = rootref2.push().getKey();
                        rootref2.child(id1).setValue(messageModel);

                        progressBar.setVisibility(View.INVISIBLE);

                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                    startActivity(new Intent(SendImageActivity.this,MessageActivity.class));
                            }
                        },1500);
                    }
                }
            });
        }else{
            Toast.makeText(this, "Select Select Image", Toast.LENGTH_SHORT).show();
        }
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getMimeTypeFromExtension(contentResolver.getType(uri));
    }
}