package com.logistics.socialcrib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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

public class User_homepage extends AppCompatActivity {

    ImageView profImage;
    User currentUser;
    ProgressBar userHomeProg;
    LinearLayout userHomeDiv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);


        profImage = findViewById(R.id.user_profile_img);
        userHomeDiv = findViewById(R.id.user_homepage_div);
        userHomeProg = findViewById(R.id.user_homepage_prog);

        profImage.setOnClickListener(e -> {
            Intent intent = new Intent(User_homepage.this, UserProfile.class);
//            intent.putExtras(Objects.requireNonNull(getIntent().getExtras()));
            startActivity(intent);

        });
        DbUtil.user(DbUtil.currentId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(User.class);
                assert currentUser != null;
                Util.getUserImage(currentUser.getImgUrl()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .transform(new CenterCrop(), new CircleCrop())
                                .into(profImage);
                        userHomeProg.setVisibility(View.GONE);
                        userHomeDiv.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Util.toast(getApplicationContext(), "Could not load profile picture");
                        userHomeProg.setVisibility(View.GONE);
                        userHomeDiv.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Util.toast(getApplicationContext(),"Could Not Load current User");
                userHomeProg.setVisibility(View.GONE);
                userHomeDiv.setVisibility(View.VISIBLE);
            }
        });



    }
}