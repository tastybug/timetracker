package com.tastybug.timetracker.core.ui.projectconfiguration;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;

import com.tastybug.timetracker.R;

import org.joda.time.LocalDate;

import java.util.Date;

public class DatePickerPreference extends DialogPreference {
    private int dayOfMonth = 0;
    private int monthOfYear = 0;
    private int year = 0;

    private DatePicker picker = null;

    public DatePickerPreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);

        setPositiveButtonText(R.string.set_date);
        setNegativeButtonText(R.string.set_no_date);
    }

    @Override
    protected View onCreateDialogView() {
        picker = new DatePicker(getContext());
        picker.setCalendarViewShown(false);

        return (picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        picker.updateDate(year, monthOfYear, dayOfMonth);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            LocalDate date = new LocalDate(picker.getYear(), picker.getMonth() + 1, picker.getDayOfMonth());
            persistLong(date.toDate().getTime());
        } else {
            persistLong(-1l); // set no date
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        LocalDate initialLocalDate;

        if (restoreValue) {
            if (defaultValue == null) {
                initialLocalDate = new LocalDate(new Date());
            } else {
                initialLocalDate = new LocalDate(new Date(getPersistedLong(new Date().getTime())));
            }
        } else {
            initialLocalDate = new LocalDate(new Date((Long) defaultValue));
        }
        year = initialLocalDate.getYear();
        monthOfYear = initialLocalDate.getMonthOfYear() - 1;
        dayOfMonth = initialLocalDate.getDayOfMonth() - 1;
    }
}