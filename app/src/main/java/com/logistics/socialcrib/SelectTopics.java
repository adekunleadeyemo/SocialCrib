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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
        myAdapter.setTopics(generateTopics());
        myAdapter.setTopicsIcon(generateTopicsIcon());
        rv.setAdapter(myAdapter);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv.setHasFixedSize(true);
        myAdapter.notifyDataSetChanged();

        intent = new Intent(SelectTopics.this, User_homepage.class);

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

        myAdapter.notifyDataSetChanged();

    }

   private List<List<String>> generateTopics (){
        List<List<String>> topics = new ArrayList<>();
        topics.add(Arrays.asList("Nigeria","Instagram"));
        topics.add(Arrays.asList("Bring a Drink","Startup"));
        topics.add(Arrays.asList("Fitness","Health"));
        topics.add(Arrays.asList("Dating","Medication"));
        topics.add(Arrays.asList("Shopping","Product"));
        topics.add(Arrays.asList("Sports","Rap"));
        topics.add(Arrays.asList("Cars","Cartoons"));

        return topics;
    }


    private List<List<Integer>> generateTopicsIcon (){
        List<List<Integer>> topicsIcon = new ArrayList<>();
        topicsIcon.add(Arrays.asList(R.drawable.nigeria,R.drawable.instagram));
        topicsIcon.add(Arrays.asList(R.drawable.bring_a_drink,R.drawable.startup));
        topicsIcon.add(Arrays.asList(R.drawable.fitness,R.drawable.health));
        topicsIcon.add(Arrays.asList(R.drawable.dating,R.drawable.medication));
        topicsIcon.add(Arrays.asList(R.drawable.shopping,R.drawable.product));
        topicsIcon.add(Arrays.asList(R.drawable.sport,R.drawable.rap));
        topicsIcon.add(Arrays.asList(R.drawable.car,R.drawable.cartoon));

        return topicsIcon;
    }

}