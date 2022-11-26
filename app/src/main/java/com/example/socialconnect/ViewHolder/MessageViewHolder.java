package com.example.socialconnect.ViewHolder;

import android.app.Application;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialconnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    TextView senderTv,receiverTv;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setMessage(Application application,String message,String time,String date,String type,String senderUid,String receiverUid){
        senderTv=itemView.findViewById(R.id.sender_tv);
        receiverTv=itemView.findViewById(R.id.receiver_tv);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String curUid=user.getUid();

        if(curUid.equals(senderUid)){
            receiverTv.setVisibility(View.GONE);
            senderTv.setText(message);

        }else if(curUid.equals(receiverUid)){
            senderTv.setVisibility(View.GONE);
            receiverTv.setText(message);

        }
    }
}
