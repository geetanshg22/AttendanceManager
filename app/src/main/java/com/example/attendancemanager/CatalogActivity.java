package com.example.attendancemanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.design.widget.FloatingActionButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendancemanager.Data.ManagerContract;

import java.text.DateFormat;
import java.util.Calendar;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ManagerCursorAdapter mAdapter;
    Spinner daySelectorSpinner;
    private String selectedDay;
    private String currentDay;
    private ListView catalogList;
    private Cursor mCursor;
    private int check = 0;
    private String selection;
    private int markDialogCount;
    private String selectionTextCircle;

//    private final BroadcastReceiver m = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//           // if(intent.getAction().equals(Intent.ACTION_DATE_CHANGED)){
//                Toast.makeText(context,"DATE CHANFED",Toast.LENGTH_LONG).show();
//                System.out.println("DATE CHANGED");
//            //}
//        }
//    };
//
//    private static final IntentFilter s;
//
//    static {
//        s = new IntentFilter();
//        s.addAction(Intent.ACTION_DATE_CHANGED);
//    }
//
//    public void onDestroy(){
//        super.onDestroy();
//        unregisterReceiver(m);
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

//        registerReceiver(m,s);

        markDialogCount = 0;

        catalogList = findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        catalogList.setEmptyView(emptyView);

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        currentDay = currentDate.substring(0, 3);

        selectedDay = currentDay;
        setupSpinner();

        getLoaderManager().initLoader(0, null, this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CatalogActivity.this, AddSubjectActivity.class);
                startActivity(intent);

            }
        });

        onClickWeek();
        mAdapter = new ManagerCursorAdapter(this, null);
        catalogList.setAdapter(mAdapter);

    }

    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter daySelectorSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_day_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        daySelectorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        daySelectorSpinner = findViewById(R.id.select_day);

        // Apply the adapter to the spinner
        daySelectorSpinner.setAdapter(daySelectorSpinnerAdapter);
        daySelectorSpinner.setSelection(dayToInt(currentDay));

        // Set the integer mSelected to the constant values
        daySelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals("Monday")) {
                        selectedDay = "Mon";
                    } else if (selection.equals("Tuesday")) {
                        selectedDay = "Tue";
                    } else if (selection.equals("Wednesday")) {
                        selectedDay = "Wed";
                    } else if (selection.equals("Thursday")) {
                        selectedDay = "Thu";
                    } else if (selection.equals("Friday")) {
                        selectedDay = "Fri";
                    } else if (selection.equals("Saturday")) {
                        selectedDay = "Sat";
                    } else if (selection.equals("Sunday")) {
                        selectedDay = "Sun";
                    } else {
                        selectedDay = null;
                    }
                } else {
                    selectedDay = null;
                }

                getLoaderManager().restartLoader(0, null, CatalogActivity.this);
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDay = currentDay;
            }

        });

        //Mark attendance
        final ArrayAdapter markAttendanceSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_attendance_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        markAttendanceSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        final Spinner markAttendanceSpinner = findViewById(R.id.mark_attendance);
        // Apply the adapter to the spinner
        markAttendanceSpinner.setAdapter(markAttendanceSpinnerAdapter);

        // Set the integer mSelected to the constant values
        markAttendanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, final long id) {
                if (markDialogCount != 0) {
                    if (id == 1) {

                        AlertDialog.Builder attendanceDialog = new AlertDialog.Builder(CatalogActivity.this);
                        attendanceDialog.setTitle("Mark Attendance For Whole Day");

                        attendanceDialog.setPositiveButton("Mark", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mCursor.moveToPosition(-1);
                                while (mCursor.moveToNext()) {

                                    Uri uriAllAttendance = ContentUris.withAppendedId(ManagerContract.SubjectEntry.CONTENT_URI, mCursor.getInt(mCursor.getColumnIndex(ManagerContract.SubjectEntry._ID)));

                                    if (mCursor.getInt(mCursor.getColumnIndex(selectionTextCircle)) == 2) {
                                        ContentValues presentValues = new ContentValues();
                                        presentValues.put(ManagerContract.SubjectEntry.COLUMN_PRESENT, mCursor.getInt(mCursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_PRESENT)) + 1);
                                        presentValues.put(ManagerContract.SubjectEntry.COLUMN_ABSENT, mCursor.getInt(mCursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_ABSENT)) - 1);
                                        presentValues.put(selectionTextCircle, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_PRESENT);
                                        getContentResolver().update(uriAllAttendance, presentValues, null, null);
                                    }
                                    if (mCursor.getInt(mCursor.getColumnIndex(selectionTextCircle)) == 1) {
                                    } else {
                                        ContentValues presentValues = new ContentValues();
                                        presentValues.put(ManagerContract.SubjectEntry.COLUMN_PRESENT, mCursor.getInt(mCursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_PRESENT)) + 1);
                                        presentValues.put(selectionTextCircle, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_PRESENT);
                                        getContentResolver().update(uriAllAttendance, presentValues, null, null);
                                    }
                                }
                            }

                        });
                        attendanceDialog.setNegativeButton("Cancel", null);

                        AlertDialog dialog = attendanceDialog.create();
                        dialog.show();

                    } else if (id == 2) {
                        AlertDialog.Builder attendanceDialog = new AlertDialog.Builder(CatalogActivity.this);
                        attendanceDialog.setTitle("Mark Absent for whole Day");

                        attendanceDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mCursor.moveToPosition(-1);
                                while (mCursor.moveToNext()) {

                                    Uri uriAllAttendance = ContentUris.withAppendedId(ManagerContract.SubjectEntry.CONTENT_URI, mCursor.getInt(mCursor.getColumnIndex(ManagerContract.SubjectEntry._ID)));

                                    if (mCursor.getInt(mCursor.getColumnIndex(selectionTextCircle)) == 1) {
                                        ContentValues presentValues = new ContentValues();
                                        presentValues.put(ManagerContract.SubjectEntry.COLUMN_PRESENT, mCursor.getInt(mCursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_PRESENT)) - 1);
                                        presentValues.put(ManagerContract.SubjectEntry.COLUMN_ABSENT, mCursor.getInt(mCursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_ABSENT)) + 1);
                                        presentValues.put(selectionTextCircle, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_ABSENT);
                                        getContentResolver().update(uriAllAttendance, presentValues, null, null);
                                    }
                                    if (mCursor.getInt(mCursor.getColumnIndex(selectionTextCircle)) == 2) {
                                    } else {
                                        ContentValues presentValues = new ContentValues();
                                        presentValues.put(ManagerContract.SubjectEntry.COLUMN_ABSENT, mCursor.getInt(mCursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_ABSENT)) + 1);
                                        presentValues.put(selectionTextCircle, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_ABSENT);
                                        getContentResolver().update(uriAllAttendance, presentValues, null, null);
                                    }
                                }
                            }

                        });
                        attendanceDialog.setNegativeButton("Cancel", null);

                        AlertDialog dialog = attendanceDialog.create();
                        dialog.show();
                    }

                }
                markDialogCount = 1;
                markAttendanceSpinner.setSelection(0);
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
    }

    public int dayToInt(String day) {

        int i = 0;

        switch (day) {

            case "Mon":
                i = 1;
                break;
            case "Tue":
                i = 2;
                break;
            case "Wed":
                i = 3;
                break;
            case "Thu":
                i = 4;
                break;
            case "Fri":
                i = 5;
                break;
            case "Sat":
                i = 6;
                break;
            case "Sun":
                i = 7;
                break;
            default:
                i = 0;
                break;
        }
        return i;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllData();
                return true;
            case R.id.action_reset_all_entries:
                resetAllData();
                return true;
            case R.id.action_reset_week_colour:
                resetWeekColour();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void resetWeekColour() {

        ContentValues values = new ContentValues();
        values.put(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_MON_COLOUR, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_NOTMARKED);
        values.put(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_TUE_COLOUR, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_NOTMARKED);
        values.put(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_WED_COLOUR, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_NOTMARKED);
        values.put(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_THU_COLOUR, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_NOTMARKED);
        values.put(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_FRI_COLOUR, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_NOTMARKED);
        values.put(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_SAT_COLOUR, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_NOTMARKED);
        values.put(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_SUN_COLOUR, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_NOTMARKED);
        getContentResolver().update(ManagerContract.SubjectEntry.CONTENT_URI, values, null, null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void deleteAllData() {
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this);
        deleteDialog.setTitle("Delete All Data");
        deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int noOfRows = getContentResolver().delete(ManagerContract.SubjectEntry.CONTENT_URI, null, null);
                Toast.makeText(CatalogActivity.this, "Total Rows Deleted : " + noOfRows, Toast.LENGTH_SHORT).show();
            }
        });
        deleteDialog.setNegativeButton("No", null);
        AlertDialog dialog = deleteDialog.create();
        dialog.show();
    }

    public void resetAllData() {
        AlertDialog.Builder resetDialog = new AlertDialog.Builder(this);
        resetDialog.setTitle("Reset All Data");
        resetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ContentValues values = new ContentValues();
                values.put(ManagerContract.SubjectEntry.COLUMN_ABSENT, 0);
                values.put(ManagerContract.SubjectEntry.COLUMN_PRESENT, 0);
                values.put(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_MON_COLOUR, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_NOTMARKED);
                values.put(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_TUE_COLOUR, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_NOTMARKED);
                values.put(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_WED_COLOUR, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_NOTMARKED);
                values.put(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_THU_COLOUR, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_NOTMARKED);
                values.put(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_FRI_COLOUR, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_NOTMARKED);
                values.put(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_SAT_COLOUR, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_NOTMARKED);
                values.put(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_SUN_COLOUR, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_NOTMARKED);
                getContentResolver().update(ManagerContract.SubjectEntry.CONTENT_URI, values, null, null);

            }
        });
        resetDialog.setNegativeButton("No", null);
        AlertDialog dialog = resetDialog.create();
        dialog.show();
    }

    public void onClickWeek() {
        catalogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {

                final Uri uri = ContentUris.withAppendedId(ManagerContract.SubjectEntry.CONTENT_URI, id);

                innerOnClickWeek(selectionTextCircle, selectedDay, uri);

                TextView monday = view.findViewById(R.id.check_mon);
                monday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        innerOnClickWeek(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_MON_COLOUR, "Monday", uri);
                    }

                });

                TextView tuesday = view.findViewById(R.id.check_tue);
                tuesday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        innerOnClickWeek(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_TUE_COLOUR, "Tuesday", uri);
                    }

                });

                TextView wednesday = view.findViewById(R.id.check_wed);
                wednesday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        innerOnClickWeek(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_WED_COLOUR, "Wednesday", uri);
                    }

                });

                TextView thursday = view.findViewById(R.id.check_thu);
                thursday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        innerOnClickWeek(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_THU_COLOUR, "Thursday", uri);
                    }

                });

                TextView friday = view.findViewById(R.id.check_fri);
                friday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        innerOnClickWeek(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_FRI_COLOUR, "Friday", uri);
                    }

                });

                TextView saturday = view.findViewById(R.id.check_sat);
                saturday.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        innerOnClickWeek(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_SAT_COLOUR, "Saturday", uri);
                    }

                });
            }
        });

        catalogList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                AlertDialog.Builder optionsDialog = new AlertDialog.Builder(CatalogActivity.this);
                String[] options = {"Edit", "Delete"};
                optionsDialog.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(CatalogActivity.this, AddSubjectActivity.class);
                            intent.setData(ContentUris.withAppendedId(ManagerContract.SubjectEntry.CONTENT_URI, id));
                            startActivity(intent);
                        } else {
                            Uri uri = ContentUris.withAppendedId(ManagerContract.SubjectEntry.CONTENT_URI, id);
                            getContentResolver().delete(uri, null, null);
                        }
                    }
                });
                AlertDialog dialog = optionsDialog.create();
                dialog.show();
                return true;
            }
        });

    }

    public void innerOnClickWeek(final String weekSelect, String day, final Uri uri) {
        AlertDialog.Builder attendanceDialog = new AlertDialog.Builder(CatalogActivity.this);
        int checkedItem = check;
        attendanceDialog.setTitle(day);
        String[] attendance = {"Present", "Absent", "Cancelled"};
        attendanceDialog.setSingleChoiceItems(attendance, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                check = which;
            }
        });

        attendanceDialog.setPositiveButton("Mark", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (check == 0) {
                    if (mCursor.getInt(mCursor.getColumnIndex(weekSelect)) == 2) {
                        ContentValues presentValues = new ContentValues();
                        presentValues.put(ManagerContract.SubjectEntry.COLUMN_PRESENT, mCursor.getInt(mCursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_PRESENT)) + 1);
                        presentValues.put(ManagerContract.SubjectEntry.COLUMN_ABSENT, mCursor.getInt(mCursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_ABSENT)) - 1);
                        presentValues.put(weekSelect, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_PRESENT);
                        getContentResolver().update(uri, presentValues, null, null);
                    }
                    if (mCursor.getInt(mCursor.getColumnIndex(weekSelect)) == 1) {
                    } else {
                        ContentValues presentValues = new ContentValues();
                        presentValues.put(ManagerContract.SubjectEntry.COLUMN_PRESENT, mCursor.getInt(mCursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_PRESENT)) + 1);
                        presentValues.put(weekSelect, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_PRESENT);
                        getContentResolver().update(uri, presentValues, null, null);
                    }
                }
                if (check == 1) {
                    if (mCursor.getInt(mCursor.getColumnIndex(weekSelect)) == 1) {
                        ContentValues presentValues = new ContentValues();
                        presentValues.put(ManagerContract.SubjectEntry.COLUMN_PRESENT, mCursor.getInt(mCursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_PRESENT)) - 1);
                        presentValues.put(ManagerContract.SubjectEntry.COLUMN_ABSENT, mCursor.getInt(mCursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_ABSENT)) + 1);
                        presentValues.put(weekSelect, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_ABSENT);
                        getContentResolver().update(uri, presentValues, null, null);
                    }
                    if (mCursor.getInt(mCursor.getColumnIndex(weekSelect)) == 2) {
                    } else {
                        ContentValues presentValues = new ContentValues();
                        presentValues.put(ManagerContract.SubjectEntry.COLUMN_ABSENT, mCursor.getInt(mCursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_ABSENT)) + 1);
                        presentValues.put(weekSelect, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_ABSENT);
                        getContentResolver().update(uri, presentValues, null, null);
                    }
                }
                if (check == 2) {
                    if (mCursor.getInt(mCursor.getColumnIndex(weekSelect)) == 1) {
                        ContentValues presentValues = new ContentValues();
                        presentValues.put(ManagerContract.SubjectEntry.COLUMN_PRESENT, mCursor.getInt(mCursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_PRESENT)) - 1);
                        presentValues.put(weekSelect, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_PRESENT);
                        getContentResolver().update(uri, presentValues, null, null);
                    }
                    if (mCursor.getInt(mCursor.getColumnIndex(weekSelect)) == 2) {
                        ContentValues presentValues = new ContentValues();
                        presentValues.put(ManagerContract.SubjectEntry.COLUMN_ABSENT, mCursor.getInt(mCursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_ABSENT)) - 1);
                        presentValues.put(weekSelect, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_ABSENT);
                        getContentResolver().update(uri, presentValues, null, null);
                    } else {
                        ContentValues presentValues = new ContentValues();
                        presentValues.put(weekSelect, ManagerContract.SubjectEntry.STYLE_TEXTVIEW_CANCELLED);
                        getContentResolver().update(uri, presentValues, null, null);

                    }
                }
            }

        });
        attendanceDialog.setNegativeButton("Cancel", null);

        AlertDialog dialog = attendanceDialog.create();
        dialog.show();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
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
                ManagerContract.SubjectEntry.COLUMN_CHECKBOX_SUN,
                ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_MON_COLOUR,
                ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_TUE_COLOUR,
                ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_WED_COLOUR,
                ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_THU_COLOUR,
                ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_FRI_COLOUR,
                ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_SAT_COLOUR,
                ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_SUN_COLOUR
        };

        if (selectedDay == null) {
            selectionTextCircle = ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_SUN_COLOUR;
            return new CursorLoader(this, ManagerContract.SubjectEntry.CONTENT_URI, projection, null, null, null);
        } else {

            int column_index = dayToInt(selectedDay);

            switch (column_index) {
                case 1:
                    selection = ManagerContract.SubjectEntry.COLUMN_CHECKBOX_MON;
                    selectionTextCircle = ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_MON_COLOUR;
                    break;
                case 2:
                    selection = ManagerContract.SubjectEntry.COLUMN_CHECKBOX_TUE;
                    selectionTextCircle = ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_TUE_COLOUR;
                    break;
                case 3:
                    selection = ManagerContract.SubjectEntry.COLUMN_CHECKBOX_WED;
                    selectionTextCircle = ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_WED_COLOUR;
                    break;
                case 4:
                    selection = ManagerContract.SubjectEntry.COLUMN_CHECKBOX_THU;
                    selectionTextCircle = ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_THU_COLOUR;
                    break;
                case 5:
                    selection = ManagerContract.SubjectEntry.COLUMN_CHECKBOX_FRI;
                    selectionTextCircle = ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_FRI_COLOUR;
                    break;
                case 6:
                    selection = ManagerContract.SubjectEntry.COLUMN_CHECKBOX_SAT;
                    selectionTextCircle = ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_SAT_COLOUR;
                    break;
                case 7:
                    selection = ManagerContract.SubjectEntry.COLUMN_CHECKBOX_SUN;
                    selectionTextCircle = ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_SUN_COLOUR;
                    break;
                default:
                    selection = ManagerContract.SubjectEntry.COLUMN_CHECKBOX_SUN;
                    selectionTextCircle = ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_SUN_COLOUR;
            }
            String[] args = new String[]{"1"};
            return new CursorLoader(this, ManagerContract.SubjectEntry.CONTENT_URI, projection, selection + "=?", args, null);

        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursor = cursor;
        mAdapter.changeCursor(cursor);
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
