package com.example.socialconnect.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.socialconnect.AskActivity;
import com.example.socialconnect.PrivacyActivity;
import com.example.socialconnect.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AskFragment extends Fragment implements View.OnClickListener {

    FloatingActionButton floatingActionButton;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    DocumentReference reference;
    ImageView profile_iv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_ask, container, false);



        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        floatingActionButton=getActivity().findViewById(R.id.floatingActionButton);
        profile_iv=getActivity().findViewById(R.id.iv_profile_af);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String curUid=user.getUid();

        reference=db.collection("user").document(curUid);

        profile_iv.setOnClickListener(this);
        floatingActionButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.floatingActionButton:
                startActivity(new Intent(getActivity(), AskActivity.class));
                break;
            case R.id.iv_profile_af:
                break;
        }


    }

    @Override
    public void onStart() {
        super.onStart();

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists()){
                    String url=task.getResult().getString("url");
                    try {
                        Glide.with(getActivity()).load(url).into(profile_iv);
                    }catch (Exception e){
                        Toast.makeText(getActivity(), "Error in Loading Image", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}