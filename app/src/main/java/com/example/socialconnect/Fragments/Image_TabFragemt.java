package com.example.socialconnect.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.socialconnect.Model.PostModel;
import com.example.socialconnect.R;
import com.example.socialconnect.ViewHolder.ImagesFragmentViewHolder;
import com.example.socialconnect.ViewHolder.PostViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Image_TabFragemt extends Fragment {


    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference reference;
    RecyclerView recyclerView;
    TextView empty_txt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_image__tab, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String curUid= user.getUid();

        empty_txt=getActivity().findViewById(R.id.empty_txt_imgf);
        recyclerView=getActivity().findViewById(R.id.rv_images_tab);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        reference=database.getReference("AllImages").child(curUid);

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<PostModel> options=new FirebaseRecyclerOptions.Builder<PostModel>()
                .setQuery(reference,PostModel.class)
                .build();

        FirebaseRecyclerAdapter<PostModel, ImagesFragmentViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<PostModel, ImagesFragmentViewHolder>(options) {
            public void onDataChanged() {
                super.onDataChanged();
                if(getItemCount()==0){
                    recyclerView.setVisibility(View.INVISIBLE);
                    empty_txt.setVisibility(View.VISIBLE);
                }else{
                    recyclerView.setVisibility(View.VISIBLE);
                    empty_txt.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onBindViewHolder(@NonNull ImagesFragmentViewHolder holder, int position, @NonNull PostModel model) {
                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                String curUid=user.getUid();

                final String postKey=getRef(position).getKey();
                holder.setImage(getActivity(),model.getName(), model.getUrl(), model.getPostUri(), model.getTime(), model.getUid(), model.getType(),model.getDesc());

                String postUri=getItem(position).getPostUri();
                String name=getItem(position).getName();
                //    String url=getItem(position).getUrl();
                String time=getItem(position).getTime();
                String type=getItem(position).getType();
                String userid=getItem(position).getUid();

            }

            @NonNull
            @Override
            public ImagesFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view=getLayoutInflater().from(parent.getContext()).inflate(R.layout.images_post_indiviual_design_item,parent,false);
                return new ImagesFragmentViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        GridLayoutManager glm=new GridLayoutManager(getActivity(),3,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(glm);
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }
}