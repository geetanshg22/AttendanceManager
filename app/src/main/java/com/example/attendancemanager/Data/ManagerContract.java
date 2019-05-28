package com.example.attendancemanager.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ManagerContract {

        private ManagerContract() {}


        public static final String CONTENT_AUTHORITY = "com.example.attendancemanager";


        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


        public static final String PATH_SUBJECTS = "subjects";


        public static final class SubjectEntry implements BaseColumns {

            /** The content URI to access the pet data in the provider */
            public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SUBJECTS);

            /** Name of database table for pets */
            public final static String TABLE_NAME = "subjects";

            /**
             * Unique ID number for the pet (only for use in the database table).
             *
             * Type: INTEGER
             */
            public final static String _ID = BaseColumns._ID;

            public final static String COLUMN_SUBJECT_NAME ="name";

            public final static String COLUMN_PRESENT = "present";

            public final static String COLUMN_ABSENT = "absent";

            public final static String COLUMN_CRITERIA = "criteria";

            public final static String COLUMN_CHECKBOX_MON = "mon";
            public final static String COLUMN_CHECKBOX_TUE = "tue";
            public final static String COLUMN_CHECKBOX_WED = "wed";
            public final static String COLUMN_CHECKBOX_THU = "thu";
            public final static String COLUMN_CHECKBOX_FRI = "fri";
            public final static String COLUMN_CHECKBOX_SAT = "sat";
            public final static String COLUMN_CHECKBOX_SUN = "sun";

            public final static String COLUMN_TEXTVIEW_MON_COLOUR = "monColour";
            public final static String COLUMN_TEXTVIEW_TUE_COLOUR = "tueColour";
            public final static String COLUMN_TEXTVIEW_WED_COLOUR = "wedColour";
            public final static String COLUMN_TEXTVIEW_THU_COLOUR = "thuColour";
            public final static String COLUMN_TEXTVIEW_FRI_COLOUR = "friColour";
            public final static String COLUMN_TEXTVIEW_SAT_COLOUR = "satColour";
            public final static String COLUMN_TEXTVIEW_SUN_COLOUR = "sunColour";

            public final static int STYLE_TEXTVIEW_PRESENT = 1;
            public final static int STYLE_TEXTVIEW_ABSENT = 2;
            public final static int STYLE_TEXTVIEW_CANCELLED = 3;
            public final static int STYLE_TEXTVIEW_NOTMARKED = 4;


        }

}

