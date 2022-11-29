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
import com.example.socialconnect.Model.StoryModel;
import com.example.socialconnect.R;
import com.example.socialconnect.ViewHolder.ImagesFragmentViewHolder;
import com.example.socialconnect.ViewHolder.StoryFragmentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class StoriesTabFragment extends Fragment {

    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference reference;
    RecyclerView recyclerView;
    TextView empty_txt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stories_tab, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String curUid= user.getUid();

        empty_txt=getActivity().findViewById(R.id.empty_txt_storyf);
        recyclerView=getActivity().findViewById(R.id.rv_stories_tab);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        reference=database.getReference("story").child(curUid);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<StoryModel> options=new FirebaseRecyclerOptions.Builder<StoryModel>()
                .setQuery(reference,StoryModel.class)
                .build();

        FirebaseRecyclerAdapter<StoryModel, StoryFragmentViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<StoryModel, StoryFragmentViewHolder>(options) {
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
            protected void onBindViewHolder(@NonNull StoryFragmentViewHolder holder, int position, @NonNull StoryModel model) {
                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                String curUid=user.getUid();


                holder.setStory(getActivity(),model.getPostUri(), model.getName(), model.getTimeUpload(), model.getType(), model.getCaption(), model.getUrl(),model.getUid(), model.getTimeEnd());



            }

            @NonNull
            @Override
            public StoryFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view=getLayoutInflater().from(parent.getContext()).inflate(R.layout.images_post_indiviual_design_item,parent,false);
                return new StoryFragmentViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        GridLayoutManager glm=new GridLayoutManager(getActivity(),3,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(glm);
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}