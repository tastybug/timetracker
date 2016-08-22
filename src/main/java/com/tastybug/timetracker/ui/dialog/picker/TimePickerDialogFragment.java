package com.tastybug.timetracker.ui.dialog.picker;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TimePicker;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.util.Date;

/**
 * TODO vielleicht kann man diese Klasse von DatePickerDialogFragment ableiten und etwas Code sparen
 */
public class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private static final String INITIAL_DATE = "INITIAL_DATE";
    private static final String TOPIC = "TOPIC";
    private static final String CAN_RETURN_NONE = "CAN_RETURN_NONE";

    private OttoProvider ottoProvider = new OttoProvider();
    private LocalTime presetTime = new LocalTime();
    private String topic;
    private boolean canReturnNone = true;

    public TimePickerDialogFragment() {
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setCanNotReturnNone() {
        this.canReturnNone = false;
    }

    public void setPresetDate(Date date) {
        presetTime = new LocalTime(date);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.topic = savedInstanceState.getString(TOPIC);
            this.presetTime = (LocalTime) savedInstanceState.getSerializable(INITIAL_DATE);
            this.canReturnNone = savedInstanceState.getBoolean(CAN_RETURN_NONE);
        }
        if (TextUtils.isEmpty(topic)) {
            throw new RuntimeException("No topic specified!");
        }
        TimePickerDialog dialog = new TimePickerDialog(getActivity(),
                this,
                presetTime.getHourOfDay(),
                presetTime.getMinuteOfHour(),
                true);
        if (canReturnNone) {
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.set_no_date), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_NEGATIVE) {
                        dismiss();
                        ottoProvider.getSharedBus().post(new DatePickerDialogFragment.DatePickedEvent(topic, null));
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
        outState.putSerializable(INITIAL_DATE, presetTime);
        outState.putBoolean(CAN_RETURN_NONE, canReturnNone);
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        DateTime selectedDateTime = new DateTime()
                .withHourOfDay(hourOfDay)
                .withMinuteOfHour(minute)
                .withSecondOfMinute(0);
        ottoProvider.getSharedBus().post(new DatePickerDialogFragment.DatePickedEvent(topic, selectedDateTime));
    }
}