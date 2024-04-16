package com.logistics.socialcrib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.logistics.Model.User;
import com.logistics.Utils.DbUtil;
import com.logistics.Utils.TopicRecycleInterface;
import com.logistics.Utils.TopicsAdapter;
import com.logistics.Utils.Util;

import java.util.Objects;

public class UserProfile extends AppCompatActivity implements TopicRecycleInterface {

    private ImageView profileImg;
    private TextView following;
    private TextView followers;
    private TextView fname;
    private TextView uname;

    private  TextView userBio;

    private  ImageView userSetting;

    RecyclerView topicRv;

    User currentUser;

    TopicsAdapter myAdapter;

    ImageView bkArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        profileImg = findViewById(R.id.profile_img);
        followers = findViewById(R.id.user_followers);
        following = findViewById(R.id.user_following);
        fname = findViewById(R.id.fname);
        uname = findViewById(R.id.uname);
        userBio = findViewById(R.id.bio);
        userSetting = findViewById(R.id.setting_btn);
        topicRv = findViewById(R.id.fav_topic_rv);

        bkArr = findViewById(R.id.user_profile_bk);

        myAdapter = new TopicsAdapter(getApplicationContext(), this);

        bkArr.setOnClickListener( e -> startActivity(new Intent(UserProfile.this, UserFeed.class)));

        DbUtil.user(DbUtil.currentId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(User.class);
                assert currentUser != null;
                String fullName = currentUser.getFirstName() +" "+ currentUser.getLastName();
                fname.setText(fullName);
                uname.setText(currentUser.getUsername());

                if(currentUser.getBio() != null){
                    userBio.setText(currentUser.getBio());
                }

                if(currentUser.getFollowers() != null){
                    followers.setText(String.valueOf(currentUser.getFollowers().size()));
                }
                else {
                    followers.setText("0");
                }

                if(currentUser.getFollowing() != null){
                    following.setText(String.valueOf(currentUser.getFollowing().size()));
                }
                else {
                    following.setText("0");
                }

                myAdapter.setMyTopics(currentUser.getTopics());
                myAdapter.setTopics(Util.generateTopics());
                myAdapter.setTopicsIcon(Util.generateTopicsIcon());
                topicRv.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                topicRv.setHasFixedSize(true);
                topicRv.setAdapter(myAdapter);
                myAdapter.notifyDataSetChanged();

                Util.getUserImage(currentUser.getImgUrl()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .transform(new CenterCrop(), new CircleCrop())
                                .into(profileImg);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Util.toast(getApplicationContext(), "Could not load User!");
            }
        });

        userSetting.setOnClickListener(e -> {
            Intent intent = new Intent(UserProfile.this, UserSettings.class);
//            intent.putExtras(Objects.requireNonNull(getIntent().getExtras()));
            startActivity(intent);
        });
    }

    @Override
    public void itemClick(int pos, String topic) {

    }
}