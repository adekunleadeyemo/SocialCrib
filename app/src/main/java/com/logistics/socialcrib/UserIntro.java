package com.logistics.socialcrib;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import com.logistics.Model.User;
import com.logistics.Utils.DbUtil;
import com.logistics.Utils.MyAdapter;
import com.logistics.Utils.RecyclerViewInterface;
import com.logistics.Utils.Util;
import com.logistics.socialcrib.databinding.ActivityUserIntroBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserIntro extends AppCompatActivity implements RecyclerViewInterface {

    TextView btn_click;
    MyAdapter myAdapter;

    LinearLayout userIntroDiv;
    ProgressBar userProgBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_intro);
        RecyclerView rv = findViewById(R.id.intro_recycler);
        userIntroDiv  = findViewById(R.id.user_intro_div);
        userProgBar = findViewById(R.id.user_intro_progress);
        btn_click =  findViewById(R.id.user_intro_action_btn);
        myAdapter = new MyAdapter(getApplicationContext(), this);

        DbUtil.users().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    myAdapter.setUsers(task.getResult().toObjects(User.class));
                    rv.setAdapter(myAdapter);
                    rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    rv.setHasFixedSize(true);
                    myAdapter.notifyDataSetChanged();
                    userProgBar.setVisibility(View.INVISIBLE);
                    userIntroDiv.setVisibility(View.VISIBLE);
                }
            }
        });

    }


    @Override
    public void onItemClick(int position, String event) {

    }


}