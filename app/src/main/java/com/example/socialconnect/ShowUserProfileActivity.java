package com.example.socialconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.socialconnect.Model.RequestModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ShowUserProfileActivity extends AppCompatActivity {

    TextView nameTv,professionTv,bioTv,emailTv,websiteTv,errorTv;
    ImageView imageView;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference databaseReferenceRequests,databaseReferenceFollowers,databaseReference2,postnoref,requestRef;
    Button btnFollow;
    TextView followersTv,postsTv;
    String url,name,age,email,privacy,p,website,bio,userid;
    RequestModel requestModel;
    String nameReq,urlReq,professionReq;
    LinearLayout follower_lyt,post_lyt;

    int postNo=0;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    DocumentReference documentReference,documentReference1;

    int follwerCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_profile);

        requestModel=new RequestModel();
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String curUid=user.getUid();

        nameTv=findViewById(R.id.tv_name_show_user);
        professionTv=findViewById(R.id.tv_profession_show_user);
        bioTv=findViewById(R.id.tv_bio_show_user);
        emailTv=findViewById(R.id.tv_email_show_user);
        imageView=findViewById(R.id.iv_profile_show_user);
        websiteTv=findViewById(R.id.tv_website_show_user);
        btnFollow=findViewById(R.id.btn_fol_unfol_show_user);
        errorTv=findViewById(R.id.tv_error_show_user);

        followersTv=findViewById(R.id.tv_followers_show_user);
        postsTv=findViewById(R.id.tv_post_show_user);
        follower_lyt=findViewById(R.id.lauout_followers_show_user);
        post_lyt=findViewById(R.id.layout_posts_show_user);

        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            url=extras.getString("url");
            name=extras.getString("name");
            userid=extras.getString("userid");

        }else{
            Toast.makeText(this, "Private Account", Toast.LENGTH_SHORT).show();
        }

        databaseReferenceRequests=database.getReference("Requests").child(userid);
        databaseReferenceFollowers=database.getReference("Followers").child(userid);
        documentReference=db.collection("user").document(userid);
        postnoref=database.getReference("AllPosts");
        databaseReference2=database.getReference("Followers");
        documentReference1=db.collection("user").document(curUid);


        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status=btnFollow.getText().toString();
                if(status.equals("Follow")){
                    follow();
                }else if(status.equals("Requested")){
                    delRequest();
                }else if(status.equals("Following")){
                    unFollow();
                }

            }
        });


    }

    private void delRequest(){
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        String curUid=user.getUid();
        databaseReferenceRequests.child(curUid).removeValue();
        btnFollow.setText("Follow");
        errorTv.setVisibility(View.GONE);
    }

    private void follow(){
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        String curUid=user.getUid();

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    String privacy=task.getResult().getString("privacy");
                    RequestModel requestModel=new RequestModel();
                    requestModel.setName(name);
                    requestModel.setUrl(url);
                    requestModel.setUserid(curUid);
                    if(privacy.equals("Public")){

                        databaseReferenceFollowers.child(curUid).setValue(requestModel);
                        btnFollow.setText("Following");
                    }else{
                        databaseReferenceRequests.child(curUid).setValue(requestModel);
                        btnFollow.setText("Requested");
                        errorTv.setVisibility(View.VISIBLE);
                        errorTv.setText("Wait for User to accept");
                    }
                }else{
                    Toast.makeText(ShowUserProfileActivity.this, "follow btn error", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void unFollow(){
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        String curUid=user.getUid();
        databaseReferenceFollowers.child(curUid).removeValue();
        btnFollow.setText("Follow");

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        String curUid=user.getUid();

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.getResult().exists()){
                    String name_result=task.getResult().getString("name");
                    String age_reult=task.getResult().getString("prof");
                    String bio_result=task.getResult().getString("bio");
                    String email_result=task.getResult().getString("email");
                    String web_result = task.getResult().getString("web");
                    String url_result=task.getResult().getString("url");
                    String p=task.getResult().getString("privacy");

                    if(p.equals("Public")){
                        professionTv.setText(bio_result);
                        nameTv.setText(name_result);
                        bioTv.setText(age_reult);
                        emailTv.setText(email_result);
                        websiteTv.setText(web_result);
                        Glide.with(ShowUserProfileActivity.this).load(url).into(imageView);
                        errorTv.setVisibility(View.GONE);

                    }else{
                        String u=btnFollow.getText().toString();
                        if(u.equals("Following")){
                            professionTv.setText(bio_result);
                            nameTv.setText(name_result);
                            bioTv.setText(age_reult);
                            emailTv.setText(email_result);
                            websiteTv.setText(web_result);
                            Glide.with(ShowUserProfileActivity.this).load(url).into(imageView);
                            errorTv.setVisibility(View.GONE);
                        }else{
                            professionTv.setText("******");
                            nameTv.setText(name_result);
                            bioTv.setText("******");
                            emailTv.setText("******");
                            websiteTv.setText("******");
                            Glide.with(ShowUserProfileActivity.this).load(url).into(imageView);
                            errorTv.setText("Wait for User to accept");
                           errorTv.setVisibility(View.VISIBLE);
                        }
                    }

                }else{
                    Toast.makeText(ShowUserProfileActivity.this, "No Profile Exists", Toast.LENGTH_SHORT).show();
                }
            }
        });

        postnoref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                        postNo=0;
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    if(dataSnapshot.child("uid").getValue(String.class).equals(userid)){
                        postNo++;
                    }
                }
//                postNo=(int) snapshot.getChildrenCount();
                postsTv.setText(Integer.toString(postNo));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReferenceFollowers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                follwerCount= (int) snapshot.getChildrenCount();
                followersTv.setText(Integer.toString(follwerCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReferenceRequests.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(curUid)){
                    btnFollow.setText("Requested");
                    errorTv.setVisibility(View.VISIBLE);
                    errorTv.setText("Wait for User to accept");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReferenceFollowers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(curUid)){
                    btnFollow.setText("Following");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}


