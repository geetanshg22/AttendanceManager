package com.example.attendancemanager.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class ManagerProvider extends ContentProvider {
    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = ManagerProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the pets table
     */
    private static final int Subject = 100;

    /**
     * URI matcher code for the content URI for a single pet in the pets table
     */
    private static final int Subject_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {

        sUriMatcher.addURI(ManagerContract.CONTENT_AUTHORITY, ManagerContract.PATH_SUBJECTS, Subject);
        sUriMatcher.addURI(ManagerContract.CONTENT_AUTHORITY, ManagerContract.PATH_SUBJECTS + "/#", Subject_ID);
    }

    /**
     * Database helper object
     */
    private ManagerHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ManagerHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case Subject:

                cursor = database.query(ManagerContract.SubjectEntry.TABLE_NAME, projection, selection, selectionArgs,
                      null, null, null);
                break;
            case Subject_ID:

                selection = ManagerContract.SubjectEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(ManagerContract.SubjectEntry.TABLE_NAME, projection, selection, selectionArgs,
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
            case Subject:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues values) {

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(ManagerContract.SubjectEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case Subject:
                return updateSubject(uri, contentValues, selection, selectionArgs);
            case Subject_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ManagerContract.SubjectEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateSubject(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updateSubject(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        //get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        getContext().getContentResolver().notifyChange(uri, null);
        // Returns the number of database rows affected by the update statement
        return database.update(ManagerContract.SubjectEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case Subject:
                // Delete all rows that match the selection and selection args
                getContext().getContentResolver().notifyChange(uri, null);
                return database.delete(ManagerContract.SubjectEntry.TABLE_NAME, selection, selectionArgs);
            case Subject_ID:
                // Delete a single row given by the ID in the URI
                selection = ManagerContract.SubjectEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                getContext().getContentResolver().notifyChange(uri, null);
                return database.delete(ManagerContract.SubjectEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        return "";
    }
}
