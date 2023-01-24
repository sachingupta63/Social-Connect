package com.example.socialconnect.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.socialconnect.ChatActivity;
import com.example.socialconnect.CreateProfileActivity;
import com.example.socialconnect.FollowersActivity;
import com.example.socialconnect.ImageViewActivity;
import com.example.socialconnect.IndividualPostActivity;
import com.example.socialconnect.R;
import com.example.socialconnect.StoryActivity;
import com.example.socialconnect.UpdateProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfileFragment extends Fragment implements View.OnClickListener{

    ImageView imageView;
    TextView nameEt,proEt,emailEt,webEt,bioEt,postsTv,storyTv,followerTv;
    ImageView profile_edit,menu_imv;
    Button sendMessage;
    Uri imageUri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imageView=getActivity().findViewById(R.id.iv_profile_pf);
        nameEt=getActivity().findViewById(R.id.tv_name_pf);
        proEt=getActivity().findViewById(R.id.tv_prof_pf);
        emailEt=getActivity().findViewById(R.id.tv_email_pf);
        webEt=getActivity().findViewById(R.id.tv_website_pf);
        bioEt=getActivity().findViewById(R.id.tv_bio_pf);
        postsTv=getActivity().findViewById(R.id.tv_post_pf);
        sendMessage=getActivity().findViewById(R.id.btn_send_message_pf);
        storyTv=getActivity().findViewById(R.id.tv_stories_pf);
        followerTv=getActivity().findViewById(R.id.tv_followers_pf);

        profile_edit=getActivity().findViewById(R.id.iv_edit_pf);
        menu_imv=getActivity().findViewById(R.id.iv_menu_pf);

        postsTv.setOnClickListener(this);
        menu_imv.setOnClickListener(this);
        imageView.setOnClickListener(this);
        profile_edit.setOnClickListener(this);
        webEt.setOnClickListener(this);
        storyTv.setOnClickListener(this);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ChatActivity.class));
            }
        });
        followerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), FollowersActivity.class));
            }
        });


        
    }

    private void loadProfile() {

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String currentUid=user.getUid();

        DocumentReference reference;
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();

        reference=firestore.collection("user").document(currentUid);

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    String nameResult=task.getResult().getString("name");
                    String bioResult=task.getResult().getString("bio");
                    String emailResult=task.getResult().getString("email");
                    String webResult=task.getResult().getString("web");
                    String urlResult=task.getResult().getString("url");
                    String profResult=task.getResult().getString("prof");
                    try {
                        Glide.with(getActivity()).load(urlResult).into(imageView);
                    }catch (Exception e){

                    }

                    nameEt.setText(nameResult);
                    bioEt.setText(bioResult);
                    emailEt.setText(emailResult);
                    webEt.setText(webResult);
                    proEt.setText(profResult);

                }else{
                    Intent intent=new Intent(getActivity(),CreateProfileActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.iv_edit_pf:
                startActivity(new Intent(getActivity(), UpdateProfileActivity.class));
                break;
            case R.id.tv_post_pf:
                startActivity(new Intent(getActivity(), IndividualPostActivity.class));
                break;
            case R.id.iv_menu_pf:
                BottomSheetDialogFragment bottomSheetDialogFragment=new BottomSheetDialogFragment();
                bottomSheetDialogFragment.show(getFragmentManager(),"bottomsheet");
                break;
            case R.id.iv_profile_pf:
                startActivity(new Intent(getActivity(), ImageViewActivity.class));
                break;
            case R.id.tv_website_pf:
                try {
                    String url=webEt.getText().toString();
                    Intent intent=new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);

                }catch (Exception e){
                    Toast.makeText(getActivity(), "Invalid Url", Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.tv_stories_pf:
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent,1);
                break;


        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == 1 || resultCode == RESULT_OK || data != null || data.getData() != null) {
                imageUri = data.getData();

                String url = imageUri.toString();
                Intent intent = new Intent(getActivity(), StoryActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            Toast.makeText(getActivity(), "Error in setting up story", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        loadProfile();
    }
}