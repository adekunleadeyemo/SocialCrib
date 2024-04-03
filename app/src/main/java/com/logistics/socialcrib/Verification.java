package com.logistics.socialcrib;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.logistics.Utils.Util;
import com.logistics.socialcrib.databinding.ActivityVerificationBinding;

import java.util.Objects;

public class Verification extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityVerificationBinding binding = ActivityVerificationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.verActionBtn.setOnClickListener(e -> goToNextActivity());

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                intent = new Intent(Verification.this, Login.class);
                intent.putExtras(Objects.requireNonNull(getIntent().getExtras()));
                startActivity(intent);
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);

    }

    void goToNextActivity(){
        if(Objects.equals(Objects.requireNonNull(getIntent().getExtras()).getString(Util.LOGINTYPE), Util.LOGIN)){
            intent = new Intent(Verification.this, UserIntro.class);
            intent.putExtras(Objects.requireNonNull(getIntent().getExtras()));
            startActivity(intent);
        }
        else {
            intent = new Intent(Verification.this, UserDetails.class);
            intent.putExtras(Objects.requireNonNull(getIntent().getExtras()));
            startActivity(intent);
        }
    }
}