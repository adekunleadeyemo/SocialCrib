package com.logistics.socialcrib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.logistics.Utils.Util;

import java.util.Objects;

public class UserProfile extends AppCompatActivity {

    private ImageView profileImg;
    private TextView following;
    private TextView followers;
    private TextView fname;
    private TextView uname;

    private  TextView userBio;

    private  ImageView userSetting;

    User currentUser;

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
}