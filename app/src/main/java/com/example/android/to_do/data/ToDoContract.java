package com.example.android.to_do.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Ajay on 25-09-2017.
 */

public class ToDoContract {

    private ToDoContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.to_do";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TODO = "to_do";

    public static final class ToDoEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TODO);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TODO;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TODO;

        public final static String TABLE_NAME = "to_do";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_TODO_TITLE = "title";
        public final static String COLUMN_TODO_DESCRIPTION = "description";
        public final static String COLUMN_TODO_PRIORITY = "priority";
        public static final String DUE_DATE = "dueDate";
        public static final String DUE_TIME = "dueTime";
        public static final String IS_CHECKED = "isChecked";
        public static final String DATE_CREATED = "dateCreated";
        public static final String TIME_CREATED = "timeCreated";
        public static final String DESC_IMG = "descriptionImage";

        public static final int PRIORITY_HIGH = 3;
        public static final int PRIORITY_MEDIUM = 2;
        public static final int PRIORITY_LOW = 1;
        public static final int NO_PRIORITY = 0;

        public static final int CHECKBOX_CHECKED = 1;
        public static final int CHECKBOX_UNCHECKED = 0;

        public static final int IMAGE_PRESENT = 1;
        public static final int IMAGE_ABSCENT = 0;

        public static boolean isValidPriority(int priority) {
            if (priority == PRIORITY_HIGH || priority == PRIORITY_MEDIUM || priority == PRIORITY_LOW || priority == NO_PRIORITY) {
                return true;
            }
            return false;
        }
    }
}
