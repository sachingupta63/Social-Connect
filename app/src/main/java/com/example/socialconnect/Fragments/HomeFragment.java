package com.example.socialconnect.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialconnect.Model.PostModel;
import com.example.socialconnect.Model.QuestionMember;
import com.example.socialconnect.PostActivity;
import com.example.socialconnect.R;
import com.example.socialconnect.ReplyActivity;
import com.example.socialconnect.ViewHolder.PostViewHolder;
import com.example.socialconnect.ViewHolder.ViewHolder_Questions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.security.Permission;
import java.util.List;


public class HomeFragment extends Fragment implements View.OnClickListener {


    Button button;
    RecyclerView recyclerView;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference reference,likeref;
    Boolean likeChecker=false;
    DatabaseReference db1,db2,db3;
    ProgressBar empty_progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        button=getActivity().findViewById(R.id.btn_createpost_home);

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        String curUid= user.getUid();

        reference=database.getReference("AllPosts");
        likeref=database.getReference("postlikes");

        empty_progress=getActivity().findViewById(R.id.empty_progressbar_rv);
        recyclerView=getActivity().findViewById(R.id.rv_homeFrag_post);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        db1=database.getReference("AllImages").child(curUid);
        db2=database.getReference("AllVideos").child(curUid);
        db3=database.getReference("AllPosts");



        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_createpost_home:
                startActivity(new Intent(getActivity(), PostActivity.class));
                break;
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<PostModel> options=new FirebaseRecyclerOptions.Builder<PostModel>()
                .setQuery(reference,PostModel.class)
                .build();

        FirebaseRecyclerAdapter<PostModel, PostViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<PostModel, PostViewHolder>(options) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();

                if(getItemCount()==0){
                    recyclerView.setVisibility(View.INVISIBLE);
                    empty_progress.setVisibility(View.VISIBLE);
                }else{
                    recyclerView.setVisibility(View.VISIBLE);
                    empty_progress.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull PostModel model) {
                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                String curUid=user.getUid();

                final String postKey=getRef(position).getKey();
                holder.setPost(getActivity(),model.getName(), model.getUrl(), model.getPostUri(), model.getTime(), model.getUid(), model.getType(),model.getDesc());

                String postUri=getItem(position).getPostUri();
                String name=getItem(position).getName();
            //    String url=getItem(position).getUrl();
                String time=getItem(position).getTime();
                String type=getItem(position).getType();
                String userid=getItem(position).getUid();



                holder.likeChecker(postKey);

                holder.moreOptionbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(name,postUri,time,userid,type);
                    }
                });


                holder.likebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        likeChecker=true;
                        likeref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(likeChecker.equals(true)){
                                    if(snapshot.child(postKey).hasChild(curUid)){
                                        likeref.child(postKey).child(curUid).removeValue();
                                        //deleteTime(time);
                                        likeChecker=false;
                                    }else{
                                        likeref.child(postKey).child(curUid).setValue(true);

                                        likeChecker=false;


                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view=getLayoutInflater().from(parent.getContext()).inflate(R.layout.post_rv_home_item_design,parent,false);
                return new PostViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public void showDialog(String name,String postUrl,String time,String userid,String type){
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View view=inflater.inflate(R.layout.options_layout_design,null);

        TextView download=view.findViewById(R.id.tv_download_post);
        TextView share=view.findViewById(R.id.tv_share_post);
        TextView delete=view.findViewById(R.id.tv_delete_post);
        TextView copyUrl=view.findViewById(R.id.tv_copy_url_post);


        AlertDialog dialog=new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
        dialog.show();

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String curUid=user.getUid();

        if(curUid.equals(userid)){
            delete.setVisibility(View.VISIBLE);
        }else{
            delete.setVisibility(View.GONE);
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    deletePost(db1,time);
                    deletePost(db2,time);
                    deletePost(db3,time);

                StorageReference reference= FirebaseStorage.getInstance().getReference(postUrl);
                reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                    }
                });

                    dialog.dismiss();
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PermissionListener permissionListener=new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(postUrl));
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                        request.setTitle("Download");

                        if(type.equals("iv")){
                            request.setDescription("Downloading image...");
                        }else{
                            request.setDescription("Downloading video...");
                        }
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalFilesDir(getActivity(),Environment.DIRECTORY_DOWNLOADS,name+System.currentTimeMillis()+".jpg");

                        DownloadManager manager=(DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                        manager.enqueue(request);

                        Toast.makeText(getActivity(), "Downloading..", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();

                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {

                        Toast.makeText(getActivity(), "No permissions", Toast.LENGTH_SHORT).show();
                    }
                };

                TedPermission.create()
                        .setPermissionListener(permissionListener)
                        .setPermissions(Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE)
                        .check();



            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareText=name+"\n"+"\n"+ postUrl;
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_TEXT,shareText);
                intent.setType("text/plain");
                startActivity(intent);
                dialog.dismiss();
            }
        });

        copyUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cp= (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip=ClipData.newPlainText("String",postUrl);
                cp.setPrimaryClip(clip);
                clip.getDescription();
                Toast.makeText(getActivity(), "Post Url Copied", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

    }

    public void deletePost(DatabaseReference db,String time){
        Query query=db.orderByChild("time").equalTo(time);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    dataSnapshot.getRef().removeValue();

                    Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}