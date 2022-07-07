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

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addTask;
    RecyclerView recyclerView;
    MyDatabaseHelper myDB;
    ArrayList<TaskModel> myTasks;
    EditText searchTask;
    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addTask = findViewById(R.id.addTaskButton);
        searchTask = findViewById(R.id.search);
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
            finish();
        });

        myDB = new MyDatabaseHelper(MainActivity.this);
        myTasks = new ArrayList<>();

        storeDataInArrays();

        customAdapter = new CustomAdapter(MainActivity.this, this, myTasks);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    private void filter(String text){
        ArrayList<TaskModel> filteredList = new ArrayList<>();
        for(TaskModel task : myTasks){
            if(task.getTaskName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(task);
            }
        }
        customAdapter.filterList(filteredList);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            recreate();
        }
    }

    void storeDataInArrays(){
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
                        true);

                myTasks.add(task);
            }
        }
    }
}