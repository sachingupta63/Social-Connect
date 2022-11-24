package com.example.socialconnect.ViewHolder;

import android.net.Uri;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialconnect.R;

public class VideoFragmentViewHolder extends RecyclerView.ViewHolder {
    VideoView videoView;
    public VideoFragmentViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setVideo(FragmentActivity activity, String name, String url, String postUri, String time,
                        String uid, String type, String desc){


        videoView=itemView.findViewById(R.id.vv_post_individual);



            try{

                videoView.setVideoURI(Uri.parse(postUri));
                MediaController mediaController=new MediaController(activity);
                mediaController.setAnchorView(videoView);
                mediaController.setMediaPlayer(videoView);
                videoView.setMediaController(mediaController);
                videoView.pause();
            }catch(Exception e){
                Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
            }




    }
}
