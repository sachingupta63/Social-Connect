package com.example.socialconnect.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.socialconnect.Model.PostModel;
import com.example.socialconnect.Model.QuestionMember;
import com.example.socialconnect.PostActivity;
import com.example.socialconnect.R;
import com.example.socialconnect.ReplyActivity;
import com.example.socialconnect.ViewHolder.PostViewHolder;
import com.example.socialconnect.ViewHolder.ViewHolder_Questions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class HomeFragment extends Fragment implements View.OnClickListener {


    Button button;
    RecyclerView recyclerView;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference reference,likeref;
    Boolean likeChecker=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        button=getActivity().findViewById(R.id.btn_createpost_home);

        reference=database.getReference("AllPosts");
        likeref=database.getReference("postlikes");

        recyclerView=getActivity().findViewById(R.id.rv_homeFrag_post);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_createpost_home:
                startActivity(new Intent(getActivity(), PostActivity.class));
                break;
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<PostModel> options=new FirebaseRecyclerOptions.Builder<PostModel>()
                .setQuery(reference,PostModel.class)
                .build();

        FirebaseRecyclerAdapter<PostModel, PostViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<PostModel, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull PostModel model) {
                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                String curUid=user.getUid();

                final String postKey=getRef(position).getKey();
                holder.setPost(getActivity(),model.getName(), model.getUrl(), model.getPostUri(), model.getTime(), model.getUid(), model.getType(),model.getDesc());

//                String que=getItem(position).getQuestion();
                String name=getItem(position).getName();
                String url=getItem(position).getUrl();
                String time=getItem(position).getTime();
//                String privacy=getItem(position).getPrivacy();
                String userid=getItem(position).getUid();



                holder.likeChecker(postKey);

                holder.moreOptionbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(name,url,time,userid);
                    }
                });


                holder.likebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        likeChecker=true;
                        likeref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(likeChecker.equals(true)){
                                    if(snapshot.child(postKey).hasChild(curUid)){
                                        likeref.child(postKey).child(curUid).removeValue();
                                        //deleteTime(time);
                                        likeChecker=false;
                                    }else{
                                        likeref.child(postKey).child(curUid).setValue(true);

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

            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view=getLayoutInflater().from(parent.getContext()).inflate(R.layout.post_rv_home_item_design,parent,false);
                return new PostViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public void showDialog(String name,String url,String time,String userid){
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View view=inflater.inflate(R.layout.options_layout_design,null);

        TextView download=view.findViewById(R.id.tv_download_post);
        TextView share=view.findViewById(R.id.tv_share_post);
        TextView delete=view.findViewById(R.id.tv_delete_post);
        TextView copyUrl=view.findViewById(R.id.tv_copy_url_post);


        AlertDialog dialog=new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
        dialog.show();

    }
}