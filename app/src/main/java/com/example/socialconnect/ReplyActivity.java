package com.example.socialconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.socialconnect.Model.AnswerMember;
import com.example.socialconnect.Model.QuestionMember;
import com.example.socialconnect.ViewHolder.AnswerViewHolder;
import com.example.socialconnect.ViewHolder.ViewHolder_Questions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

public class ReplyActivity extends AppCompatActivity {

    String uid,question,postkey,privacy;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    DocumentReference reference,reference2;

    TextView nametv,questiontv;
    LinearLayout replytv;
    RecyclerView recyclerView;
    ImageView imageViewQue,imageViewUser;

    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference voteReference,AllQuestions;

    Boolean voteChecker=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        replytv=findViewById(R.id.tv_answer_reply);
        nametv=findViewById(R.id.tv_name_reply);
        questiontv=findViewById(R.id.tv_question_reply);
        imageViewQue=findViewById(R.id.iv_profile_que_reply);
        imageViewUser=findViewById(R.id.iv_profile_user_reply);
        recyclerView=findViewById(R.id.rv_reply_activity);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String curUid=user.getUid();

        Bundle extra=getIntent().getExtras();
        if(extra!=null){
            uid=extra.getString("uid");
            postkey=extra.getString("postkey");
            question= extra.getString("que");
            //privacy= extra.getString("privacy");
        }else{
            Toast.makeText(this, "OOps", Toast.LENGTH_SHORT).show();
        }

        voteReference=database.getReference("votes");
        AllQuestions=database.getReference("AllQuestions").child(postkey).child("Answer");


        //User
        reference=db.collection("user").document(uid);
        //self
        reference2=db.collection("user").document(curUid);


        replytv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(ReplyActivity.this, AnswerActivity.class);
                intent.putExtra("u",uid);
                intent.putExtra("q",question);
                intent.putExtra("p",postkey);
                //intent.putExtra("privacy",privacy);
                startActivity(intent);

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        //For Question User
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    String url=task.getResult().getString("url");
                    String name=task.getResult().getString("name");
                    nametv.setText(name);
                    questiontv.setText(question);
                    try {
                        Glide.with(ReplyActivity.this).load(url).into(imageViewQue);
                    }catch (Exception e){
                        Toast.makeText(ReplyActivity.this, "Error in Loading Image", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(ReplyActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //For User
        reference2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    String url=task.getResult().getString("url");

                    try {
                        Glide.with(ReplyActivity.this).load(url).into(imageViewUser);
                    }catch (Exception e){
                        Toast.makeText(ReplyActivity.this, "Error in Loading Image", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(ReplyActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //For Retrieving answer
        FirebaseRecyclerOptions<AnswerMember> options=new FirebaseRecyclerOptions.Builder<AnswerMember>()
                .setQuery(AllQuestions,AnswerMember.class)
                .build();



        FirebaseRecyclerAdapter<AnswerMember,AnswerViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<AnswerMember, AnswerViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AnswerViewHolder holder, int position, @NonNull AnswerMember model) {
                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                String curUid=user.getUid();

                //Ansewer post key for particular question
                final String post_key=getRef(position).getKey();

                holder.setItemAnswer(getApplication(),model.getName(), model.getAnswer(),model.getUid(),model.getTime(), model.getUrl());

                holder.upvoteChecker(post_key);

                holder.upvoteTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        voteChecker=true;
                        voteReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(voteChecker){
                                    if(snapshot.child(post_key).hasChild(curUid)){
                                        voteReference.child(post_key).child(curUid).removeValue();
                                        voteChecker=false;
                                    }else{
                                        voteReference.child(post_key).child(curUid).setValue(true);
                                        voteChecker=false;
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });



            }

            @NonNull
            @Override
            public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=getLayoutInflater().from(parent.getContext()).inflate(R.layout.reply_rv_design_item,parent,false);
                return new AnswerViewHolder(view);


            }
        };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);


    }
}