package com.example.attendancemanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.attendancemanager.Data.ManagerContract;

import java.text.DecimalFormat;

import static java.lang.Math.round;

public class ManagerCursorAdapter extends CursorAdapter {

    private Context mContext;

    public ManagerCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_detail, parent, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView subjectName = view.findViewById(R.id.subjectName);
        TextView attendance = view.findViewById(R.id.attendance);
        TextView percentage = view.findViewById(R.id.percentage);
        TextView attend = view.findViewById(R.id.attend);
        int present = cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_PRESENT);
        int absent = cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_ABSENT);

        //Subject name
        subjectName.setText(cursor.getString(cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_SUBJECT_NAME)));

        //Present, Absent, Total
        attendance.setText("P: " + String.valueOf(cursor.getInt(present))
                + "  A: " + String.valueOf(cursor.getInt(absent))
                + "  T: " + String.valueOf(cursor.getInt(present) + cursor.getInt(absent)));

        //Percentage
        double percent;
        String percentString;
        if (cursor.getInt(absent) + cursor.getInt(present) == 0) {
            percent = round(0);
            percentage.setText(String.valueOf((int) percent) + "%");
        } else {
            percent = (double) cursor.getInt(present) * 100 / (cursor.getInt(present) + cursor.getInt(absent));
            DecimalFormat df = new DecimalFormat("#.#");
            percentString = df.format(percent);
            percentage.setText(percentString + "%");
        }

        //Background Colour Of percentage
        int criteriaInt = cursor.getInt(cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_CRITERIA));
        if (percent < criteriaInt) {
            GradientDrawable percentageCircle = (GradientDrawable) percentage.getBackground();
            percentageCircle.setColor(ContextCompat.getColor(mContext, R.color.percentagebelow));
        } else {
            GradientDrawable percentageCircle = (GradientDrawable) percentage.getBackground();
            percentageCircle.setColor(ContextCompat.getColor(mContext, R.color.percentageabove));
        }

        //Attend Next Classes
        double total = (cursor.getInt(present) + cursor.getInt(absent));
        int criteria = (cursor.getInt(cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_CRITERIA)));
        //Attend next Classes
        double toBeAttended = (((0.01) * criteria * total) - cursor.getInt(present)) / (1 - (0.01) * criteria);
        if (toBeAttended > 0) {
            int attended = (int) Math.ceil(toBeAttended);
            attend.setText("Attend next " + String.valueOf(attended) + " classes");
        } else {
            GradientDrawable percentageCircle = (GradientDrawable) percentage.getBackground();
            percentageCircle.setColor(ContextCompat.getColor(mContext, R.color.percentageabove));
            attend.setText("You're all Set");
        }

        //Checkbox

        TextView monday = view.findViewById(R.id.check_mon);
        TextView tuesday = view.findViewById(R.id.check_tue);
        TextView wednesday = view.findViewById(R.id.check_wed);
        TextView thursday = view.findViewById(R.id.check_thu);
        TextView friday = view.findViewById(R.id.check_fri);
        TextView saturday = view.findViewById(R.id.check_sat);
        monday.setVisibility(View.GONE);
        tuesday.setVisibility(View.GONE);
        wednesday.setVisibility(View.GONE);
        thursday.setVisibility(View.GONE);
        friday.setVisibility(View.GONE);
        saturday.setVisibility(View.GONE);
        int mon = cursor.getInt(cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_CHECKBOX_MON));
        int tue = cursor.getInt(cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_CHECKBOX_TUE));
        int wed = cursor.getInt(cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_CHECKBOX_WED));
        int thu = cursor.getInt(cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_CHECKBOX_THU));
        int fri = cursor.getInt(cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_CHECKBOX_FRI));
        int sat = cursor.getInt(cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_CHECKBOX_SAT));

        if (mon == 1) {
            monday.setVisibility(View.VISIBLE);
        }
        if (tue == 1) {
            tuesday.setVisibility(View.VISIBLE);
        }

        if (wed == 1) {
            wednesday.setVisibility(View.VISIBLE);
        }

        if (thu == 1) {
            thursday.setVisibility(View.VISIBLE);
        }

        if (fri == 1) {
            friday.setVisibility(View.VISIBLE);
        }
        if (sat == 1) {
            saturday.setVisibility(View.VISIBLE);
        }


        GradientDrawable gradientDrawableMon = (GradientDrawable) monday.getBackground();
        int colourMon = cursor.getInt(cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_MON_COLOUR));
        gradientDrawableMon.setColor(ContextCompat.getColor(mContext, intToColour(colourMon)));

        GradientDrawable gradientDrawableTue = (GradientDrawable) tuesday.getBackground();
        int colourTue = cursor.getInt(cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_TUE_COLOUR));
        gradientDrawableTue.setColor(ContextCompat.getColor(mContext, intToColour(colourTue)));

        GradientDrawable gradientDrawableWed = (GradientDrawable) wednesday.getBackground();
        int colourWed = cursor.getInt(cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_WED_COLOUR));
        gradientDrawableWed.setColor(ContextCompat.getColor(mContext, intToColour(colourWed)));

        GradientDrawable gradientDrawableThu = (GradientDrawable) thursday.getBackground();
        int colourThu = cursor.getInt(cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_THU_COLOUR));
        gradientDrawableThu.setColor(ContextCompat.getColor(mContext, intToColour(colourThu)));

        GradientDrawable gradientDrawableFri = (GradientDrawable) friday.getBackground();
        int colourFri = cursor.getInt(cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_FRI_COLOUR));
        gradientDrawableFri.setColor(ContextCompat.getColor(mContext, intToColour(colourFri)));

        GradientDrawable gradientDrawableSat = (GradientDrawable) saturday.getBackground();
        int colourSat = cursor.getInt(cursor.getColumnIndex(ManagerContract.SubjectEntry.COLUMN_TEXTVIEW_SAT_COLOUR));
        gradientDrawableSat.setColor(ContextCompat.getColor(mContext, intToColour(colourSat)));

    }

    public int intToColour(int number) {
        int colour;
        switch (number) {
            case ManagerContract.SubjectEntry.STYLE_TEXTVIEW_PRESENT:
                colour = R.color.weekPresent;
                break;
            case ManagerContract.SubjectEntry.STYLE_TEXTVIEW_ABSENT:
                colour = R.color.weekAbsent;
                break;
            case ManagerContract.SubjectEntry.STYLE_TEXTVIEW_CANCELLED:
                colour = R.color.weekCancelled;
                break;
            case ManagerContract.SubjectEntry.STYLE_TEXTVIEW_NOTMARKED:
                colour = R.color.weekNotMarked;
                break;
            default:
                colour = R.color.weekNotMarked;
        }
        return colour;
    }


    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
    }

}
