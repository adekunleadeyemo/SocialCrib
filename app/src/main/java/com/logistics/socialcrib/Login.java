package com.logistics.socialcrib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.logistics.Utils.Util;
import com.logistics.socialcrib.databinding.ActivityLoginBinding;
import com.logistics.socialcrib.databinding.ActivityOtpBinding;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private Intent intent;

    Long timeoutSeconds = 60L;


    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        intent = new Intent(Login.this, Otp.class);
        intent.putExtras(Objects.requireNonNull(getIntent().getExtras()));

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        Util.showKeyboard(binding.loginInput, getApplicationContext());

        if(Objects.equals(Objects.requireNonNull(getIntent().
                getExtras()).getString(Util.LOGINTYPE),Util.SIGNUP)){
            binding.loginH1.setText(R.string.login_h1a);
            binding.loginH1Img.setImageResource(R.drawable.confretti);
        }
        else{
            binding.loginH1.setText(R.string.login_h1);
            binding.loginH1Img.setImageResource(R.drawable.wave_hand);
        }

        binding.loginCountrycode.registerCarrierNumberEditText(binding.loginInput);




        binding.loginActionBtn.setOnClickListener(e -> {
            if (!binding.loginCountrycode.isValidFullNumber()) {
                binding.loginInput.setError("Phone Number is Invalid!");
            } else {
            intent.putExtra("phoneNumber", binding.loginCountrycode.getFullNumberWithPlus());
            startActivity(intent);
            }
        });

        binding.loginNavBk.setOnClickListener(e -> startActivity(new Intent(Login.this, Home.class)));
    }


}