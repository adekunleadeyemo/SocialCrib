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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.logistics.Model.User;
import com.logistics.socialcrib.R;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyviewHolder> {

    Context context;
     List<List<User>> userTable;
     List<List<Uri>> userImageTable;

    public List<String> following;


    public void setUserImageTable(List<List<Uri>> userImageTable) {
        this.userImageTable = userImageTable;
    }

    public void setUserTable(List<List<User>> userTable) {
        this.userTable = userTable;
    }

    public void removeUser(int pos, int pos2){

        if(userTable.get(pos).size() == 1){
            userTable.remove(pos);
            userImageTable.remove(pos);
        }
        else {
            userTable.get(pos).remove(pos2);
            userImageTable.get(pos).remove(pos2);
        }

    }
    private final RecyclerViewInterface recyclerViewInterface;
    public MyAdapter(Context context, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;

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
        Util.getUserImage(userTable.get(position).get(0).getImgUrl()).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .transform(new CenterCrop(), new RoundedCorners(50))
                        .into(holder.userImg1);
            }
        });
        if(following != null && following.contains(userTable.get(position).get(0).getUserId())){
            holder.userSelect1.setImageResource(R.drawable.remove_icon);
        }else {
            holder.userSelect1.setImageResource(R.drawable.add_plus);
        }

        if(userTable.get(position).size() > 1) {

            holder.userImgTxt2.setText(userTable.get(position).get(1).getFirstName());
            Util.getUserImage(userTable.get(position).get(1).getImgUrl()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context)
                            .load(uri)
                            .transform(new CenterCrop(), new RoundedCorners(50))
                            .into(holder.userImg2);
                    holder.img_div2.setVisibility(View.VISIBLE);
                }
            });


            if(following != null && following.contains(userTable.get(position).get(1).getUserId())){
                holder.userSelect2.setImageResource(R.drawable.remove_icon);
            }else {
                holder.userSelect2.setImageResource(R.drawable.add_plus);
            }
        }else {
            holder.img_div2.setVisibility(View.GONE);
        }

        if(userTable.get(position).size() > 2) {

            holder.userImgTxt3.setText(userTable.get(position).get(2).getFirstName());

            Util.getUserImage(userTable.get(position).get(2).getImgUrl()).addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context)
                            .load(uri)
                            .transform(new CenterCrop(), new RoundedCorners(50))
                            .into(holder.userImg3);
                    holder.img_div3.setVisibility(View.VISIBLE);
                }
            });

            if(following != null && following.contains(userTable.get(position).get(2).getUserId())){
                holder.userSelect3.setImageResource(R.drawable.remove_icon);
            }else {
                holder.userSelect3.setImageResource(R.drawable.add_plus);
            }
        }else {
            holder.img_div3.setVisibility(View.GONE);
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
                        recyclerViewInterface.onItemClick(pos,0,"click");
                    }
                }
            });

            img_div2.setOnClickListener(e ->{
                if(recyclerViewInterface != null){
                    int pos = (getAdapterPosition());
                    int fpos = pos*3;
                    if(pos != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(pos,1,"click");
                    }
                }
            });

            img_div3.setOnClickListener(e ->{
                if(recyclerViewInterface != null){
                    int pos = (getAdapterPosition());
                    int fpos = pos*3;
                    if(pos != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(pos,2,"click");
                    }
                }
            });


        }
    }
}


