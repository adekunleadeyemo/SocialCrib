package com.logistics.socialcrib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class NotificationActivity extends AppCompatActivity implements NotificationInterface {

    private User currentUser;
    private List<User> users;

    RecyclerView rv;

    NotificationAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        rv = findViewById(R.id.notification_recycler_view);

        myAdapter = new NotificationAdapter(getApplicationContext(), this);
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
                                        }
                                    });
                                });
                        setupThread.start();

                                }
                            });

                    }
                });
        }
    });



    }
}