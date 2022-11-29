package com.example.socialconnect.ViewHolder;

import android.app.Application;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialconnect.R;

public class StoryViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;
    TextView textView;
    public StoryViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setStory(FragmentActivity application, String postUri, String name, String timeUpload, String type, String caption, String url, String uid, long timeEnd){

        imageView=itemView.findViewById(R.id.iv_story_home_frag_item);
        textView=itemView.findViewById(R.id.tv_username_story_item);

        Glide.with(application).load(url).into(imageView);
        textView.setText(name);

    }
}
