package com.example.android.to_do.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import com.example.android.to_do.data.ToDoContract.ToDoEntry;

import static android.R.attr.name;

/**
 * Created by Ajay on 25-09-2017.
 */

public class ToDoDbProvider extends ContentProvider {

    public static final String LOG_TAG = ToDoDbProvider.class.getSimpleName();
    private static final int TODO = 100;
    private static final int TODO_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(ToDoContract.CONTENT_AUTHORITY, ToDoContract.PATH_TODO, TODO);
        sUriMatcher.addURI(ToDoContract.CONTENT_AUTHORITY, ToDoContract.PATH_TODO + "/#", TODO_ID);
    }

    private ToDoDbHelper mToDoDbHelper;

    @Override
    public boolean onCreate() {
        mToDoDbHelper = new ToDoDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mToDoDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case TODO:
                cursor = database.query(ToDoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TODO_ID:
                selection = ToDoEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(ToDoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODO:
                return insertToDo(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertToDo(Uri uri, ContentValues values) {
        String title = values.getAsString(ToDoEntry.COLUMN_TODO_TITLE);
        if (title == null) {
            throw new IllegalArgumentException("Todo requires a name");
        }

        Integer priority = values.getAsInteger(ToDoEntry.COLUMN_TODO_PRIORITY);
        if (!ToDoEntry.isValidPriority(priority)) {
            throw new IllegalArgumentException("To-Do requires a valid priority");
        }

        SQLiteDatabase database = mToDoDbHelper.getWritableDatabase();

        long id = database.insert(ToDoEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODO:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case TODO_ID:
                selection = ToDoEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }


    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(ToDoEntry.COLUMN_TODO_TITLE)) {
            String title = values.getAsString(ToDoEntry.COLUMN_TODO_TITLE);
            if (title == null) {
                throw new IllegalArgumentException("Todo requires a title");
            }
        }

        if (values.containsKey(ToDoEntry.COLUMN_TODO_PRIORITY)) {
            Integer priority = values.getAsInteger(ToDoEntry.COLUMN_TODO_PRIORITY);
            if (!ToDoEntry.isValidPriority(priority)) {
                throw new IllegalArgumentException("Todo requires a valid priority");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mToDoDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(ToDoEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mToDoDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODO:
                rowsDeleted = database.delete(ToDoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TODO_ID:
                selection = ToDoEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ToDoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TODO:
                return ToDoEntry.CONTENT_LIST_TYPE;
            case TODO_ID:
                return ToDoEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
