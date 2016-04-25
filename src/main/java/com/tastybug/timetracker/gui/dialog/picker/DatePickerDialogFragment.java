package com.tastybug.timetracker.gui.dialog.picker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.DatePicker;

import com.google.common.base.Optional;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.task.OttoEvent;
import com.tastybug.timetracker.task.OttoProvider;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.Date;

public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private static final String INITIAL_DATE = "INITIAL_DATE";
    private static final String TOPIC = "TOPIC";
    private static final String CAN_RETURN_NONE = "CAN_RETURN_NONE";

    private LocalDate presetDate = new LocalDate();
    private OttoProvider ottoProvider = new OttoProvider();
    private String topic;
    private boolean canReturnNone = true;

    public DatePickerDialogFragment() {}

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setCanNotReturnNone() {
        this.canReturnNone = false;
    }

    public void setPresetDate(Date date) {
        this.presetDate = new LocalDate(date);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.topic = savedInstanceState.getString(TOPIC);
            this.presetDate = (LocalDate) savedInstanceState.getSerializable(INITIAL_DATE);
            this.canReturnNone = savedInstanceState.getBoolean(CAN_RETURN_NONE);
        }
        if (TextUtils.isEmpty(topic)) {
            throw new RuntimeException("No topic specified!");
        }
        DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                this,
                presetDate.getYear(),
                presetDate.getMonthOfYear()-1,
                presetDate.getDayOfMonth());
        if (canReturnNone) {
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.set_no_date), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_NEGATIVE) {
                        dismiss();
                        ottoProvider.getSharedBus().post(new DatePickedEvent(topic, null));
                    }
                }
            });
        }

        return dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TOPIC, topic);
        outState.putSerializable(INITIAL_DATE, presetDate);
        outState.putBoolean(CAN_RETURN_NONE, canReturnNone);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        ottoProvider.getSharedBus().post(new DatePickedEvent(topic, new DateTime(year, month+1, day, 0, 0)));
    }

    public static class DatePickedEvent implements OttoEvent {

        private String topic;
        private DateTime date;

        public DatePickedEvent(String topic, DateTime date) {
            this.date = date;
            this.topic = topic;
        }

        public Optional<DateTime> getDate() {
            return Optional.fromNullable(date);
        }

        public String getTopic() {
            return topic;
        }
    }

}