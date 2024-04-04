package com.logistics.Utils;

import android.content.Context;
import android.net.Uri;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.logistics.Model.User;

import java.util.List;

public class Util {

    public static final String LOGINTYPE = "logintype";
    public static final String LOGIN = "login";
    public static final String SIGNUP = "signup";

    public static void showKeyboard(EditText otpInput, Context context) {
        otpInput.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) context.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(otpInput, InputMethodManager.SHOW_IMPLICIT);
    }

    public static  void toast(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    public static StorageReference imgUrl(List<List<User>> userTable, int pos1, int pos2){

       return  FirebaseStorage.getInstance().getReference().child("images").
                child(userTable.get(pos1).get(pos2).getImgUrl());

    }

    public static Task<Uri> getEmptyProfile (){

        return  FirebaseStorage.getInstance().getReference().child("images").
                child("empty_profile.jpeg").getDownloadUrl();

    }
}
