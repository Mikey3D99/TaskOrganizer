package com.example.taskorganizer;

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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {
    private Button timeButton, dateButton, addTaskButton;
    private DatePickerDialog datePickerDialog;
    int year, month, day, hour, minute;
    EditText title_input, description_input, category_input;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Calendar notificationTime;
    private String timeOfCreation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        initDatePicker();

        dateButton = findViewById(R.id.datePickerButton);
        timeButton = findViewById(R.id.selectTime);
        title_input = findViewById(R.id.title);
        description_input = findViewById(R.id.description);
        category_input = findViewById(R.id.category);
        addTaskButton = findViewById(R.id.saveTask);
        addTaskButton.setOnClickListener(view -> {
            getDateAndTimeNow();
            getTimeAndDateFromUserToCalendar();
            //System.out.println(convertCalendarToDate(notificationTime) +  timeOfCreation);
            MyDatabaseHelper myDB = new MyDatabaseHelper(AddTaskActivity.this);
            myDB.addTask(title_input.getText().toString().trim()
                    , description_input.getText().toString().trim(),
                    category_input.getText().toString().trim(),
                    timeOfCreation,
                    convertCalendarToDate(notificationTime));

            setAlarm();
            Intent i = new Intent(AddTaskActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        });

        createNotificationChannel();

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

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                notificationTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
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
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day );
    }



    private String makeDateString(int day, int month, int year){
        return month + " " + day + " " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        return "JAN";
    }

    public void openDatePicker(View view){
        datePickerDialog.show();
    }

}