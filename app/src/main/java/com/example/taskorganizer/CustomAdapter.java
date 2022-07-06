package com.example.taskorganizer;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.car.ui.utils.ViewUtils;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    Context context;
    private ArrayList task_id, task_title, task_description, task_status;
    Activity activity;


    CustomAdapter(Activity activity, Context context,
                  ArrayList task_id,
                  ArrayList task_title,
                  ArrayList task_status,
                  ArrayList task_description){
        this.activity = activity;
        this.context = context;
        this.task_id = task_id;
        this.task_title = task_title;
        this.task_status = task_status;
        this.task_description = task_description;

    }

    @NonNull
    @Override
    public CustomAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.MyViewHolder holder, int position) {
        holder.task_id_txt.setText(String.valueOf(task_id.get(position)));
        holder.task_title_txt.setText(String.valueOf(task_title.get(position)));
        holder.task_description_txt.setText(String.valueOf(task_description.get(position)));
        holder.task_status_txt.setText(String.valueOf(task_status.get(position)));
        holder.mainLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, UpdateTaskActivity.class);
            intent.putExtra("id", String.valueOf(task_id.get(position)));
            intent.putExtra("title", String.valueOf(task_title.get(position)));
            intent.putExtra("description", String.valueOf(task_description.get(position)));
            intent.putExtra("status", String.valueOf(task_status.get(position)));
            activity.startActivityForResult(intent, 1);
        });
    }

    @Override
    public int getItemCount() {
        return task_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView task_id_txt, task_title_txt, task_description_txt, task_status_txt;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            task_id_txt = itemView.findViewById(R.id.taskID);
            task_title_txt = itemView.findViewById(R.id.taskTitle);
            task_description_txt = itemView.findViewById(R.id.taskDescription);
            task_status_txt = itemView.findViewById(R.id.taskStatus);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
