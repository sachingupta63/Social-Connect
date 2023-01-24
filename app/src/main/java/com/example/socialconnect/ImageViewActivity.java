package com.example.socialconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ImageViewActivity extends AppCompatActivity{

    ImageView imageView;
    TextView textView;

    DocumentReference reference;
    String url;
    FirebaseFirestore db=FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);


        imageView=findViewById(R.id.im_expand);
        textView=findViewById(R.id.tv_name_iv);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String currentId=user.getUid();

        reference=db.collection("user").document(currentId);






    }

    @Override
    protected void onStart() {
        super.onStart();
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    String name=task.getResult().getString("name");
                    url=task.getResult().getString("url");

                    Glide.with(ImageViewActivity.this).load(url).into(imageView);
                    textView.setText(name);
                }else{
                    Toast.makeText(ImageViewActivity.this, "No Profile", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}