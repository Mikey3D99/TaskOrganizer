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

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    Context context;
    private ArrayList<TaskModel> task;
    Activity activity;


    CustomAdapter(Activity activity, Context context,
                  ArrayList<TaskModel> task){
        this.activity = activity;
        this.context = context;
        this.task = task;

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

        /*holder.task_id_txt.setText(String.valueOf(task_id.get(position)));
        holder.task_title_txt.setText(String.valueOf(task_title.get(position)));
        holder.task_description_txt.setText(String.valueOf(task_description.get(position)));
        holder.task_status_txt.setText(String.valueOf(task_status.get(position)));*/

        holder.task_id_txt.setText(task.get(position).getTaskID());
        holder.task_title_txt.setText(task.get(position).getTaskName());
        holder.task_description_txt.setText(task.get(position).getDescription());

        String taskStatus =  task.get(position).getFinished() ? "finished" : "unfinished";
        holder.task_status_txt.setText(taskStatus);

        holder.mainLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, UpdateTaskActivity.class);
            intent.putExtra("task", task.get(position));
            activity.startActivityForResult(intent, 1);
        });
    }

    @Override
    public int getItemCount() {
        return task.size();
    }

    // here is a single task in a recyclerview
    public static class MyViewHolder extends RecyclerView.ViewHolder{

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

    public void filterList(ArrayList<TaskModel> filteredList){
        task = filteredList;
        notifyDataSetChanged();
    }

}
