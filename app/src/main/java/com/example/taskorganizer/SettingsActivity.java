package com.example.taskorganizer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    Button hideFinished, save;
    EditText minutesBefore;
    ScrollView categoryList;
    MyDatabaseHelper myDB;
    ArrayList<TaskModel> myTasks;
    ArrayList<String> myCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //buttons
        minutesBefore = findViewById(R.id.minutesBeforeNotification);
        hideFinished = findViewById(R.id.hideFinished);
        hideFinished.setOnClickListener(view -> {
            Intent i = new Intent(SettingsActivity.this, MainActivity.class);
            i.putExtra("hide_finished", "true");
            startActivity(i);
            finish();
        });
        save = findViewById(R.id.setMinutesButton);
        save.setOnClickListener(view -> {
            Intent i = new Intent(SettingsActivity.this, MainActivity.class);
            i.putExtra("minutes_before", minutesBefore.getText().toString().trim());
            startActivity(i);
            finish();
        });

        categoryList = findViewById(R.id.categoryList);

        //init myDB and arraylists
         this.myDB = new MyDatabaseHelper(SettingsActivity.this);
         this.myTasks = new ArrayList<>();
         this.myCategories = new ArrayList<>();

        //create Linear Layout for my scrollview
        LinearLayout scrollviewLinearLayout = new LinearLayout(getApplicationContext());
        scrollviewLinearLayout.setOrientation(LinearLayout.VERTICAL);
        scrollviewLinearLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        //download data from SQLite
        try {
            storeDataInArrays();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Add category buttons
        getCategories();

        for(String category : this.myCategories){

            System.out.println(category);
            Button currentCategory = new Button(getApplicationContext());
            currentCategory.setText(category);
            currentCategory.setOnClickListener(view -> {
                updateCurrentCategoryForAll(category);
                Intent i = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(i);
                finish();
                //showCategoryOnly();
            });
            scrollviewLinearLayout.addView(currentCategory);
        }
        this.categoryList.addView(scrollviewLinearLayout);

    }

    private void updateCurrentCategoryForAll(String category){
        for(TaskModel myTask : this.myTasks){
            this.myDB.updateCurrentCategory(myTask.getTaskID(),
                    category);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putBoolean("MyBoolean", true);
        savedInstanceState.putDouble("myDouble", 1.9);
        savedInstanceState.putInt("MyInt", 1);
        savedInstanceState.putString("MyString", "Welcome back to Android");
        // etc.
    }

    // Function to remove duplicates from an ArrayList
    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {
        ArrayList<T> newList = new ArrayList<T>();

        for (T element : list) {
            if (!newList.contains(element)) {
                newList.add(element);
            }
        }
        return newList;
    }

    private void getCategories(){
        this.myCategories.add("none");
        for (TaskModel task: myTasks){
            this.myCategories.add(task.getCategory());
        }
        this.myCategories = removeDuplicates(this.myCategories);
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
                        "none",
                        cursor.getString(7));

                myTasks.add(task);
            }
        }
    }
}