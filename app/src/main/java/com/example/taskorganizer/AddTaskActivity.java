package com.example.taskorganizer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {
    private Button timeButton, dateButton, addTaskButton, browseButton;
    private DatePickerDialog datePickerDialog;
    int year, month, day, hour, minute;
    EditText title_input, description_input, category_input;
    CheckBox notificationOnOff;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Calendar notificationTime;
    private String timeOfCreation;
    private TextView addAttachment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        initDatePicker();

        dateButton = findViewById(R.id.datePickerButton);
        addAttachment = findViewById(R.id.addAttachment);
        browseButton = findViewById(R.id.browseButton);
        browseButton.setOnClickListener(view -> {
            callChooseFileFromDevice();
        });
        timeButton = findViewById(R.id.selectTime);
        title_input = findViewById(R.id.title);
        description_input = findViewById(R.id.description);
        category_input = findViewById(R.id.category);
        notificationOnOff = findViewById(R.id.checkboxNotification);
        addTaskButton = findViewById(R.id.saveTask1);
        addTaskButton.setOnClickListener(view -> {
            getDateAndTimeNow();
            getTimeAndDateFromUserToCalendar();
            //System.out.println(convertCalendarToDate(notificationTime) +  timeOfCreation);
            MyDatabaseHelper myDB = new MyDatabaseHelper(AddTaskActivity.this);
            myDB.addTask(title_input.getText().toString().trim()
                    , description_input.getText().toString().trim(),
                    category_input.getText().toString().trim(),
                    timeOfCreation,
                    convertCalendarToDate(notificationTime),
                    "none",
                    addAttachment.getText().toString().trim());

            if(notificationOnOff.isChecked()){
                setAlarm();
            }


            Intent i = new Intent(AddTaskActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        });

        createNotificationChannel();

    }

    private void callChooseFileFromDevice(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK) {
                    String path = data.getData().getPath();
                    addAttachment.setText(path);
                }
                break;

        }
    }

    private String convertCalendarToDate(Calendar calendar){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = calendar.getTime();
        return formatter.format(date);
    }

    private void getTimeAndDateFromUserToCalendar(){
        notificationTime = Calendar.getInstance();
        notificationTime.set(Calendar.HOUR_OF_DAY, hour);
        notificationTime.set(Calendar.MINUTE, minute);
        notificationTime.set(Calendar.SECOND, 0);
        notificationTime.set(Calendar.DAY_OF_MONTH, day);
        notificationTime.set(Calendar.MONTH, month);
        notificationTime.set(Calendar.YEAR, year);
        System.out.println(year);
    }

    private void getDateAndTimeNow(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        timeOfCreation = formatter.format(date);
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "TaskOrganizerReminderChannel";
            String description = "Channel for Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("TaskOrganizer", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private void setAlarm(){
        getTimeAndDateFromUserToCalendar();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                notificationTime.getTimeInMillis(),
                pendingIntent);

        Toast.makeText(this, "Notification Set!", Toast.LENGTH_SHORT).show();
    }

    public void popTimeClicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = (timePicker, selectedHour, selectedMinute) -> {
            hour = selectedHour;
            minute = selectedMinute;
            timeButton.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    public void initDatePicker(){

        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String date = makeDateString(day, month, year);
            this.year = year;
            this.month = month;
            this.day = day;
            dateButton.setText(date);
        };


        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day );
    }



    private String makeDateString(int day, int month, int year){
        return month + " " + day + " " + year;
    }

    public void openDatePicker(View view){
        datePickerDialog.show();
    }

}