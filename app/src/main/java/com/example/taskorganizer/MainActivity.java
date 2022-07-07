package com.example.taskorganizer;

import androidx.annotation.ArrayRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addTask, settingsButton;
    RecyclerView recyclerView;
    MyDatabaseHelper myDB;
    ArrayList<TaskModel> myTasks;
    EditText searchTask;
    CustomAdapter customAdapter;
    boolean hideFinished, showFinished;
    long minutesBefore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addTask = findViewById(R.id.addTaskButton);
        settingsButton = findViewById(R.id.settingsButton);
        searchTask = findViewById(R.id.search);
        this.hideFinished = false;
        this.showFinished = true;
        searchTask.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        recyclerView = findViewById(R.id.taskList);
        addTask.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(i);
        });

        myDB = new MyDatabaseHelper(MainActivity.this);
        myTasks = new ArrayList<>();

        try {
            storeDataInArrays();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Collections.sort(this.myTasks);
        customAdapter = new CustomAdapter(MainActivity.this, this, myTasks);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        settingsButton.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
        });

        //constantly filter current category
        filterCategory();
        getIntentHideFinished();
        getIntentMinutesBefore();
    }


    private void filter(String text){
        ArrayList<TaskModel> filteredList = new ArrayList<>();
        for(TaskModel task : myTasks){
            if(task.getTaskName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(task);
            }
        }
        //sort
        Collections.sort(filteredList);
        customAdapter.filterList(filteredList);
    }

    private void filterCategory(){
        ArrayList<TaskModel> filteredList = new ArrayList<>();

        for(TaskModel task : myTasks){
            if (task.currentCategory != null) {
                System.out.println("OBECNA KATEGORIA "+ task.currentCategory);
                System.out.println("KATEGORIA TASKA " + task.getCategory());
                if(task.currentCategory.toLowerCase().equals(task.getCategory())
                        || task.currentCategory.equals("none")){
                    filteredList.add(task);
                }

            }
        }
        //sort
        Collections.sort(filteredList);
        customAdapter.filterList(filteredList);
    }

    void isTaskStatusFinished(TaskModel task){
        Date dateNow = new Date();
        int compare = dateNow.compareTo(task.execution);
        if(compare > 0 || compare == 0){
           task.setFinished(true);
        }
    }

    private void filterFinished(){
        ArrayList<TaskModel> filteredList = new ArrayList<>();
        for(TaskModel task : myTasks){
            isTaskStatusFinished(task);
            if(!task.getFinished()){
                filteredList.add(task);
            }
        }
        //sort
        Collections.sort(filteredList);
        customAdapter.filterList(filteredList);
    }


    void getIntentHideFinished(){
        if(getIntent().hasExtra("hide_finished")){

            //GETTING DATA FROM INTENT
            String hideFinished = getIntent().getStringExtra("hide_finished");
            if(hideFinished.equals("true")){
                this.hideFinished = true;
            }
            filterFinished();
        }
        /*else{
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }*/
    }

    void getIntentMinutesBefore(){
        if(getIntent().hasExtra("minutes_before")){
            //GETTING DATA FROM INTENT
            String minutesBefore = getIntent().getStringExtra("minutes_before");
            try{
                this.minutesBefore = Long.parseLong(minutesBefore);
            }
            catch (NumberFormatException e){
                Toast.makeText(this, "Wrong minute format!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            recreate();
        }
    }

    void storeDataInArrays() throws ParseException {
        Cursor cursor = myDB.readAllData();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No data!", Toast.LENGTH_SHORT).show();
        }
        else{
            while(cursor.moveToNext()){

                //adding task to an arraylist
                TaskModel task = new TaskModel(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        false,
                        true,
                        cursor.getString(6),
                        cursor.getString(7));

                System.out.println(cursor.getString(6));

                myTasks.add(task);
            }
        }
    }
}