package com.logistics.socialcrib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.logistics.Model.User;
import com.logistics.Utils.DbUtil;
import com.logistics.Utils.MyAdapter;
import com.logistics.Utils.TopicRecycleInterface;
import com.logistics.Utils.TopicsAdapter;
import com.logistics.Utils.Util;

import java.util.List;

public class SelectTopics extends AppCompatActivity implements TopicRecycleInterface {

    TopicsAdapter myAdapter;
    RecyclerView rv;

    LinearLayout action_btn;

   Intent intent;

   LinearLayout topicDiv;
   ProgressBar topicProgress;


    User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_topics);

        rv = findViewById(R.id.topic_recycleview);
        action_btn = findViewById(R.id.topic_action_btn);

        topicDiv = findViewById(R.id.topic_main_div);

        topicProgress = findViewById(R.id.topic_progress);

        myAdapter = new TopicsAdapter(getApplicationContext(), this);
        rv.setAdapter(myAdapter);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv.setHasFixedSize(true);

        intent = new Intent(SelectTopics.this, SelectLocation.class);

        DbUtil.user(DbUtil.currentId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser =  documentSnapshot.toObject(User.class);
                topicProgress.setVisibility(View.GONE);
                topicDiv.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Util.toast(getApplicationContext(), "Error Loading User");
                topicProgress.setVisibility(View.GONE);
                topicDiv.setVisibility(View.VISIBLE);
            }
        });


        action_btn.setOnClickListener(e -> {

            if(myAdapter.getSelectedTopics() != null && myAdapter.getSelectedTopics().size() > 4){
                currentUser.setTopics(myAdapter.getSelectedTopics());
                DbUtil.user(DbUtil.currentId()).set(currentUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Util.toast(getApplicationContext(), "There was an error completing this");
                    }
                });
            } else if (myAdapter.getSelectedTopics() != null){
                Util.toast(getApplicationContext(), "Select "+(5 - myAdapter.getSelectedTopics().size())+" to continue");
            }
            else {
                Util.toast(getApplicationContext(), "There was an error!");
            }
        });

    }

    @Override
    public void itemClick(int pos, String topic) {

        if(myAdapter.getSelectedTopics().contains(topic)){
            myAdapter.removeSelectedTopics(topic);
        }
        else{
            myAdapter.addSelectedTopics(topic);
        }

        myAdapter.notifyItemChanged(pos);

    }
}