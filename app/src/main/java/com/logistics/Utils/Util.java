package com.logistics.Utils;

import android.content.Context;
import android.net.Uri;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.logistics.Model.User;
import com.logistics.socialcrib.R;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

    public static Task<Uri> getIcon (String id){

        return  FirebaseStorage.getInstance().getReference().child("icons").
                child(id).getDownloadUrl();

    }

    public static Task<Uri> getUserImage(String id){
        return FirebaseStorage.getInstance().getReference().child("images").
                child(id).getDownloadUrl();
    }

    public static Task<List<List<Uri>>>  getAllUserImage(List<User> users){


        List<List<Uri>> userImageUri = new ArrayList<>();

            for(int i=0; i<users.size(); i=i+3){

                    List<Uri> row = new ArrayList<>();
                   for (int j=0; j<(users.size()-i); j++){
                       if (j<3) {
                           try {
                               Tasks.await(getUserImage(users.get(i+j).getImgUrl()).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                     @Override
                                     public void onComplete(@NonNull Task<Uri> task) {
                                         row.add(task.getResult());
                                     }

                                 }));
                           } catch (Exception e) {
                               String p = e.getMessage();
                           }


                       }
                       else{
                           break;
                       }

                   }

                userImageUri.add(row);
            }
        return Tasks.forResult(userImageUri);
    }

    public static List<List<String>> generateTopics (){
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


    public static  List<List<Integer>> generateTopicsIcon (){
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


    public static List<List<User>> generateUsersTable(List<User> users) {

        List<List<User>> userTable = new ArrayList<>();

        int len = users.size();
        for(int i=0; i<len; i=i+3){
            List<User> userRow = new ArrayList<>();
            for (int j=0; j<(len-i); j++){
                if (j<3) {
                    userRow.add(users.get(i+j));
                }
                else {
                    break;
                }
            }
            userTable.add(userRow);
        }
        return userTable;
    }

}
