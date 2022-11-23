package com.example.socialconnect.ViewHolder;



import android.app.Application;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialconnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AnswerViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView nameTv,timeTv,ansTv;
    public TextView votesNoTv,upvoteTv;
    int votesCount;
    DatabaseReference reference;
    FirebaseDatabase database;
    public AnswerViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setItemAnswer(Application application,String name,String answer,String uid,String time,String url){

        imageView=itemView.findViewById(R.id.iv_profile_ans);
        nameTv=itemView.findViewById(R.id.tv_name_ans);
        timeTv=itemView.findViewById(R.id.tv_time_ans);
        ansTv=itemView.findViewById(R.id.tv_ans);


        nameTv.setText(name);
        timeTv.setText(time);
        ansTv.setText(answer);

        Glide.with(application).load(url).into(imageView);

    }

    //This is used to check whether the question is voted or not
    public void upvoteChecker(String postKey){
        database=FirebaseDatabase.getInstance();
        reference=database.getReference("votes");

        votesNoTv=itemView.findViewById(R.id.tv_vote_no);
        upvoteTv=itemView.findViewById(R.id.tv_vote_ans);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String curUid=user.getUid();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postKey).hasChild(curUid)){
                    upvoteTv.setText("VOTED");
                    votesCount=(int)snapshot.child(postKey).getChildrenCount();
                    votesNoTv.setText(Integer.toString(votesCount)+"-VOTES");
                }else{
                    upvoteTv.setText("UPVOTE");
                    votesCount=(int)snapshot.child(postKey).getChildrenCount();
                    votesNoTv.setText(Integer.toString(votesCount)+"-VOTES");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
