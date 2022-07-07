package com.example.taskorganizer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class UpdateTaskActivity extends AppCompatActivity {

    EditText title_input, description_input, status_input;
    Button update_button, delete_button;
    String id, title, description, status, time_creation, time_execution;
    TextView timeOfCreation, timeOfExecution;
    ArrayList<String> creationTime, executionTime;
    ArrayList<TaskModel> myTasks;
    TaskModel updateTask;
    MyDatabaseHelper myDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);

        // finding different UI elements ---
        title_input = findViewById(R.id.title_input);
        description_input = findViewById(R.id.description_input);
        status_input = findViewById(R.id.category_input);
        update_button = findViewById(R.id.saveTask);
        timeOfCreation = findViewById(R.id.timeOfCreation);
        timeOfExecution = findViewById(R.id.timeOfExecution);
        //----------------------------------------------------

        myDB = new MyDatabaseHelper(UpdateTaskActivity.this);
        executionTime = new ArrayList<>();
        creationTime = new ArrayList<>();
        myTasks = new ArrayList<>();


        getAndSetIntentData();
        update_button.setOnClickListener(view -> {
            MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateTaskActivity.this);
            title = title_input.getText().toString().trim();
            description = description_input.getText().toString().trim();
            status = status_input.getText().toString().trim();
            myDB.updateData(id, title, description, status);

        });

        delete_button = findViewById(R.id.deleteTask);
        delete_button.setOnClickListener(view -> {
            confirmDialog();
        });

    }

    void getAndSetIntentData(){
        if(getIntent().hasExtra("task")){

            //GETTING DATA FROM INTENT
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            description = getIntent().getStringExtra("description");
            status = getIntent().getStringExtra("status");

            Bundle extras = getIntent().getExtras();
            if(extras != null){
                updateTask = (TaskModel) extras.getSerializable("task");
            }

            //SETTING DATA
            title_input.setText(updateTask.getTaskName());
            description_input.setText(updateTask.getDescription());
            String taskStatus = updateTask.getFinished() ? "finished" : "unfinished";
            status_input.setText(taskStatus);
            timeOfCreation.setText( "Creation time: " + updateTask.getTimeOfCreation());
            timeOfExecution.setText("Execution time: " + updateTask.getTimeOfExecution());
        }
        else{
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }

    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete " + title + "?");
        builder.setMessage("Are you sure you want to delete " + title + "?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateTaskActivity.this);
            myDB.deleteOneRow(id);
            finish();
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> {});
        builder.create().show();
    }

    void getArrayData(){
        Cursor cursor = myDB.readAllData();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No data!", Toast.LENGTH_SHORT).show();
        }
        else{
            while(cursor.moveToNext()){
                creationTime.add(cursor.getString(4));
                executionTime.add(cursor.getString(5));
            }
        }
    }

    void getCertainRowByID(String ID){
        Cursor cursor = myDB.readRowWithAnID(ID);
        if(cursor.getCount() == 0){
            Toast.makeText(this, "No data!", Toast.LENGTH_SHORT).show();
        }
        else{
            while(cursor.moveToNext()){
               time_creation = cursor.getString(4);
               time_execution = cursor.getString(5);
            }
        }
    }
}