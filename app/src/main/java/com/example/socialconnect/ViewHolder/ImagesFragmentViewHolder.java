package com.example.socialconnect.ViewHolder;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialconnect.R;


public class ImagesFragmentViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    public ImagesFragmentViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setImage(FragmentActivity activity, String name, String url, String postUri, String time,
                        String uid, String type, String desc){

        imageView=itemView.findViewById(R.id.iv_post_individual);
            Glide.with(activity).load(postUri).into(imageView);


    }
}
