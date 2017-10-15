package com.example.android.to_do.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.to_do.R;
import com.example.android.to_do.data.ToDoContract.ToDoEntry;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> , DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private static final int EXISTING_TODO_LOADER = 0;
    private static Uri mCurrentToDoUri;

    private EditText mTitleEditText;
    private EditText mDescriptionEditText;
    private TextView mDueDateDateTextView;
    private TextView mDueDateTimeTextView;
    private TextView mDueDateTextView;
    private ImageView mDeleteReminderImageView;

    private RadioGroup mRadioGroup;
    private RadioButton mHighPriorityRadioButton;
    private RadioButton mMediumPriorityRadioButton;
    private RadioButton mLowPriorityRadioButton;
    private RadioButton mNoPriorityRadioButton;
    private int mPriority = ToDoEntry.NO_PRIORITY;

    private DatePickerDialog mDatePickerDialog;
    private TimePickerDialog mTimePickerDialog;
    private String mDate;
    private String mTime;

    private LinearLayout mDueDateContainerNullUri;
    private TextView mDueDateDateNullUri;
    private TextView mDueDateTimeNullUri;
    private Button mDueDateButton;

    private LinearLayout mTextCreatedLinearLayout;
    private LinearLayout mDateCheckboxCreatedLinearLayout;
    private LinearLayout mTimeCreatedLinearLayout;
    private LinearLayout mDeleteDueDateContainer;

    private TextView mDateCreatedTextView;
    private TextView mTimeCreatedTextView;

    private Date mDateCreated;

    private CheckBox mEditorCheckBox;
    private static int mIsChecked = 0;

    private String mCurrentDate;
    private String mCurrentTime;


    private Vibrator mVibrator;

    private static final String LOG_TAG = EditorActivity.class.getSimpleName();
    private boolean mToDoHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mToDoHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mVibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE) ;

        Intent intent = getIntent();
        mCurrentToDoUri = intent.getData();

        mDueDateTextView = (TextView) findViewById(R.id.due_date_text);
        mTitleEditText = (EditText) findViewById(R.id.edit_todo_title);
        mDescriptionEditText = (EditText) findViewById(R.id.edit_todo_description);
        mRadioGroup = (RadioGroup) findViewById(R.id.priority_radio_group);
        mDueDateDateTextView = (TextView) findViewById(R.id.due_date_date_text_view);
        mDueDateTimeTextView = (TextView) findViewById(R.id.due_date_time_text_view);
        mEditorCheckBox = (CheckBox) findViewById(R.id.editor_completed_checkbox);
        mDateCreatedTextView = (TextView) findViewById(R.id.date_created_text_view);
        mTimeCreatedTextView = (TextView) findViewById(R.id.time_created_text_view);
        mHighPriorityRadioButton = (RadioButton) findViewById(R.id.high_priority_radio_button);
        mMediumPriorityRadioButton = (RadioButton) findViewById(R.id.medium_priority_radio_button);
        mLowPriorityRadioButton = (RadioButton) findViewById(R.id.low_priority_radio_button);
        mNoPriorityRadioButton = (RadioButton) findViewById(R.id.no_priority_radio_button);
        mDeleteDueDateContainer = (LinearLayout) findViewById(R.id.delete_due_date_container);
        mDeleteReminderImageView = (ImageView) findViewById(R.id.delete_reminder_text_view);
        mDueDateContainerNullUri = (LinearLayout) findViewById(R.id.due_date_container_null_uri);
        mDueDateDateNullUri = (TextView) findViewById(R.id.due_date_date_null_uri);
        mDueDateTimeNullUri = (TextView) findViewById(R.id.due_date_time_null_uri);
        mDueDateButton = (Button) findViewById(R.id.set_due_date_button);

        if (mCurrentToDoUri == null) {
            currentUriIsNull();
        } else {
            currentUriIsNotNull();
        }

        mTitleEditText.setOnTouchListener(mTouchListener);
        mDescriptionEditText.setOnTouchListener(mTouchListener);
        mRadioGroup.setOnTouchListener(mTouchListener);
        mDueDateButton.setOnTouchListener(mTouchListener);
        mEditorCheckBox.setOnTouchListener(mTouchListener);



        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                switch(checkedId) {
                    case R.id.high_priority_radio_button:
                        mPriority = ToDoEntry.PRIORITY_HIGH;
                        mHighPriorityRadioButton.setChecked(true);
                        break;
                    case R.id.medium_priority_radio_button:
                        mPriority = ToDoEntry.PRIORITY_MEDIUM;
                        break;
                    case R.id.low_priority_radio_button:
                        mPriority = ToDoEntry.PRIORITY_LOW;
                        break;
                    default:
                        mPriority = ToDoEntry.NO_PRIORITY;
                        break;
                }
            }
        });

        mDeleteReminderImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVibrator.vibrate(60);
                showDeleteDueDateDialog();
            }
        });

        mEditorCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    showCompletedTaskDialog();
                }
            }
        });

        mDueDateContainerNullUri.setVisibility(View.GONE);
        setDateTime();
    }

    private void setActivityBackgroundColor(int colorResId) {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(colorResId);
    }

    private void addDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mDatePickerDialog = new DatePickerDialog(EditorActivity.this, EditorActivity.this, year, month, day);
        mDatePickerDialog.show();
    }

    private void addTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        mTimePickerDialog = new TimePickerDialog(EditorActivity.this, EditorActivity.this,
                hour, minute, DateFormat.is24HourFormat(this));
        mTimePickerDialog.show();
    }


    private void saveToDo() {

        mDateCreated = new Date();
        mCurrentDate = formatDate(mDateCreated);
        mCurrentTime = formatTime(mDateCreated);

        String titleString = mTitleEditText.getText().toString().trim();
        String descriptionString = mDescriptionEditText.getText().toString().trim();
        String dateString = mDueDateDateTextView.getText().toString().trim();
        String timeString = mDueDateTimeTextView.getText().toString().trim();

        if(mEditorCheckBox.isChecked()) {
            mIsChecked = ToDoEntry.CHECKBOX_CHECKED;
        }
        else {
            mIsChecked = ToDoEntry.CHECKBOX_UNCHECKED;
        }


        if (mCurrentToDoUri == null &&
                TextUtils.isEmpty(titleString) && TextUtils.isEmpty(descriptionString) && mPriority == ToDoEntry.NO_PRIORITY
                && TextUtils.isEmpty(dateString) && TextUtils.isEmpty(timeString)&& !mEditorCheckBox.isChecked()) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ToDoEntry.COLUMN_TODO_TITLE, titleString);
        values.put(ToDoEntry.COLUMN_TODO_DESCRIPTION, descriptionString);
        values.put(ToDoEntry.COLUMN_TODO_PRIORITY, mPriority);
        values.put(ToDoEntry.DUE_DATE, dateString);
        values.put(ToDoEntry.DUE_TIME, timeString);
        values.put(ToDoEntry.IS_CHECKED, mIsChecked);

        if (mCurrentToDoUri == null) {
            values.put(ToDoEntry.DATE_CREATED, mCurrentDate);
            values.put(ToDoEntry.TIME_CREATED, mCurrentTime);
            Uri newUri = getContentResolver().insert(ToDoEntry.CONTENT_URI, values);
            Log.v(LOG_TAG, "New Inserted ToDo Uri : " + newUri);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_todo_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_todo_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }  if (mCurrentToDoUri != null) {
            updateDatabase(values);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentToDoUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if((mTitleEditText.getText().toString()).matches("")) {
                    Toast.makeText(this, getString(R.string.title_cannot_be_blank), Toast.LENGTH_SHORT).show();
                }
                else {
                    mVibrator.vibrate(60);
                    saveToDo();
                    finish();
                }
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                if (!mToDoHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mToDoHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ToDoEntry._ID,
                ToDoEntry.COLUMN_TODO_TITLE,
                ToDoEntry.COLUMN_TODO_DESCRIPTION,
                ToDoEntry.COLUMN_TODO_PRIORITY,
                ToDoEntry.DUE_DATE,
                ToDoEntry.DUE_TIME,
                ToDoEntry.IS_CHECKED,
                ToDoEntry.DATE_CREATED,
                ToDoEntry.TIME_CREATED};

        return new CursorLoader(this,
                mCurrentToDoUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int titleColumnIndex = cursor.getColumnIndex(ToDoEntry.COLUMN_TODO_TITLE);
            int descriptionColumnIndex = cursor.getColumnIndex(ToDoEntry.COLUMN_TODO_DESCRIPTION);
            int priorityColumnIndex = cursor.getColumnIndex(ToDoEntry.COLUMN_TODO_PRIORITY);
            int dateColumnIndex = cursor.getColumnIndex(ToDoEntry.DUE_DATE);
            int timeColumnIndex = cursor.getColumnIndex(ToDoEntry.DUE_TIME);
            int checkboxColumnIndex = cursor.getColumnIndex(ToDoEntry.IS_CHECKED);
            int dateCreatedColumnIndex = cursor.getColumnIndex(ToDoEntry.DATE_CREATED);
            int timeCreatedColumnIndex = cursor.getColumnIndex(ToDoEntry.TIME_CREATED);

            String title = cursor.getString(titleColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);
            int priority = cursor.getInt(priorityColumnIndex);
            String date = cursor.getString(dateColumnIndex);
            String time = cursor.getString(timeColumnIndex);
            int checkboxIsCompleted = cursor.getInt(checkboxColumnIndex);
            String dateCreated = cursor.getString(dateCreatedColumnIndex);
            String timeCreated = cursor.getString(timeCreatedColumnIndex);

            mTitleEditText.setText(title);
            mDescriptionEditText.setText(description);
            mDateCreatedTextView.setText(dateCreated);
            mTimeCreatedTextView.setText(timeCreated);
            mDueDateDateTextView.setText(date);
            mDueDateTimeTextView.setText(time);

            if(date.isEmpty() && time.isEmpty()) {
                editDateTime();
                mDueDateDateTextView.setText(date);
                mDueDateTimeTextView.setText(time);
            }

            switch(checkboxIsCompleted) {
                case ToDoEntry.CHECKBOX_CHECKED:
                    showRestoreTaskDialog();
                    break;
                default:
                    mEditorCheckBox.setChecked(false);
                    break;
            }

            switch (priority) {
                case ToDoEntry.PRIORITY_LOW:
                    mLowPriorityRadioButton.setChecked(true);
                    break;
                case ToDoEntry.PRIORITY_MEDIUM:
                    mMediumPriorityRadioButton.setChecked(true);
                    break;
                case ToDoEntry.PRIORITY_HIGH:
                    mHighPriorityRadioButton.setChecked(true);
                    break;
                default:
                    mNoPriorityRadioButton.setChecked(true);
                    break;
            }

            switch (priority) {
                case ToDoEntry.PRIORITY_LOW:
                    setActivityBackgroundColor(ContextCompat.getColor(this, R.color.low_priority_activity_background_color));
                    break;
                case ToDoEntry.PRIORITY_MEDIUM:
                    setActivityBackgroundColor(ContextCompat.getColor(this, R.color.medium_priority_activity_background_color));
                    break;
                case ToDoEntry.PRIORITY_HIGH:
                    setActivityBackgroundColor(ContextCompat.getColor(this, R.color.high_priority_activity_background_color));
                    break;
                default:
                    setActivityBackgroundColor(ContextCompat.getColor(this, R.color.none_priority_activity_background_color));
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTitleEditText.setText("");
        mDescriptionEditText.setText("");
        mNoPriorityRadioButton.setChecked(true);
        mDueDateDateTextView.setText("");
        mDueDateTimeTextView.setText("");
        mEditorCheckBox.setChecked(false);
        mDateCreatedTextView.setText("");
        mTimeCreatedTextView.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    public static void showRestoreDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.restore_msg);
        builder.setPositiveButton(R.string.restore_key, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mIsChecked = ToDoEntry.CHECKBOX_UNCHECKED;
                ContentValues values = new ContentValues();
                values.put(ToDoEntry.IS_CHECKED, mIsChecked);

                int rowsAffected = context.getContentResolver().update(mCurrentToDoUri, values, null, null);
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

    private void showCompletedTaskDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.completed_dialog_msg);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
                ContentValues values = new ContentValues();
                values.put(ToDoEntry.IS_CHECKED, ToDoEntry.CHECKBOX_CHECKED);
                updateDatabaseToDoCompleted(values);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                    mEditorCheckBox.setChecked(false);
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showRestoreTaskDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.restore_msg);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ContentValues values = new ContentValues();
                values.put(ToDoEntry.IS_CHECKED, ToDoEntry.CHECKBOX_UNCHECKED);
                updateDatabaseToDoRestored(values);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                    finish();
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
        finish();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        int year = i;
        int month = i1;
        int day = i2;

        mDate = day + "-" + month + "-" + year;
        Date date = new GregorianCalendar(year, month, day).getTime();
        mDate = formatDate(date);
        mDueDateDateTextView.setText(mDate);
        mDueDateDateNullUri.setText(mDate);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        int hour = i;
        int minute = i1;

        String AM_PM;
        if(hour < 12) {
            AM_PM = "AM";
        } else {
            AM_PM = "PM";
        }

        mTime = hour + ":" + minute + " " + AM_PM;
        mDueDateTimeTextView.setText(mTime);
        mDueDateTimeNullUri.setText(mTime);
    }

    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, LLL, dd, yyyy");
        return dateFormat.format(dateObject);
    }

    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    private void setDateTime() {
        if (mCurrentToDoUri == null) {
            mDueDateButton.setText(R.string.add_due_date);
            mDueDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mVibrator.vibrate(60);
                    addTime();
                    addDate();
                    mDueDateContainerNullUri.setVisibility(View.VISIBLE);
                }
            });
        } else {
            mDueDateButton.setText(R.string.edit_due_date);
            mDueDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mVibrator.vibrate(60);
                    addTime();
                    addDate();
                }
            });
        }
    }

    private void editDateTime() {
        mDueDateDateTextView.setVisibility(View.GONE);
        mDueDateTimeTextView.setVisibility(View.GONE);
        mDueDateTextView.setVisibility(View.GONE);
        mDeleteDueDateContainer.setVisibility(View.GONE);
        mDueDateButton.setText(R.string.add_due_date);
        mDueDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVibrator.vibrate(60);
                addTime();
                addDate();
                mDueDateDateTextView.setVisibility(View.VISIBLE);
                mDueDateTimeTextView.setVisibility(View.VISIBLE);
                mDueDateTextView.setVisibility(View.VISIBLE);
                mDeleteDueDateContainer.setVisibility(View.VISIBLE);
                mDueDateButton.setText(R.string.edit_due_date);
            }
        });
    }

    private void showDeleteDueDateDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.remove_due_date_msg);
        builder.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removeReminder();
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

    private void showReminderSelection() {
        CharSequence options[] = new CharSequence[] {"15 minutes before due time",
                "1 hour before due time", "2 hours before due time", "A day before due date"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Set Reminder : ");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on options[which]
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void removeReminder() {
        mDueDateDateTextView.setVisibility(View.GONE);
        mDueDateTimeTextView.setVisibility(View.GONE);
        mDueDateTextView.setVisibility(View.GONE);
        mDeleteDueDateContainer.setVisibility(View.GONE);
        ContentValues values = new ContentValues();
        values.put(ToDoEntry.DUE_DATE, "");
        values.put(ToDoEntry.DUE_TIME, "");
        int rowsAffected = getContentResolver().update(mCurrentToDoUri, values, null, null);
        mDueDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVibrator.vibrate(60);
                mDueDateDateTextView.setVisibility(View.VISIBLE);
                mDueDateTimeTextView.setVisibility(View.VISIBLE);
                mDueDateTextView.setVisibility(View.VISIBLE);
                mDeleteDueDateContainer.setVisibility(View.VISIBLE);
                addTime();
                addDate();
            }
        });
    }


    private void currentUriIsNull() {
        mTextCreatedLinearLayout = (LinearLayout) findViewById(R.id.created_completed_text_view);
        mDateCheckboxCreatedLinearLayout = (LinearLayout) findViewById(R.id.created_date_checkbox);
        mTimeCreatedLinearLayout = (LinearLayout) findViewById(R.id.created_time);
        mDueDateTextView = (TextView) findViewById(R.id.due_date_text);
        mDeleteDueDateContainer = (LinearLayout) findViewById(R.id.delete_due_date_container);

        mTextCreatedLinearLayout.setVisibility(LinearLayout.GONE);
        mDateCheckboxCreatedLinearLayout.setVisibility(LinearLayout.GONE);
        mTimeCreatedLinearLayout.setVisibility(LinearLayout.GONE);
        mDueDateTextView.setVisibility(View.INVISIBLE);
        mDeleteDueDateContainer.setVisibility(View.GONE);
        setTitle(getString(R.string.editor_activity_title_new_pet));
        invalidateOptionsMenu();
    }

    private void currentUriIsNotNull() {
        setTitle(getString(R.string.editor_activity_title_edit_pet));
        getLoaderManager().initLoader(EXISTING_TODO_LOADER, null, this);
    }

    private void updateDatabase(ContentValues values) {
        int rowsAffected = getContentResolver().update(mCurrentToDoUri, values, null, null);
        if (rowsAffected == 0) {
            Toast.makeText(this, getString(R.string.editor_update_todo_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.editor_update_todo_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDatabaseToDoCompleted(ContentValues values) {
        int rowsAffected = getContentResolver().update(mCurrentToDoUri, values, null, null);
        if (rowsAffected == 0) {
            Toast.makeText(this, getString(R.string.editor_update_todo_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.todo_completed),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDatabaseToDoRestored(ContentValues values) {
        int rowsAffected = getContentResolver().update(mCurrentToDoUri, values, null, null);
        if (rowsAffected == 0) {
            Toast.makeText(this, getString(R.string.todo_restore_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.todo_restore_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }

}