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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SelectUsers extends AppCompatActivity implements RecyclerViewInterface {

   LinearLayout btn_click;
    MyAdapter myAdapter;
    LinearLayout userIntroDiv;
    ProgressBar userProgBar;

    TextView actionTxt;
    ProgressBar actionProg;

    User currentUser;

    int selected = 0;

    List<List<User>>userTable;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_users);
        RecyclerView rv = findViewById(R.id.intro_recycler);
        userIntroDiv  = findViewById(R.id.user_intro_div);
        userProgBar = findViewById(R.id.user_intro_progress);
        btn_click =  findViewById(R.id.user_intro_action_btn);
       actionTxt = findViewById(R.id.user_action_btn_load_txt);
        actionProg = findViewById(R.id.user_action_btn_load);
        myAdapter = new MyAdapter(getApplicationContext(), this);
        intent = new Intent(SelectUsers.this, SelectTopics.class);
        intent.putExtras(getIntent().getExtras());

        btn_click.setOnClickListener(e ->{

            actionTxt.setVisibility(View.GONE);
            actionProg.setVisibility(View.VISIBLE);

            OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
                @Override
                public void handleOnBackPressed() {
                }
            };
            this.getOnBackPressedDispatcher().addCallback(this, callback);



            if(selected > 0){
                DbUtil.user(DbUtil.currentId()).set(currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        DbUtil.users().whereIn("userId",currentUser.getFollowing()).
                                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        List<User> followers = task.getResult().toObjects(User.class);
                                        followers.forEach(e -> {
                                            Notification notification = new Notification(e.getUserId()+currentUser.getUserId(),
                                                    currentUser.getUserId(),e.getUserId(),"Followed you!",Timestamp.now());

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
                                                    startActivity(intent);
                                                }
                                            });
                                        });
                                    }
                                });
                    }
                });
            }
            else {
                startActivity(intent);
            }

        });


        DbUtil.user(DbUtil.currentId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                currentUser = task.getResult().toObject(User.class);

            }
        });

        DbUtil.users().whereNotEqualTo("userId",DbUtil.currentId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && task.getResult().size()>0){
                   List<User> users = task.getResult().toObjects(User.class);
                    userTable = Util.generateUsersTable(users);
                    myAdapter.setUserTable(userTable);
                   Thread setUpThread = new Thread( ()-> { Util.getAllUserImage(users).addOnCompleteListener(new OnCompleteListener<List<List<Uri>>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<List<Uri>>> task) {
                            myAdapter.setUserImageTable(task.getResult());
//                            myAdapter.notifyDataSetChanged();
                            rv.setAdapter(myAdapter);
                            rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            rv.setHasFixedSize(true);
                            userProgBar.setVisibility(View.GONE);
                            userIntroDiv.setVisibility(View.VISIBLE);

                        }
                    });
                   });

                   setUpThread.start();


                }
                else {
                    startActivity(intent);
                }
            }
        });

    }


    @Override
    public void onItemClick(int pos, int pos2, String event) {


        if(myAdapter.following == null){
            myAdapter.following = new ArrayList<>();
        }

        if(myAdapter.following.contains( userTable.get(pos).get(pos2).getUserId())){
            myAdapter.following.remove(userTable.get(pos).get(pos2).getUserId());
            currentUser.removeFollowing(userTable.get(pos).get(pos2).getUserId());
            selected = selected - 1;
        }
        else {
            myAdapter.following.add(userTable.get(pos).get(pos2).getUserId());
            selected = selected + 1;
            currentUser.addFollowing(userTable.get(pos).get(pos2).getUserId());
        }

        myAdapter.notifyItemChanged(pos);
    }


}