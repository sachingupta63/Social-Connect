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

public class MessageViewHolder extends RecyclerView.ViewHolder {

    TextView senderTv,receiverTv;
    ImageView senderIv,receiverIv;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setMessage(Application application,String message,String time,String date,String type,String senderUid,String receiverUid){
        senderTv=itemView.findViewById(R.id.sender_tv);
        receiverTv=itemView.findViewById(R.id.receiver_tv);
        senderIv=itemView.findViewById(R.id.iv_sender);
        receiverIv=itemView.findViewById(R.id.iv_receiver);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String curUid=user.getUid();

        if(curUid.equals(senderUid)){
            if(type.equals("text")) {
                receiverTv.setVisibility(View.GONE);
                senderTv.setText(message);
            }else{
                receiverTv.setVisibility(View.GONE);
                senderTv.setVisibility(View.GONE);
                senderIv.setVisibility(View.VISIBLE);
                Glide.with(application).load(message).into(senderIv);
            }

        }else if(curUid.equals(receiverUid)){
            if(type.equals("text")) {
                senderTv.setVisibility(View.GONE);
                receiverTv.setText(message);
            }else{
                receiverTv.setVisibility(View.GONE);
                senderTv.setVisibility(View.GONE);
                receiverIv.setVisibility(View.VISIBLE);
                Glide.with(application).load(message).into(receiverIv);
            }

        }
    }
}
