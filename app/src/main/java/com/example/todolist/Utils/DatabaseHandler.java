package com.example.todolist.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.todolist.Model.ToDoModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String STATUS = "status";

    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TITLE + " TEXT, " + DESCRIPTION + " TEXT, "
            + STATUS + " INTEGER)";

    private SQLiteDatabase db;
    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertTask(ToDoModel task) {
        ContentValues cv = new ContentValues();
        cv.put(TITLE, task.getTitle());
        cv.put(DESCRIPTION, task.getDescription());
        cv.put(STATUS, 0);
        db.insert(TODO_TABLE, null, cv);
    }

    public List<ToDoModel> getAllTasks() {
        List<ToDoModel> taskList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try {
            cur = db.query(TODO_TABLE, null, null, null, null, null, null, null);
            if(cur != null) {
                if(cur.moveToFirst()) {
                    do {
                        ToDoModel task = new ToDoModel();

                        int idColumnIndex = cur.getColumnIndex(ID);
                        int titleColumnIndex = cur.getColumnIndex(TITLE);
                        int descriptionColumnIndex = cur.getColumnIndex(DESCRIPTION);
                        int statusColumnIndex = cur.getColumnIndex(STATUS);
                        if (idColumnIndex != -1 && titleColumnIndex != -1 && statusColumnIndex != -1) {
                            task.setId(cur.getInt(idColumnIndex));
                            task.setTitle(cur.getString(titleColumnIndex));
                            task.setDescription(cur.getString(descriptionColumnIndex));
                            task.setStatus(cur.getInt(statusColumnIndex));
                            taskList.add(task);
                        }

                    } while (cur.moveToNext());
                }
            }
        }
        finally {
            db.endTransaction();
            cur.close();
        }
        return taskList;

    }

    public void updateStatus(int id, int status) {
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TODO_TABLE, cv, ID+"=?", new String[] {String.valueOf(id)});
    }

    public void updateTitle(int id, String title) {
        ContentValues cv = new ContentValues();
        cv.put(TITLE, title);
        db.update(TODO_TABLE, cv, ID+"=?", new String[] {String.valueOf(id)});
    }

    public void updateDescription(int id, String description) {
        ContentValues cv = new ContentValues();
        cv.put(DESCRIPTION, description);
        db.update(TODO_TABLE, cv, ID+"=?", new String[] {String.valueOf(id)});
    }

    public void deleteTask(int id) {
        db.delete(TODO_TABLE, ID + "=?", new String[]{String.valueOf(id)});
    }

}
