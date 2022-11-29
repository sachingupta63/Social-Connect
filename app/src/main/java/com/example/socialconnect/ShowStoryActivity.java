package com.example.socialconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.socialconnect.Model.StoryModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class ShowStoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    int counter = 0;
    ImageView imageViewShowStory, imageViewUrl;
    TextView textView;

    List<String> postUri;
    List<String> url;
    List<String> username;

    StoriesProgressView storiesProgressView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    String userId;
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_story);

        storiesProgressView = findViewById(R.id.stories);
        imageViewShowStory = findViewById(R.id.iv_storyView);
        imageViewUrl = findViewById(R.id.iv_profile_ss);
        textView = findViewById(R.id.tv_username_ss);

        View reverse = findViewById(R.id.view_prev);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storiesProgressView.reverse();

            }
        });
        reverse.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                storiesProgressView.pause();
                return false;
            }
        });

        reverse.setOnTouchListener(onTouchListener);

        View next = findViewById(R.id.view_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storiesProgressView.skip();

            }
        });
        next.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                storiesProgressView.pause();
                return false;
            }
        });

        next.setOnTouchListener(onTouchListener);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("uid");
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

        reference = database.getReference("story").child(userId);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getStories(userId);
    }

    private void getStories(String userId) {

        postUri = new ArrayList<>();
        username = new ArrayList<>();
        url = new ArrayList<>();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                postUri.clear();
                url.clear();
                username.clear();

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    StoryModel model = snapshot1.getValue(StoryModel.class);

                    postUri.add(model.getPostUri());
                    url.add(model.getUrl());
                    username.add(model.getName());
                }

                storiesProgressView.setStoriesCount(postUri.size());
                storiesProgressView.setStoryDuration(5000L);
                storiesProgressView.setStoriesListener(ShowStoryActivity.this);
                storiesProgressView.startStories(counter);
                Glide.with(ShowStoryActivity.this).load(postUri.get(counter)).into(imageViewShowStory);
                Glide.with(ShowStoryActivity.this).load(url.get(counter)).into(imageViewUrl);
                textView.setText(username.get(counter));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onNext() {
        Glide.with(ShowStoryActivity.this).load(postUri.get(++counter)).into(imageViewShowStory);
    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0)
            return;
        Glide.with(ShowStoryActivity.this).load(postUri.get(--counter)).into(imageViewShowStory);

    }

    @Override
    public void onComplete() {
        finish();

    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();

    }
}