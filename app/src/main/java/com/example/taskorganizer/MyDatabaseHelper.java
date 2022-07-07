package com.example.taskorganizer;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ConcurrentModificationException;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String DATABASE_NAME = "TaskLibrary.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "myTodoList";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TITLE = "task_title";
    private static final String COLUMN_DESCRIPTION = "task_description";
    private static final String COLUMN_CATEGORY = "task_category";
    private static final String COLUMN_TIME_CREATION = "task_creation";
    private static final String COLUMN_TIME_EXECUTION = "task_execution";
    private static final String COLUMN_CURRENT_CATEGORY = "current_category";
    private static final String COLUMN_FILE_PATH = "attachment";




    MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =
                "CREATE TABLE " + TABLE_NAME +
                        " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_TITLE + " TEXT, " +
                        COLUMN_DESCRIPTION + " TEXT, " +
                        COLUMN_CATEGORY + " TEXT, " +
                        COLUMN_TIME_CREATION + " TEXT, " +
                        COLUMN_TIME_EXECUTION + " TEXT, " +
                        COLUMN_CURRENT_CATEGORY + " TEXT, " +
                        COLUMN_FILE_PATH + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    void addTask(String title, String description, String category, String time_creation,
                 String time_execution, String current_category, String attachment){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_DESCRIPTION, description);
        cv.put(COLUMN_CATEGORY, category);
        cv.put(COLUMN_TIME_CREATION, time_creation);
        cv.put(COLUMN_TIME_EXECUTION, time_execution);
        cv.put(COLUMN_CURRENT_CATEGORY, current_category);
        cv.put(COLUMN_FILE_PATH, attachment);

        long result = db.insert(TABLE_NAME, null, cv);
        if(result == -1)
            Toast.makeText(context, "failed database add task", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "Added successfully!", Toast.LENGTH_SHORT).show();
    }

    Cursor readAllData(){
        String query = "select * from " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null );
        }
        return cursor;
    }

    Cursor readRowWithAnID(String id){
        String query = "select * from " + TABLE_NAME + " WHERE _id = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null );
        }
        return cursor;
    }

    void updateData(String row_id,
                    String title,
                    String description,
                    String status
                    ){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_DESCRIPTION, description);
        cv.put(COLUMN_CATEGORY, status);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context, "Successfully updated!", Toast.LENGTH_SHORT).show();
        }


    }

    void deleteOneRow(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, "Successfully deleted!", Toast.LENGTH_SHORT).show();
        }
    }

    void updateCurrentCategory(String row_id,
                               String current_category){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CURRENT_CATEGORY, current_category);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context, "Successfully updated category!", Toast.LENGTH_SHORT).show();
        }
    }
}
