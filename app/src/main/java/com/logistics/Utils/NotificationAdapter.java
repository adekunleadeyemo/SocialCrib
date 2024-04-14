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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.firebase.Timestamp;
import com.logistics.Model.Notification;
import com.logistics.Model.User;
import com.logistics.socialcrib.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyviewHolder> {

    Context context;
    List<Notification> notifications;
    List<User> users;
    Map<String,Uri> userImages;

    public void setNewNotifications(List<String> newNotifications) {
        this.newNotifications = newNotifications;
    }

    List<String> newNotifications;

    public void setUserImages(Map<String,Uri> userImages) {
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
        newNotifications = new ArrayList<>();

    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.notificaton_view,parent,false);
        return new MyviewHolder(v,notificationInterface);

    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, int position) {

        User user = users.stream().filter(u -> Objects.equals(u.getUserId(), notifications.get(position).getSender())).findFirst().get();

        if(newNotifications.contains(notifications.get(position).getId())) {
            holder.userNotificationDiv.setBackgroundResource(R.color.new_notification);
        }
        Glide.with(context)
                .load(userImages.get(user.getUserId()))
                .transform(new CenterCrop(), new RoundedCorners(50))
                .into(holder.senderImg);
        String senderName = user.getFirstName()+" "+user.getLastName();
        holder.senderTxt.setText(senderName);

        holder.timeText.setText(getTimeDiff( notifications.get(position).getTimestamp()));
        holder.msgText.setText(notifications.get(position).getMessage());
    }

    private String getTimeDiff(Timestamp timestamp){

        long now = Timestamp.now().getSeconds();
        long t = now - timestamp.getSeconds();

        if(t < 60){
            return String.valueOf(Math.round(t)) + " seconds ago";
        } else if (t < 3600) {
            return String.valueOf(t/60) + " minutes ago";
        }
        else if (t < (3600*24)){
            return String.valueOf(t/3600) + " hours ago";
        }
        else {
            return String.valueOf(t/(3600*24)) + " Days ago";
        }
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
        ConstraintLayout userNotificationDiv;

        public MyviewHolder(@NonNull View itemView,NotificationInterface notificationInterface) {
            super(itemView);

            senderTxt = itemView.findViewById(R.id.notification_sender);
            msgText = itemView.findViewById(R.id.notification_message);
            timeText = itemView.findViewById(R.id.notification_time);
            senderImg = itemView.findViewById(R.id.notification_profile_img);
            userNotificationDiv = itemView.findViewById(R.id.user_notification_div);


        }
    }
}


