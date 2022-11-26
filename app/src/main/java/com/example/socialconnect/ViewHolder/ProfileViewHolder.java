package com.example.socialconnect.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialconnect.R;

public class ProfileViewHolder extends RecyclerView.ViewHolder {

    public CardView cardView;
    TextView nameTv,professionTv,viewUserProfileTv;
    ImageView imageView;
    public ProfileViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setProfile(FragmentActivity activity,String name,String uid,String prof,String url){
        cardView=itemView.findViewById(R.id.request_cardView_rf);
        nameTv=itemView.findViewById(R.id.request_item_name_rf);
        professionTv=itemView.findViewById(R.id.request_item_profession_rf);
        viewUserProfileTv=itemView.findViewById(R.id.request_item_view_profile_rf);
        imageView=itemView.findViewById(R.id.request_profile_item_rf);

        Glide.with(activity).load(url).into(imageView);
        nameTv.setText(name);
        professionTv.setText(prof);


    }
}
