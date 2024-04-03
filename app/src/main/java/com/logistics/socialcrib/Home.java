package com.logistics.socialcrib;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.logistics.Utils.Util;
import com.logistics.socialcrib.databinding.ActivityHomeBinding;

import java.util.Objects;

public class Home extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        intent = new Intent(Home.this, Login.class);

        binding.homeActionBtn.setOnClickListener(e -> {
            intent.putExtra(Util.LOGINTYPE, Util.SIGNUP);
            startActivity(intent);
        });

        binding.homeLoginBtn.setOnClickListener(e -> {
            intent.putExtra(Util.LOGINTYPE, Util.LOGIN);
            startActivity(intent);
        });




    }
}