package com.logistics.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.logistics.socialcrib.R;

public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.MyviewHolder> {

    Context context;
    List<List<String>> topics;
    List<String> selectedTopics;

    LinearLayout.LayoutParams layoutParams;

    public List<String> getSelectedTopics() {
        return selectedTopics;
    }

    public void addSelectedTopics(String selectedTopic) {

        if (selectedTopics == null){

            selectedTopics = new ArrayList<>();
            selectedTopics.add(selectedTopic);
        }
        else{
            selectedTopics.add(selectedTopic);
        }

    }

    public void removeSelectedTopics(String selectedTopic) {

        if (selectedTopics != null){
            selectedTopics.remove(selectedTopic);
        }

    }

    private final TopicRecycleInterface topicRecycleInterface;
    public TopicsAdapter(Context context, TopicRecycleInterface topicRecycleInterface) {
        this.context = context;
        this.topicRecycleInterface = topicRecycleInterface;

        topics = new ArrayList<>();
        selectedTopics = new ArrayList<>();
        topics.add(Arrays.asList("Nigeria","Instagram"));
        topics.add(Arrays.asList("Bring a Drink","Startup"));
        topics.add(Arrays.asList("Fitness","Health"));
        topics.add(Arrays.asList("Dating","Medication"));
        topics.add(Arrays.asList("Shopping","Product"));
        topics.add(Arrays.asList("Sports","Rap"));
        topics.add(Arrays.asList("Cars","Cartoons"));
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.topic_view,parent,false);
        return new MyviewHolder(v,topics,topicRecycleInterface);

    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, int position) {

        holder.topic_txt1.setText(topics.get(position).get(0));
        holder.topic_txt2.setText(topics.get(position).get(1));



        Util.getIcon(topics.get(position).get(0)+".png").addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .into(holder.topic_img1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


        Util.getIcon(topics.get(position).get(1)+".png").addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .into(holder.topic_img2);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        if(selectedTopics.contains(topics.get(position).get(0))){
            holder.topic_div1.setBackgroundResource(R.drawable.circular_bg_active);
            holder.topic_txt1.setTextColor(Color.parseColor("#FBFDFF"));
        }else{
            holder.topic_div1.setBackgroundResource(R.drawable.circurlar_bg);
            holder.topic_txt1.setTextColor(Color.parseColor("#9E9EA0"));
        }

        if(selectedTopics.contains(topics.get(position).get(1))){
            holder.topic_div2.setBackgroundResource(R.drawable.circular_bg_active);
            holder.topic_txt2.setTextColor(Color.parseColor("#FBFDFF"));
        }else{
            holder.topic_div2.setBackgroundResource(R.drawable.circurlar_bg);
            holder.topic_txt2.setTextColor(Color.parseColor("#9E9EA0"));
        }


    }


    @Override
    public int getItemCount() {
        return topics.size();
    }

    public static class MyviewHolder extends RecyclerView.ViewHolder {

        TextView topic_txt1;
        ImageView topic_img1;
        LinearLayout topic_div1;

        TextView topic_txt2;
        ImageView topic_img2;
        LinearLayout topic_div2;

        public MyviewHolder(@NonNull View itemView,List<List<String>> topics, TopicRecycleInterface topicRecycleInterface) {
            super(itemView);

            topic_txt1 = itemView.findViewById(R.id.topic_txt1);
            topic_img1 = itemView.findViewById(R.id.topic_img1);
            topic_div1 = itemView.findViewById(R.id.topic_div1);

            topic_txt2 = itemView.findViewById(R.id.topic_txt2);
            topic_img2 = itemView.findViewById(R.id.topic_img2);
            topic_div2 = itemView.findViewById(R.id.topic_div2);

            topic_div1.setOnClickListener(e ->{
                if(topicRecycleInterface != null){
                    int pos = (getAdapterPosition());
                    if(pos != RecyclerView.NO_POSITION){
                        topicRecycleInterface.itemClick(pos,topics.get(pos).get(0));
                    }
                }
            });

            topic_div2.setOnClickListener(e ->{
                if(topicRecycleInterface != null){
                    int pos = (getAdapterPosition());
                    if(pos != RecyclerView.NO_POSITION){
                        topicRecycleInterface.itemClick(pos,topics.get(pos).get(1));
                    }
                }
            });


        }
    }
}


