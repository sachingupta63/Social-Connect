package com.example.socialconnect.Adapters;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
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

public class ViewHolder_Questions extends RecyclerView.ViewHolder{


    TextView time_result,name_result,question_result;
    ImageView imageView;
    public ImageButton fvrt_btn;
    DatabaseReference favouriteRef;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    public ViewHolder_Questions(@NonNull View itemView) {
        super(itemView);
    }
    public void setItem(FragmentActivity activity,String name,String url,String userId,String key,String question,String privacy,String time){

        imageView=itemView.findViewById(R.id.iv_que_profile_item);
        time_result=itemView.findViewById(R.id.tv_time_que_item);
        name_result=itemView.findViewById(R.id.tv_name_que_item);
        question_result=itemView.findViewById(R.id.tv_que_item);


        Glide.with(activity).load(url).into(imageView);
        time_result.setText(time);
        name_result.setText(name);
        question_result.setText(question);


    }

    public void favouriteChecker(String postKey) {
        fvrt_btn=itemView.findViewById(R.id.fvrt_que_item);
        favouriteRef=database.getReference("favourites");

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String uid=user.getUid();
        favouriteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postKey).hasChild(uid)){
                    fvrt_btn.setImageResource(R.drawable.ic_baseline_turned_in_24);
                }else{
                    fvrt_btn.setImageResource(R.drawable.ic_baseline_turned_in_not_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}
