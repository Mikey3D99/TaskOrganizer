package com.example.taskorganizer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class UpdateTaskActivity extends AppCompatActivity {

    EditText title_input, description_input, category_input;
    Button update_button, delete_button;
    String id, title, description, status, time_creation, time_execution, attachment;
    TextView timeOfCreation, timeOfExecution, taskStatus, attachmentView;
    ArrayList<String> creationTime, executionTime;
    ArrayList<TaskModel> myTasks;
    TaskModel updateTask;
    MyDatabaseHelper myDB;
    CheckBox notificationOnOff;
    private Calendar notificationTime;
    int year, month, day, hour, minute;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private ImageView myImage;
    OutputStream outputStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);

        // finding different UI elements ---
        title_input = findViewById(R.id.title_input);
        description_input = findViewById(R.id.description_input);
        category_input = findViewById(R.id.category_input);
        update_button = findViewById(R.id.saveTask);
        timeOfCreation = findViewById(R.id.timeOfCreation);
        timeOfExecution = findViewById(R.id.timeOfExecution);
        notificationOnOff = findViewById(R.id.checkboxNotification_input);
        taskStatus = findViewById(R.id.status);
        attachmentView = findViewById(R.id.addAttachment_input);
        myImage = findViewById(R.id.imageView);

        //----------------------------------------------------

        myDB = new MyDatabaseHelper(UpdateTaskActivity.this);
        executionTime = new ArrayList<>();
        creationTime = new ArrayList<>();
        myTasks = new ArrayList<>();


        // Task model loaded here
        getAndSetIntentData();

        update_button.setOnClickListener(view -> {
            MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateTaskActivity.this);
            title = title_input.getText().toString().trim();
            description = description_input.getText().toString().trim();
            status = category_input.getText().toString().trim();
            myDB.updateData(id, title, description, status);

            if(notificationOnOff.isChecked()){
                setAlarm();
            }

        });

        delete_button = findViewById(R.id.deleteTask);
        delete_button.setOnClickListener(view -> {
            confirmDialog();
        });

        Retrieve( updateTask.attachmentFileName);

    }

    private void getExecutionTimeToCalendar(){
        notificationTime = Calendar.getInstance();
        notificationTime.setTime(updateTask.execution);
    }

    private void setAlarm(){
        getExecutionTimeToCalendar();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                notificationTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);


        Toast.makeText(this, "Notification Set!", Toast.LENGTH_SHORT).show();
    }

    private void cancelAlarm(){
        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        if(alarmManager == null){
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            Toast.makeText(this, "Alarm canceled!", Toast.LENGTH_SHORT).show();
        }
    }

    public void Retrieve(String path)
    {
        File imageFile = new File(Environment.getDataDirectory() + path);

        if(imageFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(path);
            myImage = (ImageView) findViewById(R.id.imageView);
            myImage.setImageBitmap(BitmapFactory.decodeFile(path));

        }
        else{
            Toast.makeText(getApplicationContext(),Environment.getDataDirectory() + path, Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageToExternal(){
        if(this.updateTask.attachmentFileName.contains("image:")){
            BitmapDrawable drawable = (BitmapDrawable) myImage.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            File filepath = Environment.getExternalStorageDirectory();
            File dir = new File(filepath.getAbsolutePath() + this.updateTask.attachmentFileName);
            dir.mkdir();
            File file = new File(dir, System.currentTimeMillis() + ".jpg");
            try {
                outputStream = new FileOutputStream(file);
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            Toast.makeText(getApplicationContext(), "Image saved to external storage", Toast.LENGTH_SHORT).show();
            try {
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void setTaskStatus(){
        Date dateNow = new Date();
        int compare = dateNow.compareTo(updateTask.execution);
        if(compare > 0 || compare == 0){
            this.updateTask.setFinished(true);
        }
    }

    void getAndSetIntentData(){
        if(getIntent().hasExtra("task")){

            //GETTING DATA FROM INTENT
            Bundle extras = getIntent().getExtras();
            if(extras != null){
                updateTask = (TaskModel) extras.getSerializable("task");
            }

            //SETTING DATA
            title_input.setText(updateTask.getTaskName());
            description_input.setText(updateTask.getDescription());
            category_input.setText(updateTask.getCategory());
            timeOfCreation.setText( "Creation time: " + updateTask.getTimeOfCreation());
            timeOfExecution.setText("Execution time: " + updateTask.getTimeOfExecution());
            attachmentView.setText("Attachment file: " + updateTask.attachmentFileName);
            //check if task finished
            setTaskStatus();
            taskStatus.setText(updateTask.getFinished() ? "Task status: finished" : "Task status: unfinished");
        }
        else{
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }

    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete " + updateTask.getTaskName() + "?");
        builder.setMessage("Are you sure you want to delete " + updateTask.getTaskName() + "?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateTaskActivity.this);
            myDB.deleteOneRow(updateTask.getTaskID());
            finish();
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> {});
        builder.create().show();
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