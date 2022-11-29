package com.example.socialconnect.ViewHolder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialconnect.R;

public class StoryFragmentViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    public StoryFragmentViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setStory(FragmentActivity application, String postUri, String name, String timeUpload, String type, String caption, String url, String uid, long timeEnd){

        imageView=itemView.findViewById(R.id.iv_post_individual);
        Glide.with(application).load(postUri).into(imageView);

    }


}
