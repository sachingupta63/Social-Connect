package com.example.socialconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.socialconnect.Model.AnswerMember;
import com.example.socialconnect.Model.QuestionMember;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AnswerActivity extends AppCompatActivity {

    String uid,que,postkey;
    EditText editText;
    Button btn_submit;
    AnswerMember member;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference AllQuestions;
    String name,url,time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        member=new AnswerMember();
        editText=findViewById(R.id.et_answer_activity);
        btn_submit=findViewById(R.id.btn_submit_answer_activity);

        Bundle bundle=getIntent().getExtras();

        if(bundle!=null){
            uid=bundle.getString("u");
            postkey=bundle.getString("p");

        }else{
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

        AllQuestions=database.getReference("AllQuestions").child(postkey).child("Answer");

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAnswer();
            }
        });



    }
    void saveAnswer(){

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String curUid=user.getUid();
        String answer=editText.getText().toString();
        if(answer!=null){

            Calendar cdate=Calendar.getInstance();
            SimpleDateFormat currdate=new SimpleDateFormat("dd-MM-yyyy");
            final String savedate=currdate.format(cdate.getTime());

            Calendar ctime=Calendar.getInstance();
            SimpleDateFormat curTime=new SimpleDateFormat("HH:mm:ss");
            final String savetime=curTime.format(ctime.getTime());

             time=savedate+":"+savetime;

             member.setTime(time);
             member.setAnswer(answer);
             member.setName(name);
             member.setUid(curUid);
             member.setUrl(url);

             String id=AllQuestions.push().getKey();
             AllQuestions.child(id).setValue(member);

            Toast.makeText(this, "Answer Saved Succesfully", Toast.LENGTH_SHORT).show();





        }else{
            Toast.makeText(this, "Please Write answer", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String curUid=user.getUid();

        DocumentReference documentReference= FirebaseFirestore.getInstance().collection("user").document(curUid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                     url=task.getResult().getString("url");
                     name=task.getResult().getString("name");

                }else{
                    Toast.makeText(AnswerActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}