package com.logistics.socialcrib;

import androidx.activity.OnBackPressedCallback;
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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.logistics.Model.Notification;
import com.logistics.Model.User;
import com.logistics.Utils.DbUtil;
import com.logistics.Utils.MyAdapter;
import com.logistics.Utils.RecyclerViewInterface;
import com.logistics.Utils.Util;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserFeed extends AppCompatActivity  implements RecyclerViewInterface {

    ImageView profImage;
    User currentUser;
    List<User> users;
    ProgressBar userHomeProg;
    LinearLayout userHomeDiv;
    MyAdapter myAdapter;
    TextView notification;
    ImageView notificationIcon;
    RecyclerView homePagRv;

    TextView followImageTxt;
    LinearLayout followLoadImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);


        profImage = findViewById(R.id.user_profile_img);
        userHomeDiv = findViewById(R.id.user_homepage_div);
        userHomeProg = findViewById(R.id.user_homepage_prog);
        notificationIcon = findViewById(R.id.user_notification);
        notification = findViewById(R.id.notification_active);

        homePagRv = findViewById(R.id.user_homepage_rv);

        followImageTxt = findViewById(R.id.follow_image_div);
        followLoadImage = findViewById(R.id.follow_image_loading);

        myAdapter = new MyAdapter(getApplicationContext(), this);

        profImage.setOnClickListener(e -> {
            Intent intent = new Intent(UserFeed.this, UserProfile.class);
            startActivity(intent);

        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {

            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

        notificationIcon.setOnClickListener(e -> startActivity(new Intent(UserFeed.this, UserNotification.class)));

            DbUtil.users().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                    currentUser = queryDocumentSnapshots.toObjects(User.class).stream()
                            .filter(u -> u.getUserId().equals(DbUtil.currentId())).findFirst().get();

                    if(currentUser.getNotifications() != null && currentUser.getNotifications().size() > 0){
                        notification.setVisibility(View.VISIBLE);
                        if(currentUser.getNotifications().size() > 9){
                            notification.setText("9+");
                        }
                        else {
                            notification.setText(String.valueOf(currentUser.getNotifications().size()));
                        }
                    }

                    users = queryDocumentSnapshots.toObjects(User.class).stream().filter(u ->  !Objects.equals(u.getUserId(), currentUser.getUserId()) &&
                            (u.getFollowers() == null | (u.getFollowers() != null && !u.getFollowers().
                            contains(currentUser.getUserId())))).collect(Collectors.toList());
                    if (users.size() > 0){
                        myAdapter.setUserTable(Util.generateUsersTable(users));
                        Thread setUpThread = new Thread( ()-> {  Util.getAllUserImage(users).addOnCompleteListener(new OnCompleteListener<List<List<Uri>>>() {
                            @Override
                            public void onComplete(@NonNull Task<List<List<Uri>>> task) {
                                myAdapter.setUserImageTable(task.getResult());
                                homePagRv.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                                myAdapter.notifyDataSetChanged();
                                homePagRv.setAdapter(myAdapter);
                                homePagRv.setHasFixedSize(true);

                                userHomeProg.setVisibility(View.GONE);
                                userHomeDiv.setVisibility(View.VISIBLE);
                                followLoadImage.setVisibility(View.GONE);
                                homePagRv.setVisibility(View.VISIBLE);
                            }
                        });

                        });

                        setUpThread.start();

                    }
                    else {
                        followLoadImage.setVisibility(View.GONE);
                        followImageTxt.setVisibility(View.GONE);
                    }

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



        followLoadImage.setVisibility(View.VISIBLE);
        homePagRv.setVisibility(View.GONE);
        currentUser.addFollowing(users.get((pos*3)+pos2).getUserId());

        DbUtil.user(currentUser.getUserId()).set(currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    DbUtil.user(users.get((pos*3)+pos2).getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                         @Override
                         public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                            User e = task.getResult().toObject(User.class);
                                        Notification notification = new Notification(e.getUserId()+currentUser.getUserId(),
                                        currentUser.getUserId(),e.getUserId(),"Followed you!", Timestamp.now());

                                        e.addNotification(notification.getId());
                                        e.addFollower(currentUser.getUserId());

                                        DbUtil.notification(notification.getId()).set(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                            }
                                        });
                                        DbUtil.user(e.getUserId()).set(e).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful() && users.size()>1){
                                                    myAdapter.removeUser(pos,pos2);
                                                    users.remove((pos*3)+pos2);
                                                    myAdapter.notifyDataSetChanged();
                                                    followLoadImage.setVisibility(View.GONE);
                                                    homePagRv.setVisibility(View.VISIBLE);
                                                } else if (task.isSuccessful()) {
                                                    followLoadImage.setVisibility(View.GONE);
                                                    followImageTxt.setVisibility(View.GONE);
                                                    myAdapter.removeUser(pos,pos2);
                                                    users.remove((pos*3)+pos2);
                                                } else {
                                                    followLoadImage.setVisibility(View.GONE);
                                                    homePagRv.setVisibility(View.VISIBLE);
                                                    Util.toast(getApplicationContext(), task.getException().getMessage());
                                                }

                                            }
                                        });
                                }
                            });

                }
                else{
                    Util.toast(getApplicationContext(), task.getException().getMessage());
                }
            }
        });

    }
}