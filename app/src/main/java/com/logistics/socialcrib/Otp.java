package com.logistics.socialcrib;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.logistics.Model.User;
import com.logistics.Utils.DbUtil;
import com.logistics.Utils.Util;
import com.logistics.socialcrib.databinding.ActivityHomeBinding;
import com.logistics.socialcrib.databinding.ActivityOtpBinding;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Otp extends AppCompatActivity {

    private ActivityOtpBinding binding;
    private int otpInputPos = 0;
    private FirebaseAuth mAuth;

    private String verificationCode;

    private Intent intent;
    PhoneAuthProvider.ForceResendingToken  resendingToken;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
       intent =  new Intent(Otp.this, Verification.class);
       intent.putExtras(Objects.requireNonNull(getIntent().getExtras()));

        FirebaseAuth.getInstance().getFirebaseAuthSettings().forceRecaptchaFlowForTesting(true);

        mAuth = FirebaseAuth.getInstance();
        binding.otpInput1.addTextChangedListener(textWatcher);
        binding.otpInput2.addTextChangedListener(textWatcher);
        binding.otpInput3.addTextChangedListener(textWatcher);
        binding.otpInput4.addTextChangedListener(textWatcher);
        binding.otpInput5.addTextChangedListener(textWatcher);
        binding.otpInput6.addTextChangedListener(textWatcher);

        sendOtp(Objects.requireNonNull(getIntent().getExtras()).getString("phoneNumber"),false);


        binding.otpResendBtn.setOnClickListener( e -> {
            sendOtp(Objects.requireNonNull(getIntent().getExtras()).getString("phoneNumber"),true);
        });



        binding.otpNavBk.setOnClickListener(e -> {
            Intent intent =  new Intent(Otp.this, Login.class);
            intent.putExtras(getIntent().getExtras());
            startActivity(intent);
        });
    }

    public void sendOtp(String phoneNumber, boolean resendEnabled){
        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(mAuth).
                setPhoneNumber(phoneNumber).setTimeout(60L, TimeUnit.SECONDS).
                setActivity(this)
                .setCallbacks(mCallbacks);
        if(resendEnabled){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }
        else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }

    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {


        }

        @Override
        public void afterTextChanged(Editable s) {
            String otpValue = binding.otpInput1.getText().toString() + binding.otpInput2.getText().toString()+
                    binding.otpInput3.getText().toString()+ binding.otpInput4.getText().toString()+
                    binding.otpInput5.getText().toString()+ binding.otpInput6.getText().toString();

            if(otpValue.length() == 6){
                binding.otpProgressBar.setVisibility(View.VISIBLE);
                binding.otpDiv.setVisibility(View.INVISIBLE);
                verifyWithCode(otpValue);
            }
            else {
                if(s.length() > 0){
                    switch (otpInputPos){
                        case 0:
                            otpInputPos = 1;
                            binding.otpInput1.setBackground(getResources().getDrawable(R.drawable.otp_bg_active));
                            Util.showKeyboard(binding.otpInput2, getApplicationContext());;
                            break;
                        case 1:
                            otpInputPos = 2;
                            binding.otpInput2.setBackground(getResources().getDrawable(R.drawable.otp_bg_active));
                            Util.showKeyboard(binding.otpInput3, getApplicationContext());
                            break;
                        case 2:
                            otpInputPos = 3;
                            binding.otpInput3.setBackground(getResources().getDrawable(R.drawable.otp_bg_active));
                            Util.showKeyboard(binding.otpInput4, getApplicationContext());
                            break;
                        case 3:
                            otpInputPos = 4;
                            binding.otpInput4.setBackground(getResources().getDrawable(R.drawable.otp_bg_active));
                            Util.showKeyboard(binding.otpInput5, getApplicationContext());
                            break;
                        case 4:
                            otpInputPos = 5;
                            binding.otpInput5.setBackground(getResources().getDrawable(R.drawable.otp_bg_active));
                            Util.showKeyboard(binding.otpInput6, getApplicationContext());
                            break;
                        default:
                            binding.otpInput6.setBackground(getResources().getDrawable(R.drawable.otp_bg_active));
                            break;
                    }
                }
            }
        }
    };


    public void verifyWithCode(String otpValue){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otpValue);
        signInWithPhoneAuthCredential(credential);
    }



    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            // add something here
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCode = s;
            resendingToken = forceResendingToken;
            binding.otpProgressBar.setVisibility(View.INVISIBLE);
            binding.otpDiv.setVisibility(View.VISIBLE);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            Util.showKeyboard(binding.otpInput1, getApplicationContext());
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    DbUtil.user(DbUtil.currentId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult().exists()) {
                                intent.putExtra(Util.LOGINTYPE, Util.LOGIN);
                                binding.otpProgressBar.setVisibility(View.INVISIBLE);
                                binding.otpDiv.setVisibility(View.VISIBLE);
                                startActivity(intent);
                            } else {
                                intent.putExtra(Util.LOGINTYPE, Util.SIGNUP);
                                startActivity(intent);

                            }
                        }
                    });
                }
            });
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_DEL){
            switch (otpInputPos){
                case 1:
                    otpInputPos = 0;
                    binding.otpInput2.setBackground(getResources().getDrawable(R.drawable.text_bg));
                    Util.showKeyboard(binding.otpInput1, getApplicationContext());
                    break;
                case 2:
                    otpInputPos = 1;
                    binding.otpInput3.setBackground(getResources().getDrawable(R.drawable.text_bg));
                    Util.showKeyboard(binding.otpInput2, getApplicationContext());
                    break;
                case 3:
                    otpInputPos = 2;
                    binding.otpInput4.setBackground(getResources().getDrawable(R.drawable.text_bg));
                    Util.showKeyboard(binding.otpInput3, getApplicationContext());
                    break;
                case 4:
                    otpInputPos = 3;
                    binding.otpInput5.setBackground(getResources().getDrawable(R.drawable.text_bg));
                    Util.showKeyboard(binding.otpInput4, getApplicationContext());
                    break;
                case 5:
                    otpInputPos = 4;
                    binding.otpInput6.setBackground(getResources().getDrawable(R.drawable.text_bg));
                    Util.showKeyboard(binding.otpInput5, getApplicationContext());
                    break;
                default:
                    break;
            }
            return true;
        }
        else {
            return super.onKeyUp(keyCode, event);
        }

    }
}
