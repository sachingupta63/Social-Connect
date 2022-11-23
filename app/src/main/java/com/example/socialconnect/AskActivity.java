package com.example.socialconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class AskActivity extends AppCompatActivity {

    EditText editText;
    Button button;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference AllQuestion,UserQuestion;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    QuestionMember member;
    String name,url,privacy,uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();;
        String curId=user.getUid();

        editText=findViewById(R.id.et_ask_question);
        button=findViewById(R.id.btn_submit_askA);

        documentReference=db.collection("user").document(curId);

        AllQuestion=database.getReference("AllQuestions");
        //This is used to identify which question to be deleted
        UserQuestion=database.getReference("UserQuestions").child(curId);

        member=new QuestionMember();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question=editText.getText().toString();
                Calendar cdate=Calendar.getInstance();
                SimpleDateFormat currdate=new SimpleDateFormat("dd-MM-yyyy");
                final String savedate=currdate.format(cdate.getTime());

                Calendar ctime=Calendar.getInstance();
                SimpleDateFormat curTime=new SimpleDateFormat("HH:mm:ss");
                final String savetime=curTime.format(ctime.getTime());

                String time=savedate+":"+savetime;

                if(question!=null){
                    member.setQuestion(question);
                    member.setName(name);
                    member.setPrivacy(privacy);
                    member.setUrl(url);
                    member.setUrserId(uid);
                    member.setTime(time);

                    String id=UserQuestion.push().getKey();
                    UserQuestion.child(id).setValue(member);


                    //The Key Of Previous Question is Adding for Replying functionalty
                    String child=AllQuestion.push().getKey();
                    member.setKey(id);
                    AllQuestion.child(child).setValue(member);
                    Toast.makeText(AskActivity.this, "Sumitted", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(AskActivity.this, "Enter a Question", Toast.LENGTH_SHORT).show();
                }

            }
        });





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
                     privacy = task.getResult().getString("privacy");
                    uid=task.getResult().getString("uid");



                }else{
                    Toast.makeText(AskActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}