package com.example.socialconnect.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialconnect.ViewHolder.ViewHolder_Questions;
import com.example.socialconnect.AskActivity;
import com.example.socialconnect.Model.QuestionMember;
import com.example.socialconnect.R;
import com.example.socialconnect.ReplyActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class AskFragment extends Fragment implements View.OnClickListener {

    FloatingActionButton floatingActionButton;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    DocumentReference reference;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference databaseReference,fvrtRefernce,fvrt_listRef;
    RecyclerView recyclerView;
    Boolean fvrtChecker=false;
    ImageView profile_iv;
    ProgressBar empty_progress;

    QuestionMember member;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_ask, container, false);



        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        floatingActionButton=getActivity().findViewById(R.id.floatingActionButton);
        profile_iv=getActivity().findViewById(R.id.iv_profile_af);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String curUid=user.getUid();

        empty_progress=getActivity().findViewById(R.id.empty_progress_ask_frag);
        recyclerView=getActivity().findViewById(R.id.rv_af);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        databaseReference=database.getReference("AllQuestions");
        member=new QuestionMember();
        //This is Just check whether question save or not
        fvrtRefernce=database.getReference("favourites");
        fvrt_listRef=database.getReference("favouriteList").child(curUid);



        reference=db.collection("user").document(curUid);

        profile_iv.setOnClickListener(this);
        floatingActionButton.setOnClickListener(this);

        FirebaseRecyclerOptions<QuestionMember> options=new FirebaseRecyclerOptions.Builder<QuestionMember>()
                .setQuery(databaseReference,QuestionMember.class)
                .build();

        FirebaseRecyclerAdapter<QuestionMember, ViewHolder_Questions> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<QuestionMember, ViewHolder_Questions>(options) {
            @Override
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
            protected void onBindViewHolder(@NonNull ViewHolder_Questions holder, int position, @NonNull QuestionMember model) {
                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                String curUid=user.getUid();

                final String postKey=getRef(position).getKey();
                holder.setItem(getActivity(),model.getName(), model.getUrl(), model.getUrserId(), model.getKey(), model.getQuestion(), model.getPrivacy(), model.getTime());

                String que=getItem(position).getQuestion();
                String name=getItem(position).getName();
                String url=getItem(position).getUrl();
                String time=getItem(position).getTime();
                String privacy=getItem(position).getPrivacy();
                String userid=getItem(position).getUrserId();

                holder.replybtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(getActivity(), ReplyActivity.class);
                        intent.putExtra("uid",userid);
                        intent.putExtra("que",que);
                        intent.putExtra("postkey",postKey);
                        intent.putExtra("privacy",privacy);
                        startActivity(intent);

                    }
                });

                holder.favouriteChecker(postKey);
                holder.fvrt_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fvrtChecker=true;
                        fvrtRefernce.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(fvrtChecker.equals(true)){
                                    if(snapshot.child(postKey).hasChild(curUid)){
                                        fvrtRefernce.child(postKey).child(curUid).removeValue();
                                        deleteTime(time);
                                        fvrtChecker=false;
                                    }else{
                                        fvrtRefernce.child(postKey).child(curUid).setValue(true);
                                         member.setName(name);
                                        member.setTime(time);
                                        member.setPrivacy(privacy);
                                        member.setUrserId(userid);
                                        member.setUrl(url);
                                        member.setQuestion(que);

                                        //All post saved here to be checked
                                        //String id=fvrt_listRef.push().getKey();
                                        fvrt_listRef.child(postKey).setValue(member);
                                        fvrtChecker=false;


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
            public ViewHolder_Questions onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view=getLayoutInflater().from(parent.getContext()).inflate(R.layout.question_item_design,parent,false);
                return new ViewHolder_Questions(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);






    }

    void deleteTime(String time){
        Query query=fvrt_listRef.orderByChild("time").equalTo(time);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    dataSnapshot.getRef().removeValue();

                    Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.floatingActionButton:
                startActivity(new Intent(getActivity(), AskActivity.class));
                break;
            case R.id.iv_profile_af:
                BottomSheetAskQuestionFragmentDialog bottomSheetAskQuestionFragmentDialog=new BottomSheetAskQuestionFragmentDialog();
                bottomSheetAskQuestionFragmentDialog.show(getFragmentManager(),"bottom");
                break;
        }


    }

    @Override
    public void onStart() {
        super.onStart();

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    String url=task.getResult().getString("url");
                    try {
                        Glide.with(getActivity()).load(url).into(profile_iv);
                    }catch (Exception e){
                        Toast.makeText(getActivity(), "Error in Loading Image", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}