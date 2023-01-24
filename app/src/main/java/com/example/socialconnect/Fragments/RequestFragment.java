package com.example.socialconnect.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialconnect.Model.PostModel;
import com.example.socialconnect.Model.ProfileViewModel;
import com.example.socialconnect.Model.RequestModel;
import com.example.socialconnect.R;
import com.example.socialconnect.ShowUserProfileActivity;
import com.example.socialconnect.ViewHolder.PostViewHolder;
import com.example.socialconnect.ViewHolder.ProfileViewHolder;
import com.example.socialconnect.ViewHolder.RequestViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RequestFragment extends Fragment {


    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference databaseReferenceRequests,profileRef,databaseReferenceFollowers;
    RecyclerView recyclerView_request,recyclerView_friend;
    RequestModel requestModel;
    TextView emptyRequest;
    ProgressBar progressBar_empty;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_request, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String curUid= user.getUid();


        databaseReferenceFollowers=database.getReference("Followers").child(curUid);
        databaseReferenceRequests=database.getReference("Requests").child(curUid);
        profileRef=database.getReference("All Users");

        requestModel=new RequestModel();

        emptyRequest=getActivity().findViewById(R.id.empty_request_rv);
        progressBar_empty=getActivity().findViewById(R.id.empty_progress_request_frag);


        recyclerView_friend=getActivity().findViewById(R.id.rv_request_fragment_friends);
        recyclerView_friend.setHasFixedSize(true);
        recyclerView_friend.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView_request=getActivity().findViewById(R.id.rv_request_fragment_request);
        recyclerView_request.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
        recyclerView_request.setHasFixedSize(true);
        recyclerView_request.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<RequestModel> options=new FirebaseRecyclerOptions.Builder<RequestModel>()
                .setQuery(databaseReferenceRequests,RequestModel.class)
                .build();

        FirebaseRecyclerAdapter<RequestModel,RequestViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<RequestModel, RequestViewHolder>(options) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if(getItemCount()==0){
                    recyclerView_request.setVisibility(View.VISIBLE);
                    emptyRequest.setVisibility(View.VISIBLE);
                }else{
                    emptyRequest.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull RequestModel model) {

                holder.setRequest(getActivity(),model.getName(),model.getUrl());

                holder.acceptbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        databaseReferenceFollowers.child(model.getUserid()).setValue(model);
                        databaseReferenceRequests.child(model.getUserid()).removeValue();

                        Toast.makeText(getActivity(), "Request Accepted", Toast.LENGTH_SHORT).show();

                    }
                });

                holder.rejectbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        databaseReferenceRequests.child(model.getUserid()).removeValue();
                        Toast.makeText(getActivity(), "Request Rejected", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=getLayoutInflater().from(parent.getContext()).inflate(R.layout.accep_reject_request_item_design_fragment,parent,false);
                return new RequestViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        recyclerView_request.setAdapter(firebaseRecyclerAdapter);




        FirebaseRecyclerOptions<ProfileViewModel> options1=new FirebaseRecyclerOptions.Builder<ProfileViewModel>()
                .setQuery(profileRef,ProfileViewModel.class)
                .build();

        FirebaseRecyclerAdapter<ProfileViewModel,ProfileViewHolder> firebaseRecyclerAdapter1=new FirebaseRecyclerAdapter<ProfileViewModel, ProfileViewHolder>(options1) {

            public void onDataChanged() {
                super.onDataChanged();
                if(getItemCount()==0){
                    recyclerView_friend.setVisibility(View.INVISIBLE);
                    progressBar_empty.setVisibility(View.VISIBLE);
                }else{
                    recyclerView_friend.setVisibility(View.VISIBLE);
                    progressBar_empty.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onBindViewHolder(@NonNull ProfileViewHolder holder, int position, @NonNull ProfileViewModel model) {
                String id= model.getUid();
                String user=FirebaseAuth.getInstance().getCurrentUser().getUid();



                    holder.setProfile(getActivity(), model.getName(), model.getUid(), model.getProf(), model.getUrl());
                    holder.cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), ShowUserProfileActivity.class);
                            intent.putExtra("url", model.getUrl());
                            intent.putExtra("name", model.getName());
                            intent.putExtra("userid", model.getUid());
                            startActivity(intent);
                        }
                    });
                if(id.equals(user)){
                    holder.cardView.setVisibility(View.GONE);
                }
                }



            @NonNull
            @Override
            public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=getLayoutInflater().from(parent.getContext()).inflate(R.layout.request_rv_design_request_fragment,parent,false);
                return new ProfileViewHolder(view);
            }
        };
//        GridLayoutManager glm=new GridLayoutManager(getActivity(),2,GridLayoutManager.VERTICAL,false);
//        recyclerView_friend.setLayoutManager(glm);

        firebaseRecyclerAdapter1.startListening();
        recyclerView_friend.setAdapter(firebaseRecyclerAdapter1);
    }
}