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
import java.util.List;

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

    List<List<User>> userTable = new ArrayList<>();

    public void setUsers(List<User> users) {
        this.users = users;
        for(int i=0; i<users.size(); i=i+3){
            List<User> userRow = new ArrayList<>();

            if(i<users.size()-2){
                userRow.add(users.get(i));
                userRow.add(users.get(i+1));
                userRow.add(users.get(i+2));;
            }
            else if (i<users.size()-1){
                userRow.add(users.get(i));
                userRow.add(users.get(i+1));
            }
            else {
                userRow.add(users.get(i));
            }

            userTable.add(userRow);

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
       // StorageReference st = Util.imgUrl(userTable,position,0).getDownloadUrl();
        Util.imgUrl(userTable,position,0).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Glide.with(context)
                        .load(task.getResult())
                        .transform(new CenterCrop(), new RoundedCorners(50))
                        .into(holder.userImg1);
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

            if(recyclerViewInterface != null){
                int pos = getAdapterPosition();
                if(pos != RecyclerView.NO_POSITION){
                    recyclerViewInterface.onItemClick(pos,"click");
                }
            }

        }
    }
}


