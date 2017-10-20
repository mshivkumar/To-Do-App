package com.example.android.to_do.ui;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.to_do.R;
import com.example.android.to_do.data.ToDoContract.ToDoEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.id;
import static com.example.android.to_do.R.drawable.todo;
import static com.example.android.to_do.data.ToDoContract.ToDoEntry.CHECKBOX_CHECKED;

/**
 * Created by Ajay on 25-09-2017.
 */

public class ToDoCursorAdapter extends CursorAdapter {

    private TextView mTitleTextView;
    private TextView mDescriptionTextView;
    private TextView mDateCreatedTextView;
    private TextView mTimeTextView;
    private CheckBox mListCheckBox;
    private ImageView mReminderImageView;
    private Date mDatedateObject;
    private String mDateString;

    private static final String LOG_TAG = ToDoCursorAdapter.class.getSimpleName();

    private Context mContext;

    public ToDoCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0 /* flags */);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }


    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {

        mTitleTextView = (TextView) view.findViewById(R.id.title_text_view);
        mDescriptionTextView = (TextView) view.findViewById(R.id.description_text_view);
        mDateCreatedTextView = (TextView) view.findViewById(R.id.date_text_view);
        mListCheckBox = (CheckBox) view.findViewById(R.id.mainlist_checkBox);
        mReminderImageView = (ImageView) view.findViewById(R.id.reminder_image_view);
        mTimeTextView = (TextView) view.findViewById(R.id.time_text_view);

        mReminderImageView.setVisibility(View.GONE);
        mListCheckBox.setVisibility(View.GONE);

        int titleColumnIndex = cursor.getColumnIndex(ToDoEntry.COLUMN_TODO_TITLE);
        int descriptionColumnIndex = cursor.getColumnIndex(ToDoEntry.COLUMN_TODO_DESCRIPTION);
        int dateColumnIndex = cursor.getColumnIndex(ToDoEntry.DUE_DATE);
        int priorityColumnIndex = cursor.getColumnIndex(ToDoEntry.COLUMN_TODO_PRIORITY);
        int mainListCheckBoxColumIndex = cursor.getColumnIndex(ToDoEntry.IS_CHECKED);
        int timeColumnIndex = cursor.getColumnIndex(ToDoEntry.DUE_TIME);

        String todoTitle = cursor.getString(titleColumnIndex);
        String todoDescription = cursor.getString(descriptionColumnIndex);
        String todoDate = cursor.getString(dateColumnIndex);
        String todoTime = cursor.getString(timeColumnIndex);
        int todoPriority = cursor.getInt(priorityColumnIndex);
        int todoIsCompletedMainListCheckBox = cursor.getInt(mainListCheckBoxColumIndex);



        mTitleTextView.setText(todoTitle);
        mDescriptionTextView.setText(todoDescription);
        mDateCreatedTextView.setText(setReminder(todoDate));
        mTimeTextView.setText(todoTime);


        switch (todoPriority) {
            case 1:
//                mPriorityTextView.setText(R.string.priority_low);
//                mPriorityTextView.setTextColor(ContextCompat.getColor(context, R.color.low_priority_text_color));
                view.setBackgroundColor(ContextCompat.getColor(context, R.color.low_priority_background_color));
                break;
            case 2:
                view.setBackgroundColor(ContextCompat.getColor(context, R.color.medium_priority_background_color));
                break;
            case 3:
                view.setBackgroundColor(ContextCompat.getColor(context, R.color.high_priority_background_color));
                break;
            default:
                view.setBackgroundColor(ContextCompat.getColor(context, R.color.none_priority_background_color));
                break;
        }

        switch (todoIsCompletedMainListCheckBox) {
            case CHECKBOX_CHECKED:
                mListCheckBox.setVisibility(View.VISIBLE);
                mListCheckBox.setChecked(true);
                mListCheckBox.setClickable(false);
                mTitleTextView.setPaintFlags(mTitleTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                mDescriptionTextView.setVisibility(View.GONE);
                mDateCreatedTextView.setVisibility(View.GONE);
                mTimeTextView.setVisibility(View.GONE);
                break;
            default:
                mListCheckBox.setVisibility(View.GONE);
                mListCheckBox.setChecked(false);
                mTitleTextView.setPaintFlags(mTitleTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

                //Make description view gone if empty
                if (TextUtils.isEmpty(todoDescription)) {
                    mDescriptionTextView.setVisibility(View.GONE);
                } else {
                    mDescriptionTextView.setVisibility(View.VISIBLE);
                }

                //Make dateView gone if empty
                if (TextUtils.isEmpty(todoDate)) {
                    mDateCreatedTextView.setVisibility(View.GONE);
                } else {
                    mDateCreatedTextView.setVisibility(View.VISIBLE);
//                    mReminderImageView.setVisibility(View.VISIBLE);
//                    mReminderImageView.setImageResource(android.R.drawable.ic_lock_idle_alarm);
                }

                //Make timeView gone if empty
                if (TextUtils.isEmpty(todoTime)) {
                    mTimeTextView.setVisibility(View.GONE);
                } else {
                    mTimeTextView.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private String setReminder(String todoDate) {
        String dummy = "abc";
        if(todoDate.isEmpty()) {
            return dummy;
        }else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("E, LLL, dd, yyyy");

            try {
                mDatedateObject = dateFormat.parse(todoDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            mDateString = formatListDate(mDatedateObject);
            return mDateString;
        }
    }

    private String formatListDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, LLL, dd");
        return dateFormat.format(dateObject);
    }
}
