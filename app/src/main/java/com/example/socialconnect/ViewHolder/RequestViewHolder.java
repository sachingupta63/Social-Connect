package com.example.socialconnect.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialconnect.R;

public class RequestViewHolder extends RecyclerView.ViewHolder {

    TextView textView;
    ImageView imageView;
    public TextView acceptbtn,rejectbtn;
    public RequestViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setRequest(FragmentActivity activity,String name,String url){
        imageView=itemView.findViewById(R.id.iv_profile_rf_item);
        textView=itemView.findViewById(R.id.tv_name_rf_item);
        acceptbtn=itemView.findViewById(R.id.tv_accept_rf_item);
        rejectbtn=itemView.findViewById(R.id.tv_decline_rf_item);

        Glide.with(activity).load(url).into(imageView);
        textView.setText(name);


    }



}
