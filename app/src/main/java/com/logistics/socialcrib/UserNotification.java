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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.logistics.Model.Notification;
import com.logistics.Model.User;
import com.logistics.Utils.DbUtil;
import com.logistics.Utils.NotificationAdapter;
import com.logistics.Utils.NotificationInterface;
import com.logistics.Utils.Util;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserNotification extends AppCompatActivity implements NotificationInterface {

    private User currentUser;
    private List<User> users;

    RecyclerView rv;

    ImageView bkArr;

    NotificationAdapter myAdapter;

    ProgressBar progressBar;

    LinearLayout recylerLayout;

    TextView noNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_notification);

        rv = findViewById(R.id.notification_recycler_view);

        bkArr = findViewById(R.id.notification_bk);

        progressBar = findViewById(R.id.user_notification_progress);

        recylerLayout = findViewById(R.id.notification_recycler_div);

        noNotification = findViewById(R.id.no_notifications);


        progressBar.setVisibility(View.VISIBLE);
        recylerLayout.setVisibility(View.GONE);




        myAdapter = new NotificationAdapter(getApplicationContext(), this);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(UserNotification.this, UserFeed.class));
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);


        bkArr.setOnClickListener(e -> startActivity(new Intent(UserNotification.this, UserFeed.class)));
    DbUtil.user(DbUtil.currentId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            currentUser = task.getResult().toObject(User.class);
            assert currentUser != null;
            if(currentUser.notifications != null) {
                myAdapter.setNewNotifications(currentUser.notifications);
                currentUser.resetNotification();
                DbUtil.user(currentUser.getUserId()).set(currentUser);
            }
                DbUtil.notifications().
                        whereEqualTo("reciever", DbUtil.currentId()).get().
                        addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        List<Notification> notifications = task.getResult().
                                toObjects(Notification.class).stream().sorted(new Comparator<Notification>() {
                                    @Override
                                    public int compare(Notification o1, Notification o2) {

                                        return Long.compare(o2.getTimestamp().getSeconds(), o1.getTimestamp().getSeconds());
                                    }
                                }).collect(Collectors.toList());
                            myAdapter.setNotifications(notifications);
                            List<String> senders = notifications.stream().map(c -> c.getSender()).collect(Collectors.toList());

                            if(senders.size() > 0){
                                DbUtil.users().whereIn("userId", senders ).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        myAdapter.setUsers(queryDocumentSnapshots.toObjects(User.class));

                                        Thread setupThread = new Thread(() -> { Util.getAllUserImageAsMap(queryDocumentSnapshots.toObjects(User.class))
                                                .addOnCompleteListener(new OnCompleteListener<Map<String, Uri>>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Map<String, Uri>> task) {
                                                        myAdapter.setUserImages(task.getResult());
                                                        rv.setAdapter(myAdapter);
                                                        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                                        rv.setHasFixedSize(true);
                                                        progressBar.setVisibility(View.GONE);
                                                        recylerLayout.setVisibility(View.VISIBLE);
                                                    }
                                                });
                                        });
                                        setupThread.start();

                                    }
                                });
                            }
                            else {
                                progressBar.setVisibility(View.GONE);
                                noNotification.setVisibility(View.VISIBLE);
                            }
                    }
                });
        }
    });



    }
}