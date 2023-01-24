package com.example.socialconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialconnect.Model.ProfileViewModel;
import com.example.socialconnect.Model.RequestModel;
import com.example.socialconnect.ViewHolder.FollowerViewHolder;
import com.example.socialconnect.ViewHolder.ProfileViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FollowersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView empty_progress;
    DatabaseReference profileRef;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        recyclerView=findViewById(R.id.rv_follower_friends);
        empty_progress=findViewById(R.id.empty_progress_follower);
        profileRef=database.getReference().child("Followers").child(user.getUid());

        FirebaseRecyclerOptions<RequestModel> options1=new FirebaseRecyclerOptions.Builder<RequestModel>()
                .setQuery(profileRef,RequestModel.class)
                .build();

        FirebaseRecyclerAdapter<RequestModel, FollowerViewHolder> firebaseRecyclerAdapter1=new FirebaseRecyclerAdapter<RequestModel, FollowerViewHolder>(options1) {

            public void onDataChanged() {
                super.onDataChanged();
                if(getItemCount()==0){
                    recyclerView.setVisibility(View.INVISIBLE);
                    empty_progress.setVisibility(View.VISIBLE);
                }else{
                    recyclerView.setVisibility(View.VISIBLE);
                    empty_progress.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onBindViewHolder(@NonNull FollowerViewHolder holder, int position, @NonNull RequestModel model) {


                holder.setFollowerProfile(getApplication(), model.getName(), model.getUserid(), model.getUrl());
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FollowersActivity.this, ShowUserProfileActivity.class);
                        intent.putExtra("url", model.getUrl());
                        intent.putExtra("name", model.getName());
                        intent.putExtra("userid", model.getUserid());
                        startActivity(intent);
                    }
                });

            }



            @NonNull
            @Override
            public FollowerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=getLayoutInflater().from(FollowersActivity.this).inflate(R.layout.request_rv_design_request_fragment,parent,false);
                return new FollowerViewHolder(view);
            }
        };


        firebaseRecyclerAdapter1.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter1);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}