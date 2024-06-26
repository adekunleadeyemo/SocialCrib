package com.logistics.socialcrib;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.RoundedCorner;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.logistics.Model.User;
import com.logistics.Utils.DbUtil;
import com.logistics.Utils.Util;
import com.logistics.socialcrib.databinding.ActivityUserDetailsBinding;
import com.logistics.socialcrib.databinding.ActivityVerificationBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.Random;


public class UserDetails extends AppCompatActivity {

    private Intent intent;
    private User newUser;
    private ActivityUserDetailsBinding binding;

    private  Uri userImgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

         newUser = new User(DbUtil.currentId(), Objects.requireNonNull(getIntent().getExtras()).
                               getString("phoneNumber"), Timestamp.now());
        intent = new Intent(UserDetails.this, UserIntro.class);
        intent.putExtras(Objects.requireNonNull(getIntent().getExtras()));

        genUserName();

        binding.userUnameBtn.setOnClickListener(e -> genUserName());

        binding.userImgFromFile.setOnClickListener(e -> {
            binding.userImg.setVisibility(View.INVISIBLE);
            binding.userImgLoad.setVisibility(View.VISIBLE);
            imageChooser();
            binding.userImgLoad.setVisibility(View.INVISIBLE);
            binding.userImg.setVisibility(View.VISIBLE);
        });

        binding.userFname.addTextChangedListener(textWatcher);
        binding.userLname.addTextChangedListener(textWatcher);
        binding.userUname.addTextChangedListener(textWatcher);
        binding.userAge.addTextChangedListener(textWatcher);
        binding.userBio.addTextChangedListener(textWatcher);



        binding.userActionBtn.setOnClickListener(e -> {
            binding.userActionBtn.setVisibility(View.INVISIBLE);
            binding.userActionBtnLoad.setVisibility(View.VISIBLE);
            int userAge = 0;



            if(binding.userAge.getText().length() > 1){
                userAge = Integer.parseInt(binding.userAge.getText().toString());
            }
            if(binding.userFname.getText().length() < 3){
                binding.userFname.setError("Enter a valid First Name");
            }
            else if(binding.userLname.getText().length() < 3){
                binding.userLname.setError("Enter a valid Last Name");
            }
            else if(binding.userUname.getText().length() < 3){
                binding.userUname.setError("Enter a valid Username");
            }
            else if(userAge <18){
                binding.userAge.setError("You are under age");
            } else {
                DbUtil.user(DbUtil.currentId()).set(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (userImgUri != null) {
                                uploadToBucket(userImgUri, newUser.getUserId());
                            }
                            Util.toast(getApplicationContext(), "successfully updated");
                        }
                    }
                });
            }

            binding.userActionBtnLoad.setVisibility(View.INVISIBLE);
            binding.userActionBtn.setVisibility(View.VISIBLE);
        });

    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            newUser.setFirstName(binding.userFname.getText().toString());
            newUser.setLastName(binding.userLname.getText().toString());
            newUser.setUsername(binding.userUname.getText().toString());
            if(binding.userAge.getText().toString().length() > 1){
                newUser.setAge(Integer.parseInt(binding.userAge.getText().toString()));
            }
            newUser.setBio(binding.userBio.getText().toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void imageChooser()
    {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        launchSomeActivity.launch(i);
    }

    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult( new ActivityResultContracts
                    .StartActivityForResult(),result -> {
                 if(result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        userImgUri = data.getData();

                        Glide.with(getApplicationContext())
                                .load(userImgUri)
//                                .transform(new CenterCrop(), new RoundedCorners(50))
                                .transform(new CenterCrop(), new CircleCrop())
                                .into(binding.userImg);
                    }
                }
            });






        private void uploadToBucket(Uri imgUri, String imgSrc){

            FirebaseApp.initializeApp(this);
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imagesRef = storageRef.child("images/"+imgSrc);
            imagesRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Util.toast(getApplicationContext(), "File Was Uploaded successfully");
                    startActivity(intent);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    binding.userActionBtnLoad.setVisibility(View.INVISIBLE);
                    binding.userActionBtn.setVisibility(View.VISIBLE);
                    Util.toast(getApplicationContext(), "File was not uploaded");
                }
            });
        }

    private  void genUserName(){
        String alpha = "abcdefghijklmnopqrstuvwxyz";
        String num = "0123456789";
        StringBuilder output = new StringBuilder();
        Random rnd = new Random();

        String fName = binding.userFname.getText().toString();
        String lName = binding.userLname.getText().toString();
        if(!fName.isEmpty() && !lName.isEmpty()){
            alpha = fName+lName;
        }

        for(int i =0; i<4; i++){
            char c = alpha.charAt(rnd.nextInt(alpha.length()));
            output.append(c);
        }
        for(int i =0; i<2; i++){
            char c = num.charAt(rnd.nextInt(num.length()));
            output.append(c);
        }


        binding.userUname.setText(output.toString());
    }
}

