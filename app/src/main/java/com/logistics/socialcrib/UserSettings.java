package com.logistics.socialcrib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.logistics.Model.User;
import com.logistics.Utils.DbUtil;
import com.logistics.Utils.Util;

public class UserSettings extends AppCompatActivity {

    private ImageView profImg;
    private TextView fName;
    private TextView userName;

    LinearLayout logoutBtn;

    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        profImg = findViewById(R.id.setting_profile_img);
        fName = findViewById(R.id.setting_name);
        userName = findViewById(R.id.setting_uname);
        logoutBtn = findViewById(R.id.setting_action);

        DbUtil.user(DbUtil.currentId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(User.class);
                assert currentUser != null;
                String fullName = currentUser.getFirstName() +" "+ currentUser.getLastName();
                fName.setText(fullName);
                userName.setText(currentUser.getUsername());


                Util.getUserImage(currentUser.getImgUrl()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext())
                                .load(uri)
                                .transform(new CenterCrop(), new CircleCrop())
                                .into(profImg);
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

        logoutBtn.setOnClickListener(e -> {

            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(UserSettings.this, Home.class);
            startActivity(intent);
        });

    }
}