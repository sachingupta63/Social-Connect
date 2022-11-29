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

public class CommentViewHolder extends RecyclerView.ViewHolder {

    public ImageView iv_profile,iv_like;
    TextView tv_time,tv_like_count,tv_name,tv_comment;
    public TextView tv_delete;
    int likeCount;
    DatabaseReference databaseReference;
    FirebaseDatabase database;
    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setComment(Application application,String comment,String time,String url,String username,String uid){

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        String curUid=user.getUid();

        iv_profile=itemView.findViewById(R.id.iv_profile_comment_item);
        iv_like=itemView.findViewById(R.id.iv_like_comment_item);
        tv_time=itemView.findViewById(R.id.tv_time_comment_item);
        tv_like_count=itemView.findViewById(R.id.tv_count_like_comment_item);
        tv_delete=itemView.findViewById(R.id.tv_delete_comment_item);
        tv_name=itemView.findViewById(R.id.tv_name_comment_item);
        tv_comment=itemView.findViewById(R.id.tv_question_comment_item);

        tv_name.setText(username);
        tv_time.setText(time);
        tv_comment.setText(comment);
        Glide.with(application).load(url).into(iv_profile);

        if(!uid.equals(curUid)){
            tv_delete.setVisibility(View.INVISIBLE);
        }


    }

    public void likeChecker(String commentKey){
        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference("CommentLikes").child(commentKey);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String curUid=user.getUid();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(curUid)){
                    iv_like.setImageResource(R.drawable.ic_like);
                }else{
                    iv_like.setImageResource(R.drawable.ic_dislike);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


         databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likeCount= (int) snapshot.getChildrenCount();
                tv_like_count.setText(Integer.toString(likeCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }
}
