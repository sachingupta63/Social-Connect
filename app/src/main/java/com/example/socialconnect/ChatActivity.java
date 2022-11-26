package com.example.socialconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.socialconnect.Model.ProfileViewModel;
import com.example.socialconnect.ViewHolder.ProfileViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ChatActivity extends AppCompatActivity {

    DatabaseReference profileRef;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    RecyclerView recyclerView;
    EditText searchEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        searchEt=findViewById(R.id.search_user);
        recyclerView=findViewById(R.id.rv_ch);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        profileRef=database.getReference("All Users");

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchUer();

            }
        });

    }

    public void searchUer(){
        String query=searchEt.getText().toString().toUpperCase();
        Query search=profileRef.orderByChild("name").startAt(query).endAt(query+"\uf0ff");
        FirebaseRecyclerOptions<ProfileViewModel> options1=new FirebaseRecyclerOptions.Builder<ProfileViewModel>()
                .setQuery(search,ProfileViewModel.class)
                .build();

        FirebaseRecyclerAdapter<ProfileViewModel,ProfileViewHolder> firebaseRecyclerAdapter1=new FirebaseRecyclerAdapter<ProfileViewModel, ProfileViewHolder>(options1) {

            @Override
            protected void onBindViewHolder(@NonNull ProfileViewHolder holder, int position, @NonNull ProfileViewModel model) {

                final String postKey=getRef(position).getKey();

                holder.setProfileChat(getApplication(),model.getName(), model.getUid(), model.getProf(),model.getUrl());

                String name=model.getName();
                String url=model.getUrl();
                String uid=model.getUid();

                holder.sendMessageBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(ChatActivity.this, MessageActivity.class);
                        intent.putExtra("url",model.getUrl());
                        intent.putExtra("name",model.getName());
                        intent.putExtra("userid",model.getUid());
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=getLayoutInflater().from(parent.getContext()).inflate(R.layout.chat_profile_search_item,parent,false);
                return new ProfileViewHolder(view);
            }
        };


        firebaseRecyclerAdapter1.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter1);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<ProfileViewModel> options1=new FirebaseRecyclerOptions.Builder<ProfileViewModel>()
                .setQuery(profileRef,ProfileViewModel.class)
                .build();

        FirebaseRecyclerAdapter<ProfileViewModel,ProfileViewHolder> firebaseRecyclerAdapter1=new FirebaseRecyclerAdapter<ProfileViewModel, ProfileViewHolder>(options1) {

            @Override
            protected void onBindViewHolder(@NonNull ProfileViewHolder holder, int position, @NonNull ProfileViewModel model) {

                final String postKey=getRef(position).getKey();

                holder.setProfileChat(getApplication(),model.getName(), model.getUid(), model.getProf(),model.getUrl());

                String name=model.getName();
                String url=model.getUrl();
                String uid=model.getUid();

                holder.sendMessageBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(ChatActivity.this, MessageActivity.class);
                        intent.putExtra("url",model.getUrl());
                        intent.putExtra("name",model.getName());
                        intent.putExtra("userid",model.getUid());
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=getLayoutInflater().from(parent.getContext()).inflate(R.layout.chat_profile_search_item,parent,false);
                return new ProfileViewHolder(view);
            }
        };


        firebaseRecyclerAdapter1.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter1);
    }
}