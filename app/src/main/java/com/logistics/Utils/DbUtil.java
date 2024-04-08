package com.logistics.Utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DbUtil {

    public static String currentId(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static DocumentReference user(String id){
        return users().document(id);
    }

    public static CollectionReference users(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static CollectionReference notifications(){
        return FirebaseFirestore.getInstance().collection("notification");
    }

    public static DocumentReference notification(String id){
        return notifications().document(id);
    }
}



//    public static boolean isLoggedIn(){
//        if(currentUserId()!=null){
//            return true;
//        }
//        return false;
//    }
