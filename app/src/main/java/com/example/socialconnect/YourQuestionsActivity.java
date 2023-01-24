package com.example.socialconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialconnect.ViewHolder.ViewHolder_Questions;
import com.example.socialconnect.Model.QuestionMember;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class YourQuestionsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference UserQuestionsreference,AllQuestionsReference;
    TextView empty_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_questions);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String curUid=user.getUid();

        empty_text=findViewById(R.id.tv_empty_your_question);
        recyclerView=findViewById(R.id.rv_your_questions);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AllQuestionsReference=database.getReference("AllQuestions");
        UserQuestionsreference=database.getReference("UserQuestions").child(curUid);

        FirebaseRecyclerOptions<QuestionMember> options=new FirebaseRecyclerOptions.Builder<QuestionMember>()
                .setQuery(UserQuestionsreference,QuestionMember.class)
                .build();

        FirebaseRecyclerAdapter<QuestionMember, ViewHolder_Questions> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<QuestionMember, ViewHolder_Questions>(options) {
            public void onDataChanged() {
                super.onDataChanged();
                if(getItemCount()==0){
                    recyclerView.setVisibility(View.INVISIBLE);
                    empty_text.setVisibility(View.VISIBLE);
                }else{
                    recyclerView.setVisibility(View.VISIBLE);
                    empty_text.setVisibility(View.GONE);
                }
            }
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder_Questions holder, int position, @NonNull QuestionMember model) {
                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                String curUid=user.getUid();

                final String postKey=getRef(position).getKey();
                holder.setItemYourQuestionActivity(getApplication(),model.getName(), model.getUrl(), model.getUrserId(), model.getKey(), model.getQuestion(), model.getPrivacy(), model.getTime());


                String time=getItem(position).getTime();
                holder.deletebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteTime(time);
                    }
                });





            }

            @NonNull
            @Override
            public ViewHolder_Questions onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view=getLayoutInflater().from(parent.getContext()).inflate(R.layout.your_question_rv_design_item,parent,false);
                return new ViewHolder_Questions(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    void deleteTime(String time){
        Query query=UserQuestionsreference.orderByChild("time").equalTo(time);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    dataSnapshot.getRef().removeValue();

                    Toast.makeText(YourQuestionsActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Query query1=AllQuestionsReference.orderByChild("time").equalTo(time);
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    dataSnapshot.getRef().removeValue();

                    Toast.makeText(YourQuestionsActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}