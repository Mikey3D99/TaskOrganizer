package com.example.taskorganizer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
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
import java.util.Calendar;
import java.util.Date;

public class SettingsActivity extends AppCompatActivity {

    Button hideFinished, save;
    EditText minutesBefore;
    ScrollView categoryList;
    MyDatabaseHelper myDB;
    ArrayList<TaskModel> myTasks;
    ArrayList<String> myCategories;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

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
            setReminders();
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
    void isTaskStatusFinished(TaskModel task){
        Date dateNow = new Date();
        int compare = dateNow.compareTo(task.execution);
        if(compare > 0 || compare == 0){
            task.setFinished(true);
        }
    }
    private String convertCalendarToDate(Calendar calendar){
        android.icu.text.SimpleDateFormat formatter = new android.icu.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = calendar.getTime();
        return formatter.format(date);
    }

    //returns time minus minutes
    private Calendar subtractTime(String time){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new android.icu.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            cal.setTime(sdf.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int minus = Integer.parseInt(minutesBefore.getText().toString().trim());
        cal.add(Calendar.MINUTE, -1 * minus);
        return cal;
    }

    private void setReminders(){
        for(TaskModel myTask : this.myTasks){
            isTaskStatusFinished(myTask);
            System.out.println("CZEK  ___>>>> " +myTask.getFinished());

            if(!myTask.getFinished()){
                //get exec time, convert to date, subtract user amount and set the alarm
                String execTime = myTask.getTimeOfExecution();
                System.out.println(subtractTime(execTime));
                setAlarm(subtractTime(execTime));
            }
        }
    }

    private void setAlarm(Calendar notificationTime){

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                notificationTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);


        Toast.makeText(this, "Notification Set!", Toast.LENGTH_SHORT).show();
    }

    private void updateCurrentCategoryForAll(String category){
        for(TaskModel myTask : this.myTasks){
            this.myDB.updateCurrentCategory(myTask.getTaskID(),
                    category);
        }
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