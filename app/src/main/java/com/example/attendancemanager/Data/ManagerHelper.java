package com.example.attendancemanager.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ManagerHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = ManagerHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "qwerfdgffgdvg.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    public ManagerHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + ManagerContract.SubjectEntry.TABLE_NAME + " ("
                + ManagerContract.SubjectEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ManagerContract.SubjectEntry.COLUMN_SUBJECT_NAME + " TEXT NOT NULL, "
                + ManagerContract.SubjectEntry.COLUMN_PRESENT + " INTEGER DEFAULT 0, "
                + ManagerContract.SubjectEntry.COLUMN_ABSENT + " INTEGER DEFAULT 0,"
                + ManagerContract.SubjectEntry.COLUMN_CRITERIA + " INTEGER DEFAULT 75,"
                + ManagerContract.SubjectEntry.COLUMN_CHECKBOX_MON + " INTEGER, "
                + ManagerContract.SubjectEntry.COLUMN_CHECKBOX_TUE + " INTEGER, "
                + ManagerContract.SubjectEntry.COLUMN_CHECKBOX_WED + " INTEGER, "
                + ManagerContract.SubjectEntry.COLUMN_CHECKBOX_THU + " INTEGER, "
                + ManagerContract.SubjectEntry.COLUMN_CHECKBOX_FRI + " INTEGER, "
                + ManagerContract.SubjectEntry.COLUMN_CHECKBOX_SAT + " INTEGER, "
                + ManagerContract.SubjectEntry.COLUMN_CHECKBOX_SUN + " INTEGER, "
                + ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_MON_COLOUR + " INTEGER DEFAULT 4,"
                + ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_TUE_COLOUR + " INTEGER DEFAULT 4, "
                + ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_WED_COLOUR + " INTEGER DEFAULT 4, "
                + ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_THU_COLOUR + " INTEGER DEFAULT 4, "
                + ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_FRI_COLOUR + " INTEGER DEFAULT 4, "
                + ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_SAT_COLOUR + " INTEGER DEFAULT 4, "
                + ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_SUN_COLOUR + " INTEGER DEFAULT 4 );";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}

