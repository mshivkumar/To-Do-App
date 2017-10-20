package com.example.android.to_do.ui;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;;
import android.util.Log;;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.to_do.R;
import com.example.android.to_do.SettingsActivity;
import com.example.android.to_do.data.ToDoContract.ToDoEntry;
import com.example.android.to_do.data.ToDoDbHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int TODO_LOADER = 0;
    private ToDoCursorAdapter mToDoCursorAdapter;
    private static final String LOG_TAG = MainListActivity.class.getSimpleName();
    private Uri mCurrentToDoUri;
    private ListView mToDoListView;
    private ToDoDbHelper mToDoDbHelper;

    private CheckBox mMainListCheckBox;
    private int mIsChecked = 0;

    private Vibrator mVibrator;

    private Date mDateCreated;
    private String mCurrentDate;
    private String mCurrentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mVibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE) ;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVibrator.vibrate(60);
                Intent intent = new Intent(MainListActivity.this, EditorActivity.class);;
                startActivity(intent);
            }
        });


        mToDoListView = (ListView) findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        mToDoListView.setEmptyView(emptyView);

        mToDoCursorAdapter = new ToDoCursorAdapter(this, null);
        mToDoListView.setAdapter(mToDoCursorAdapter);

        mToDoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mVibrator.vibrate(60);
                Intent intent = new Intent(MainListActivity.this, EditorActivity.class);
                mCurrentToDoUri = ContentUris.withAppendedId(ToDoEntry.CONTENT_URI, id);
                Log.v(LOG_TAG, "Current ToDo Uri in Main : " + mCurrentToDoUri);
                intent.setData(mCurrentToDoUri);
                startActivity(intent);
            }
        });


        mToDoListView.setSmoothScrollbarEnabled(true);

        mToDoListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                mVibrator.vibrate(60);
                mCurrentToDoUri = ContentUris.withAppendedId(ToDoEntry.CONTENT_URI, id);
                showDeleteConfirmationDialog();
                return true;
            }
        });

        mToDoDbHelper = new ToDoDbHelper(this);
        getLoaderManager().initLoader(TODO_LOADER, null, this);
    }

    private void insertToDo() {

        mDateCreated = new Date();
        mCurrentDate = formatDate(mDateCreated);
        mCurrentTime = formatTime(mDateCreated);

        ContentValues values = new ContentValues();
        values.put(ToDoEntry.COLUMN_TODO_TITLE, "Title");
        values.put(ToDoEntry.COLUMN_TODO_DESCRIPTION, "Description");
        values.put(ToDoEntry.COLUMN_TODO_PRIORITY, ToDoEntry.PRIORITY_LOW);
        values.put(ToDoEntry.DUE_DATE, mCurrentDate);
        values.put(ToDoEntry.DUE_TIME, mCurrentTime);
        values.put(ToDoEntry.IS_CHECKED, ToDoEntry.CHECKBOX_CHECKED);

        Uri newUri = getContentResolver().insert(ToDoEntry.CONTENT_URI, values);
    }

    private void deleteAllToDos() {
        int rowsDeleted = getContentResolver().delete(ToDoEntry.CONTENT_URI, null, null);
        Log.v("MainListActivity", rowsDeleted + " rows deleted from task database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(MainListActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );


        String[] projection = {
                ToDoEntry._ID,
                ToDoEntry.COLUMN_TODO_TITLE,
                ToDoEntry.COLUMN_TODO_DESCRIPTION,
                ToDoEntry.COLUMN_TODO_PRIORITY,
                ToDoEntry.DUE_DATE,
                ToDoEntry.DUE_TIME,
                ToDoEntry.IS_CHECKED};


        return new CursorLoader(this,
                ToDoEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mToDoCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mToDoCursorAdapter.swapCursor(null);
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteToDo();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteToDo() {

        if (mCurrentToDoUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentToDoUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_todo_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_todo_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, LLL, dd, yyyy");
        return dateFormat.format(dateObject);
    }

    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

}
