package com.example.android.to_do.data;

/**
 * Created by Ajay on 21-09-2017.
 */

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.to_do.data.ToDoContract.ToDoEntry;

import static com.example.android.to_do.data.ToDoContract.ToDoEntry.DESC_IMG;
import static com.example.android.to_do.data.ToDoContract.ToDoEntry.TABLE_NAME;

public class ToDoDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ToDoDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "task.db";

    private static final int DATABASE_VERSION = 7;

    SQLiteDatabase db;
    ContentResolver mContentResolver;

    public ToDoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        mContentResolver = context.getContentResolver();

        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TODO_TABLE =  "CREATE TABLE " + TABLE_NAME + " ("
                + ToDoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ToDoEntry.COLUMN_TODO_TITLE + " TEXT NOT NULL, "
                + ToDoEntry.COLUMN_TODO_DESCRIPTION + " TEXT, "
                + ToDoEntry.DESC_IMG + " BLOB, "
                + ToDoEntry.COLUMN_TODO_PRIORITY + " INTEGER DEFAULT 0, "
                + ToDoEntry.DATE_CREATED + " TEXT, "
                + ToDoEntry.TIME_CREATED + " TEXT, "
                + ToDoEntry.DUE_DATE + " TEXT, "
                + ToDoEntry.DUE_TIME + " TEXT, "
                + ToDoEntry.IS_CHECKED + " INTEGER DEFAULT 0);";

        db.execSQL(SQL_CREATE_TODO_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
