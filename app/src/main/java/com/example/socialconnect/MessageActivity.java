package com.example.socialconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.socialconnect.Model.MessageModel;
import com.example.socialconnect.Model.ProfileViewModel;
import com.example.socialconnect.ViewHolder.MessageViewHolder;
import com.example.socialconnect.ViewHolder.ProfileViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MessageActivity extends AppCompatActivity {

    ProgressBar empty_progress;
    Button btn;
    ImageButton btnSend,cameraBtn,micBtn;
    EditText editText;
    ImageView userProfile;
    TextView nameTv;
    RecyclerView recyclerView;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference rootref1,rootref2;
    MessageModel messageModel;

    TextView empty_txt;
    String receiver_name,receiver_uid,sender_uid,receiver_url;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        //empty_progress=findViewById(R.id.empty_progress_message_activity);
        btn=findViewById(R.id.btn_send_message);
        btnSend=findViewById(R.id.imageButton_message_activity);
        cameraBtn=findViewById(R.id.camera);
        micBtn=findViewById(R.id.mic);
        editText=findViewById(R.id.editText_message_activity);
        userProfile=findViewById(R.id.iv_profile_message);
        nameTv=findViewById(R.id.user_name_message_tv);
        recyclerView=findViewById(R.id.rv_message_activity);
       // empty_txt=findViewById(R.id.empty_txt_message_activity);

        messageModel=new MessageModel();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String curUid=user.getUid();
        Bundle extras=getIntent().getExtras();

        if(extras!=null){
            receiver_name=extras.getString("name");
            receiver_uid=extras.getString("userid");
            receiver_url=extras.getString("url");
            sender_uid=curUid;

        }else{
            Toast.makeText(this, "User Missing", Toast.LENGTH_SHORT).show();
        }

        Glide.with(this).load(receiver_url).into(userProfile);
        nameTv.setText(receiver_name);

        //send
        rootref1=database.getReference("message").child(sender_uid).child(receiver_uid);
        //retrieve
        rootref2=database.getReference("message").child(receiver_uid).child(sender_uid);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {

        String message= editText.getText().toString();

        Calendar cdate=Calendar.getInstance();
        SimpleDateFormat currdate=new SimpleDateFormat("dd-MM-yyyy");
        final String savedate=currdate.format(cdate.getTime());

        Calendar ctime=Calendar.getInstance();
        SimpleDateFormat curTime=new SimpleDateFormat("HH:mm:ss");
        final String savetime=curTime.format(ctime.getTime());


        if(message.isEmpty()){
            Toast.makeText(this, "Cannot Send Empty message", Toast.LENGTH_SHORT).show();

        }else {
            messageModel.setMessage(message);
            messageModel.setTime(savetime);
            messageModel.setDate(savedate);
            messageModel.setSendUid(sender_uid);
            messageModel.setReceiverUid(receiver_uid);
            messageModel.setType("text");

            String id = rootref1.push().getKey();
            rootref1.child(id).setValue(messageModel);

            String id1 = rootref2.push().getKey();
            rootref2.child(id1).setValue(messageModel);

            editText.setText("");
        }


    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<MessageModel> options1=new FirebaseRecyclerOptions.Builder<MessageModel>()
                .setQuery(rootref1,MessageModel.class)
                .build();

        FirebaseRecyclerAdapter<MessageModel,MessageViewHolder> firebaseRecyclerAdapter1=new FirebaseRecyclerAdapter<MessageModel, MessageViewHolder>(options1) {



            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull MessageModel model) {


                    holder.setMessage(getApplication(), model.getMessage(), model.getTime(), model.getDate(), model.getType(), model.getSendUid(), model.getReceiverUid());


            }

            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=getLayoutInflater().from(parent.getContext()).inflate(R.layout.message_layout,parent,false);
                return new MessageViewHolder(view);
            }
        };


        firebaseRecyclerAdapter1.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter1);
    }
}