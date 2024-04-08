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
import java.util.List;
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

        myAdapter = new NotificationAdapter(getApplicationContext(), this);
    DbUtil.user(DbUtil.currentId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            currentUser = task.getResult().toObject(User.class);
            assert currentUser != null;
            if(currentUser.notifications != null) {
                DbUtil.notifications().whereEqualTo("reciever", DbUtil.currentId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        myAdapter.setNotifications(queryDocumentSnapshots.toObjects(Notification.class));
                        DbUtil.users().whereIn("sender", queryDocumentSnapshots.
                                toObjects(Notification.class).stream().
                                map(c -> c.getSender()).collect(Collectors.toList())).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                myAdapter.setUsers(queryDocumentSnapshots.toObjects(User.class));
                                Thread setupThread = new Thread(() -> {
                                    Util.getAllUserImage(queryDocumentSnapshots.toObjects(User.class))
                                            .addOnCompleteListener(new OnCompleteListener<List<List<Uri>>>() {
                                                @Override
                                                public void onComplete(@NonNull Task<List<List<Uri>>> task) {
                                                    List<Uri> uris = new ArrayList<>();
                                                    task.getResult().forEach(t -> uris.addAll(t));
                                                    myAdapter.setUserImages(uris);
                                                    rv.setAdapter(myAdapter);
                                                    rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                                    rv.setHasFixedSize(true);
                                                }
                                            });
                                });
                                setupThread.start();
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

                    }
                });
            }
        }
    });



    }
}