package com.logistics.Utils;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.logistics.Model.User;
import com.logistics.socialcrib.R;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyviewHolder> {

    Context context;
    List<User> users;

    Map<Integer, Boolean> following = new HashMap<>();

    public Map<Integer, Boolean> getFollowing() {
        return following;
    }

    List<List<User>> userTable = new ArrayList<>();

    String currentUserId;

    public void setUsers(List<User> users) {
        this.users = users;
        for(int i=0; i<users.size(); i=i+3){
            List<User> userRow = new ArrayList<>();

            if(i<users.size()-2){
                if(users.get(i).getFollowers()!=null) {
                    following.put(i, users.get(i).
                            getFollowers().
                            contains(currentUserId));
                }
                else {
                    following.put(i, false);
                }

                if(users.get(i+1).getFollowers()!=null) {
                    following.put(i + 1, users.get(i + 1).
                            getFollowers().
                            contains(currentUserId));
                }
                else {
                    following.put(i + 1, false);
                }

                if(users.get(i+2).getFollowers()!=null) {
                    following.put(i + 2, users.get(i + 2).
                            getFollowers().
                            contains(currentUserId));
                }
                else {
                    following.put(i + 2, false);
                }
                userRow.add(users.get(i));
                userRow.add(users.get(i+1));
                userRow.add(users.get(i+2));;
            }
            else if (i<users.size()-1){
                if(users.get(i).getFollowers()!=null) {
                    following.put(i, users.get(i).
                            getFollowers().
                            contains(currentUserId));
                }
                else {
                    following.put(i, false);
                }

                if(users.get(i+1).getFollowers()!=null) {
                    following.put(i + 1, users.get(i + 1).
                            getFollowers().
                            contains(currentUserId));
                }
                else {
                    following.put(i+1, false);
                }

                userRow.add(users.get(i));
                userRow.add(users.get(i+1));
            }
            else {
                if(users.get(i).getFollowers()!=null){
                    following.put(i,users.get(i).
                            getFollowers().
                            contains(currentUserId));
                }
                else {
                    following.put(i, false);
                }

                userRow.add(users.get(i));
            }

            userTable.add(userRow);

        }


    }

    public  void  updateFollowing(int pos, Boolean follows){
        following.put(pos,follows);
    }

    private final RecyclerViewInterface recyclerViewInterface;
    public MyAdapter(Context context, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
        currentUserId = DbUtil.currentId();

    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.user_list_view,parent,false);
        return new MyviewHolder(v,recyclerViewInterface);

    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, int position) {

        holder.userImgTxt1.setText(userTable.get(position).get(0).getFirstName());
       // StorageReference st = Util.imgUrl(userTable,position,0).getDownloadUrl();
        int fpos = position*3;
        Util.imgUrl(userTable,position,0).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Glide.with(context)
                        .load(task.getResult())
                        .transform(new CenterCrop(), new RoundedCorners(50))
                        .into(holder.userImg1);
                if(Boolean.TRUE.equals(following.get(fpos))){
                    holder.userSelect1.setImageResource(R.drawable.remove_icon);
                }else {
                    holder.userSelect1.setImageResource(R.drawable.add_plus);
                }
            }
        });

        if(userTable.get(position).size() > 1) {

            holder.userImgTxt2.setText(userTable.get(position).get(1).getFirstName());
            Util.imgUrl(userTable,position,1).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Glide.with(context)
                            .load(task.getResult())
                            .transform(new CenterCrop(), new RoundedCorners(50))
                            .into(holder.userImg2);

                    Map<Integer, Boolean> fol = following;
                    if(Boolean.TRUE.equals(following.get(fpos+1))){
                        holder.userSelect2.setImageResource(R.drawable.remove_icon);
                    }else {
                        holder.userSelect2.setImageResource(R.drawable.add_plus);
                    }
                }
            });
        }else {
            holder.img_div2.setVisibility(View.INVISIBLE);
        }

        if(userTable.get(position).size() > 2) {
            holder.userImgTxt3.setText(userTable.get(position).get(2).getFirstName());
            Util.imgUrl(userTable,position,2).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Glide.with(context)
                            .load(task.getResult())
                            .transform(new CenterCrop(), new RoundedCorners(50))
                            .into(holder.userImg3);
                    if(Boolean.TRUE.equals(following.get(fpos+2))){
                        holder.userSelect3.setImageResource(R.drawable.remove_icon);
                    }else {
                        holder.userSelect3.setImageResource(R.drawable.add_plus);
                    }
                }
            });
        }else {
            holder.img_div3.setVisibility(View.INVISIBLE);
        }
     }


    @Override
    public int getItemCount() {
        return userTable.size();
    }

    public static class MyviewHolder extends RecyclerView.ViewHolder {

        TextView userImgTxt1;
        TextView userImgTxt2;
        TextView userImgTxt3;
        ConstraintLayout img_div1;
        ConstraintLayout img_div2;
        ConstraintLayout img_div3;

        ImageView userImg1;
        ImageView userImg2;
        ImageView userImg3;

        ImageView userSelect1;
        ImageView userSelect2;
        ImageView userSelect3;

        public MyviewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            userImgTxt1 = itemView.findViewById(R.id.user_img_txt1);
            userImgTxt2 = itemView.findViewById(R.id.user_img_txt2);
            userImgTxt3 = itemView.findViewById(R.id.user_img_txt3);
            img_div1 = itemView.findViewById(R.id.img1_div);
            img_div2 = itemView.findViewById(R.id.img2_div);
            img_div3 = itemView.findViewById(R.id.img3_div);
            userImg1 = itemView.findViewById(R.id.user_img1);
            userImg2 = itemView.findViewById(R.id.user_img2);
            userImg3 = itemView.findViewById(R.id.user_img3);
            userSelect1 = itemView.findViewById(R.id.user_select1);
            userSelect2 = itemView.findViewById(R.id.user_select2);
            userSelect3 = itemView.findViewById(R.id.user_select3);

            img_div1.setOnClickListener(e ->{
                if(recyclerViewInterface != null){
                    int pos = (getAdapterPosition());
                    int fpos = pos*3;
                    if(pos != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(pos,fpos,"click");
                    }
                }
            });

            img_div2.setOnClickListener(e ->{
                if(recyclerViewInterface != null){
                    int pos = (getAdapterPosition());
                    int fpos = pos*3;
                    if(pos != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(pos,fpos+1,"click");
                    }
                }
            });

            img_div3.setOnClickListener(e ->{
                if(recyclerViewInterface != null){
                    int pos = (getAdapterPosition());
                    int fpos = pos*3;
                    if(pos != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(pos,fpos+2,"click");
                    }
                }
            });


        }
    }
}


