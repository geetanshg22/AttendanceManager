package com.example.attendancemanager;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.location.Criteria;
import android.net.Uri;
import android.os.Bundle;
import android.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.attendancemanager.Data.ManagerContract;

public class AddSubjectActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mPresentEditText;

    /** EditText field to enter the pet's weight */
    private EditText mAbsentEditText;

    private EditText mCriteriaEditText;

    private CheckBox mMondayCheckBox;
    private CheckBox mTuesdayCheckBox;
    private CheckBox mWednesdayCheckBox;
    private CheckBox mThursdayCheckBox;
    private CheckBox mFridayCheckBox;
    private CheckBox mSaturdayCheckBox;

    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addsubject);

        // Find all relevant views that we will need to read user input from
        mNameEditText =  findViewById(R.id.edit_Subject_name);
        mPresentEditText = findViewById(R.id.edit_present);
        mAbsentEditText = findViewById(R.id.edit_absent);
        mCriteriaEditText = findViewById(R.id.criteria);
        mMondayCheckBox = findViewById(R.id.checkbox_m);
        mTuesdayCheckBox = findViewById(R.id.checkbox_t);
        mWednesdayCheckBox = findViewById(R.id.checkbox_w);
        mThursdayCheckBox = findViewById(R.id.checkbox_th);
        mFridayCheckBox = findViewById(R.id.checkbox_f);
        mSaturdayCheckBox = findViewById(R.id.checkbox_s);

        uri = getIntent().getData();
        if(uri == null){
            setTitle("Add a Subject");
        }
        else{
            setTitle("Edit Subject");
            getLoaderManager().initLoader(0,null,this);
        }

    }


    private void addSubject() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String present = mPresentEditText.getText().toString().trim();
        String absent = mAbsentEditText.getText().toString().trim();
        String criteriaString = mCriteriaEditText.getText().toString().trim();

        if(TextUtils.isEmpty(nameString)){
            finish();
            Toast.makeText(this, "No Subject Name Provided",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(present)){
            present = "0";
            int count = 0;
            if(TextUtils.isEmpty(absent)){
                absent = "0";
                count = 1;
                inner(nameString,present,absent,criteriaString);
            }
            if(count==0) {
                inner(nameString, present, absent, criteriaString);
            }
        }
        else if(TextUtils.isEmpty(absent)){
            absent = "0";
            inner(nameString,present,absent,criteriaString);
        }
        else {
            inner(nameString,present,absent,criteriaString);
        }

    }
    private void inner(String nameString,String presentString,String absentString,String CriteriaString){

        boolean monday = mMondayCheckBox.isChecked();
        boolean tuesday = mTuesdayCheckBox.isChecked();
        boolean wednesday = mWednesdayCheckBox.isChecked();
        boolean thursday = mThursdayCheckBox.isChecked();
        boolean friday = mFridayCheckBox.isChecked();
        boolean saturday = mSaturdayCheckBox.isChecked();

        ContentValues values = new ContentValues();
        values.put(ManagerContract.SubjectEntry.COLUMN_SUBJECT_NAME, nameString);
        int present = Integer.parseInt(presentString);
        values.put(ManagerContract.SubjectEntry.COLUMN_PRESENT, present);
        int absent = Integer.parseInt(absentString);
        values.put(ManagerContract.SubjectEntry.COLUMN_ABSENT, absent);
        int criteria = Integer.parseInt(CriteriaString);
        values.put(ManagerContract.SubjectEntry.COLUMN_CRITERIA, criteria);
        values.put(ManagerContract.SubjectEntry.COLUMN_CHECKBOX_MON,boolToInt(monday));
        values.put(ManagerContract.SubjectEntry.COLUMN_CHECKBOX_TUE,boolToInt(tuesday));
        values.put(ManagerContract.SubjectEntry.COLUMN_CHECKBOX_WED,boolToInt(wednesday));
        values.put(ManagerContract.SubjectEntry.COLUMN_CHECKBOX_THU,boolToInt(thursday));
        values.put(ManagerContract.SubjectEntry.COLUMN_CHECKBOX_FRI,boolToInt(friday));
        values.put(ManagerContract.SubjectEntry.COLUMN_CHECKBOX_SAT,boolToInt(saturday));

        if (uri == null) {
            Uri newUri = getContentResolver().insert(ManagerContract.SubjectEntry.CONTENT_URI, values);
            values.put(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_MON_COLOUR, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_NOTMARKED);
            values.put(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_TUE_COLOUR, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_NOTMARKED);
            values.put(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_WED_COLOUR, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_NOTMARKED);
            values.put(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_THU_COLOUR, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_NOTMARKED);
            values.put(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_FRI_COLOUR, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_NOTMARKED);
            values.put(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_SAT_COLOUR, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_NOTMARKED);
            values.put(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_SUN_COLOUR, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_NOTMARKED);

            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "Error While Inserting",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, "Subject Added",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int NoOfRows = getContentResolver().update(uri, values, null, null);

            if (NoOfRows == 0) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "Error while Editing",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, "Edition Successful",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                    addSubject();
                    // Exit activity
                    finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ManagerContract.SubjectEntry._ID,
                ManagerContract.SubjectEntry.COLUMN_SUBJECT_NAME,
                ManagerContract.SubjectEntry.COLUMN_PRESENT,
                ManagerContract.SubjectEntry.COLUMN_ABSENT,
                ManagerContract.SubjectEntry.COLUMN_CRITERIA,
                ManagerContract.SubjectEntry.COLUMN_CHECKBOX_MON,
                ManagerContract.SubjectEntry.COLUMN_CHECKBOX_TUE,
                ManagerContract.SubjectEntry.COLUMN_CHECKBOX_WED,
                ManagerContract.SubjectEntry.COLUMN_CHECKBOX_THU,
                ManagerContract.SubjectEntry.COLUMN_CHECKBOX_FRI,
                ManagerContract.SubjectEntry.COLUMN_CHECKBOX_SAT,
                ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_MON_COLOUR,
                ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_TUE_COLOUR,
                ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_WED_COLOUR,
                ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_THU_COLOUR,
                ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_FRI_COLOUR,
                ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_SAT_COLOUR,
                ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_SUN_COLOUR
        };

        return new CursorLoader(this, uri,projection,null,null,null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor.moveToFirst()) {
            mNameEditText.setText(cursor.getString(cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_SUBJECT_NAME)));
            mPresentEditText.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_PRESENT))));
            mAbsentEditText.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_ABSENT))));
            mCriteriaEditText.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_CRITERIA))));
            mMondayCheckBox.setChecked(intToBool(cursor.getInt(cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_CHECKBOX_MON))));
            mTuesdayCheckBox.setChecked(intToBool(cursor.getInt(cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_CHECKBOX_TUE))));
            mWednesdayCheckBox.setChecked(intToBool(cursor.getInt(cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_CHECKBOX_WED))));
            mThursdayCheckBox.setChecked(intToBool(cursor.getInt(cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_CHECKBOX_THU))));
            mFridayCheckBox.setChecked(intToBool(cursor.getInt(cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_CHECKBOX_FRI))));
            mSaturdayCheckBox.setChecked(intToBool(cursor.getInt(cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_CHECKBOX_SAT))));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText(null);
        mPresentEditText.setText(null);
        mAbsentEditText.setText(null);
        mMondayCheckBox.setChecked(false);
        mTuesdayCheckBox.setChecked(false);
        mWednesdayCheckBox.setChecked(false);
        mThursdayCheckBox.setChecked(false);
        mFridayCheckBox.setChecked(false);
        mSaturdayCheckBox.setChecked(false);
    }

    public int boolToInt(boolean bool){
        if(bool){
            return 1;
        }
        else{
            return 0;
        }
    }

    public boolean intToBool(int number){
        if(number == 1){
            return true;
        }
        else{
            return false;
        }
    }
}
