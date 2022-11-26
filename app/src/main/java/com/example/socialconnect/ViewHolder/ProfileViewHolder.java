package com.example.socialconnect.ViewHolder;

import android.app.Application;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialconnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileViewHolder extends RecyclerView.ViewHolder {

    public CardView cardView;
    TextView nameTv,professionTv,viewUserProfileTv;
    ImageView imageView;
    public TextView sendMessageBtn;
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

    public void setProfileChat(Application activity, String name, String uid, String prof, String url){

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String curUid=user.getUid();

        ImageView imageView=itemView.findViewById(R.id.iv_ch_ac_item);
        TextView nametv=itemView.findViewById(R.id.tv_name_ch_item);
        TextView proftv=itemView.findViewById(R.id.tv_que_ch_item);
         sendMessageBtn=itemView.findViewById(R.id.tv_send_message_item_ch_btn);

         if(uid.equals(curUid)){
             Glide.with(activity).load(url).into(imageView);
             nametv.setText(name);
             proftv.setText(prof);
             sendMessageBtn.setVisibility(View.INVISIBLE);
         }else{
             Glide.with(activity).load(url).into(imageView);
             nametv.setText(name);
             proftv.setText(prof);
         }

    }
}
