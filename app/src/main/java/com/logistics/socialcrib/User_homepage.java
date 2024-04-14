package com.logistics.socialcrib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.logistics.Model.User;
import com.logistics.Utils.DbUtil;
import com.logistics.Utils.MyAdapter;
import com.logistics.Utils.RecyclerViewInterface;
import com.logistics.Utils.Util;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class User_homepage extends AppCompatActivity  implements RecyclerViewInterface {

    ImageView profImage;
    User currentUser;
    List<User> users;
    ProgressBar userHomeProg;
    LinearLayout userHomeDiv;
    MyAdapter myAdapter;
    TextView notification;
    ImageView notificationIcon;
    RecyclerView homePagRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);


        profImage = findViewById(R.id.user_profile_img);
        userHomeDiv = findViewById(R.id.user_homepage_div);
        userHomeProg = findViewById(R.id.user_homepage_prog);
        notificationIcon = findViewById(R.id.user_notification);
        notification = findViewById(R.id.notification_active);

        homePagRv = findViewById(R.id.user_homepage_rv);

        myAdapter = new MyAdapter(getApplicationContext(), this);

        profImage.setOnClickListener(e -> {
            Intent intent = new Intent(User_homepage.this, UserProfile.class);
            startActivity(intent);

        });

        notificationIcon.setOnClickListener(e -> startActivity(new Intent(User_homepage.this, NotificationActivity.class)));

            DbUtil.users().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    users = queryDocumentSnapshots.toObjects(User.class);
                    currentUser = users.stream().filter(u -> u.getUserId().equals(DbUtil.currentId())).findFirst().get();

                    if(currentUser.getNotifications() != null && currentUser.getNotifications().size() > 0){
                        notification.setVisibility(View.VISIBLE);
                        if(currentUser.getNotifications().size() > 9){
                            notification.setText("9+");
                        }
                        else {
                            notification.setText(String.valueOf(currentUser.getNotifications().size()));
                        }
                    }

                    List<User> following = users.stream().filter(u -> u.getFollowers() == null | (u.getFollowers() != null && !u.getFollowers().
                            contains(currentUser.getUserId()) && !Objects.equals(u.getUserId(), currentUser.getUserId()))).collect(Collectors.toList());
                    myAdapter.setUserTable(Util.generateUsersTable(following));
                    Thread setUpThread = new Thread( ()-> {  Util.getAllUserImage(following).addOnCompleteListener(new OnCompleteListener<List<List<Uri>>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<List<Uri>>> task) {
                            myAdapter.setUserImageTable(task.getResult());
                            homePagRv.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                            myAdapter.notifyDataSetChanged();
                            homePagRv.setAdapter(myAdapter);
                            homePagRv.setHasFixedSize(true);

                            userHomeProg.setVisibility(View.GONE);
                            userHomeDiv.setVisibility(View.VISIBLE);
                        }
                    });

                    });

                    setUpThread.start();
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

                    Util.toast(getApplicationContext(), "Could Not Load current User");
                    userHomeProg.setVisibility(View.GONE);
                    userHomeDiv.setVisibility(View.VISIBLE);
                }
            });



    }

    @Override
    public void onItemClick(int pos, int pos2, String s) {

    }
}