package com.example.socialconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.socialconnect.Model.CommentModel;
import com.example.socialconnect.Model.PostModel;
import com.example.socialconnect.ViewHolder.CommentViewHolder;
import com.example.socialconnect.ViewHolder.PostViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CommentActivity extends AppCompatActivity {
    String name,url,postKey;
    ImageView imageView;
    TextView textView,empty_txt;
    EditText editText;
    Button btn_send;

    RecyclerView recyclerView;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference commentRef,rootCommentRef,commentLikes;
    CommentModel commentModel;

    Boolean likeChecker=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        commentModel=new CommentModel();

        recyclerView=findViewById(R.id.rv_comment);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        empty_txt=findViewById(R.id.empty_txt_comment);
        imageView=findViewById(R.id.iv_profile_user_comment);
        textView=findViewById(R.id.tv_name_commnet);
        editText=findViewById(R.id.et_comment);
        btn_send=findViewById(R.id.button_comment);

        commentRef=database.getReference("Comments");
        commentLikes=database.getReference("CommentLikes");

        Bundle extras=getIntent().getExtras();

        if(extras!=null){
            name=extras.getString("name");
            url=extras.getString("url");
            postKey=extras.getString("postkey");
        }else{
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }

        Glide.with(this).load(url).into(imageView);
        textView.setText(name);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment=editText.getText().toString();
                if(comment.isEmpty()){
                    Toast.makeText(CommentActivity.this, "Please Write Comment First", Toast.LENGTH_SHORT).show();
                }else{

                    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                    String curUid= user.getUid();

                    Calendar cdate=Calendar.getInstance();
                    SimpleDateFormat currdate=new SimpleDateFormat("dd-MM-yyyy");
                    final String savedate=currdate.format(cdate.getTime());

                    Calendar ctime=Calendar.getInstance();
                    SimpleDateFormat curTime=new SimpleDateFormat("HH:mm:ss");
                    final String savetime=curTime.format(ctime.getTime());

                    String time=savedate+":"+savetime;

                    commentModel.setComment(comment);
                    commentModel.setTime(time);
                    commentModel.setUrl(url);
                    commentModel.setUsername(name);
                    commentModel.setUid(curUid);

                    String id=commentRef.child(postKey).push().getKey();
                    commentRef.child(postKey).child(id).setValue(commentModel);

                    editText.setText("");

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        rootCommentRef=commentRef.child(postKey);
        FirebaseRecyclerOptions<CommentModel> options=new FirebaseRecyclerOptions.Builder<CommentModel>()
                .setQuery(rootCommentRef,CommentModel.class)
                .build();

        FirebaseRecyclerAdapter<CommentModel, CommentViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<CommentModel, CommentViewHolder>(options) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();

                if(getItemCount()==0){
                    recyclerView.setVisibility(View.INVISIBLE);
                    empty_txt.setVisibility(View.VISIBLE);
                }else{
                    recyclerView.setVisibility(View.VISIBLE);
                    empty_txt.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull CommentModel model) {
                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                String curUid=user.getUid();

                final String commentkey=getRef(position).getKey();
                holder.setComment(getApplication(),model.getComment(), model.getTime(), model.getUrl(), model.getUsername(), model.getUid());





                holder.likeChecker(commentkey);


                holder.iv_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        likeChecker=true;
                        commentLikes.child(commentkey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(likeChecker.equals(true)){
                                    if(snapshot.hasChild(curUid)){
                                        commentLikes.child(commentkey).child(curUid).removeValue();
                                        likeChecker=false;
                                    }else{
                                        commentLikes.child(commentkey).child(curUid).setValue(true);
                                        likeChecker=false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                holder.tv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rootCommentRef.child(commentkey).removeValue();
                        commentLikes.child(commentkey).removeValue();
                    }
                });



            }

            @NonNull
            @Override
            public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view=getLayoutInflater().from(parent.getContext()).inflate(R.layout.comment_rv_design,parent,false);
                return new CommentViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }
}