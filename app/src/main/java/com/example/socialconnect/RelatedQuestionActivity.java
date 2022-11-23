package com.example.socialconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.example.socialconnect.ViewHolder.ViewHolder_Questions;
import com.example.socialconnect.Model.QuestionMember;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RelatedQuestionActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_related_question);

        recyclerView=findViewById(R.id.rv_related);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String curUid=user.getUid();
        reference=database.getReference("favouriteList").child(curUid);


        FirebaseRecyclerOptions<QuestionMember> options=new FirebaseRecyclerOptions.Builder<QuestionMember>()
                .setQuery(reference,QuestionMember.class)
                .build();

        FirebaseRecyclerAdapter<QuestionMember, ViewHolder_Questions> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<QuestionMember, ViewHolder_Questions>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder_Questions holder, int position, @NonNull QuestionMember model) {
                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                String curUid=user.getUid();

                final String postKey=getRef(position).getKey();
                holder.setItemRelatedActivity(getApplication(),model.getName(), model.getUrl(), model.getUrserId(), model.getKey(), model.getQuestion(), model.getPrivacy(), model.getTime());

                String que=getItem(position).getQuestion();
//                String name=getItem(position).getName();
//                String url=getItem(position).getUrl();
//                String time=getItem(position).getTime();
//                String privacy=getItem(position).getPrivacy();
                String userid=getItem(position).getUrserId();


                holder.reply_btn_related.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(RelatedQuestionActivity.this, ReplyActivity.class);
                        intent.putExtra("uid",userid);
                        intent.putExtra("que",que);
                        intent.putExtra("postkey",postKey);

                        startActivity(intent);


                    }
                });

            }

            @NonNull
            @Override
            public ViewHolder_Questions onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view=getLayoutInflater().from(parent.getContext()).inflate(R.layout.related_rv_question_design_item,parent,false);
                return new ViewHolder_Questions(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}