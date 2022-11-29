package com.example.socialconnect.ViewHolder;



import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialconnect.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PostViewHolder extends RecyclerView.ViewHolder {

    ImageView imageViewProfile,imageViewPost;
    TextView tv_desc,tv_likes,tv_comment,tv_time,tv_nameProfile;
    public ImageButton likebtn,commentbtn,moreOptionbtn;
    DatabaseReference likeRef,commentRef;
    FirebaseDatabase database=FirebaseDatabase.getInstance();

    int likes_Count,comment_count;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
    }
    public void setPost(FragmentActivity activity,String name,String url,String postUri,String time,
                        String uid,String type,String desc){

        imageViewProfile=itemView.findViewById(R.id.iv_profile_post_item);
        imageViewPost=itemView.findViewById(R.id.iv_user_post);
        tv_comment=itemView.findViewById(R.id.tv_comment_post);
        tv_desc=itemView.findViewById(R.id.tv_dec_post);
        commentbtn=itemView.findViewById(R.id.commentbtn_post);
        likebtn=itemView.findViewById(R.id.likebtn_post);
        tv_likes=itemView.findViewById(R.id.tv_like_post);
        moreOptionbtn=itemView.findViewById(R.id.morebtn_post);
        tv_time=itemView.findViewById(R.id.tv_time_post);
        tv_nameProfile=itemView.findViewById(R.id.tv_name_post);


        VideoView videoView=itemView.findViewById(R.id.exoplayer_post_item);

        if(type.equals("iv")){
            Glide.with(activity).load(url).into(imageViewProfile);
            Glide.with(activity).load(postUri).into(imageViewPost);
            tv_desc.setText(desc);
            tv_nameProfile.setText(name);
            tv_time.setText(time);
            videoView.setVisibility(View.INVISIBLE);
        }else if(type.equals("vv")){
            imageViewPost.setVisibility(View.INVISIBLE);
            Glide.with(activity).load(url).into(imageViewProfile);
            tv_desc.setText(desc);
            tv_nameProfile.setText(name);
            tv_time.setText(time);

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

    public void likeChecker(String postKey) {

        likebtn=itemView.findViewById(R.id.likebtn_post);

        likeRef=database.getReference("postlikes");
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String uid=user.getUid();

        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postKey).hasChild(uid)){
                    likebtn.setImageResource(R.drawable.ic_like);
                    likes_Count=(int) snapshot.child(postKey).getChildrenCount();
                    tv_likes.setText(Integer.toString(likes_Count)+" likes");
                }else{
                    likebtn.setImageResource(R.drawable.ic_dislike);
                    likes_Count=(int) snapshot.child(postKey).getChildrenCount();
                    tv_likes.setText(Integer.toString(likes_Count)+" likes");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    public void commentChecker(String postKey){
        commentRef=database.getReference("Comments");
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comment_count=(int) snapshot.child(postKey).getChildrenCount();
                tv_comment.setText(comment_count+" Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

}
