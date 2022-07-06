package com.example.taskorganizer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateTaskActivity extends AppCompatActivity {

    EditText title_input, description_input, status_input;
    Button update_button, delete_button;
    String id, title, description, status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);

        title_input = findViewById(R.id.title_input);
        description_input = findViewById(R.id.description_input);
        status_input = findViewById(R.id.category_input);
        update_button = findViewById(R.id.saveTask);
        getAndSetIntentData();
        update_button.setOnClickListener(view -> {
            MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateTaskActivity.this);
            title = title_input.getText().toString().trim();
            description = description_input.getText().toString().trim();
            status = status_input.getText().toString().trim();
            myDB.updateData(id, title, description, status);

        });

        delete_button = findViewById(R.id.deleteTask);

    }

    void getAndSetIntentData(){
        if(getIntent().hasExtra("id") && getIntent().hasExtra("title")
        && getIntent().hasExtra("description") && getIntent().hasExtra("status")){

            //GETTING DATA FROM INTENT
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            description = getIntent().getStringExtra("description");
            status = getIntent().getStringExtra("status");

            //SETTING DATA
            title_input.setText(title);
            description_input.setText(description);
            status_input.setText(status);
        }
        else{
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }
}