package com.logistics.Utils;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.logistics.Model.Notification;
import com.logistics.Model.User;
import com.logistics.socialcrib.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyviewHolder> {

    Context context;
    List<Notification> notifications;
    List<User> users;
    List<Uri> userImages;

    public void setUserImages(List<Uri> userImages) {
        this.userImages = userImages;
    }
    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    private final NotificationInterface notificationInterface;
    public NotificationAdapter(Context context, NotificationInterface notificationInterface) {
        this.context = context;
        this.notificationInterface = notificationInterface;

    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.notificaton_view,parent,false);
        return new MyviewHolder(v,notificationInterface);

    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, int position) {

        Glide.with(context)
                .load(userImages.get(position))
                .transform(new CenterCrop(), new RoundedCorners(50))
                .into(holder.senderImg);
        String senderName = users.get(position).getFirstName()+" "+users.get(position).getLastName();
        holder.senderTxt.setText(senderName);
        holder.timeText.setText(notifications.get(position).getTimestamp());
        holder.msgText.setText(notifications.get(position).getMessage());
    }


    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class MyviewHolder extends RecyclerView.ViewHolder {

        TextView senderTxt;
        TextView msgText;

        TextView timeText;
        ImageView senderImg;

        public MyviewHolder(@NonNull View itemView,NotificationInterface notificationInterface) {
            super(itemView);

            senderTxt = itemView.findViewById(R.id.notification_sender);
            msgText = itemView.findViewById(R.id.notification_message);
            timeText = itemView.findViewById(R.id.notification_time);
            senderImg = itemView.findViewById(R.id.notification_profile_img);


        }
    }
}


