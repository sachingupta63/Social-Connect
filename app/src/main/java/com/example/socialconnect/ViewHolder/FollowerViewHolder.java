package com.example.socialconnect.ViewHolder;

import android.app.Application;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialconnect.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FollowerViewHolder extends RecyclerView.ViewHolder {
    public CardView cardView;
    TextView nameTv,professionTv,viewUserProfileTv;
    ImageView imageView;
    DatabaseReference database;
    public FollowerViewHolder(@NonNull View itemView) {
        super(itemView);
    }
    public void setFollowerProfile(Application activity, String name, String uid, String url){
        database= FirebaseDatabase.getInstance().getReference("All Users");
        cardView=itemView.findViewById(R.id.request_cardView_rf);
        nameTv=itemView.findViewById(R.id.request_item_name_rf);
        professionTv=itemView.findViewById(R.id.request_item_profession_rf);
        viewUserProfileTv=itemView.findViewById(R.id.request_item_view_profile_rf);
        imageView=itemView.findViewById(R.id.request_profile_item_rf);
        Glide.with(activity).load(url).into(imageView);
        nameTv.setText(name);

        database.child(uid).child("prof").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                professionTv.setText(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        fcardView=itemView.findViewById(R.id.request_cardView_rf);
//        fnameTv=itemView.findViewById(R.id.request_item_name_rf);
//        fprofessionTv=itemView.findViewById(R.id.request_item_profession_rf);
//        fviewUserProfileTv=itemView.findViewById(R.id.request_item_view_profile_rf);
//        fimageView=itemView.findViewById(R.id.request_profile_item_rf);
//        Glide.with(activity).load(url).into(fimageView);
//        nameTv.setText(name);
//        professionTv.setText(fprof[0]);


    }
}
