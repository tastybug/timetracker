package com.tastybug.timetracker.gui.shared;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TimePicker;

import com.tastybug.timetracker.R;
import com.tastybug.timetracker.task.OttoProvider;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

/**
 * TODO vielleicht kann man diese Klasse von DatePickerFragment ableiten und etwas Code sparen
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private OttoProvider ottoProvider = new OttoProvider();
    private String topic;
    private boolean canReturnNone = true;

    public TimePickerFragment() {}

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setCanNotReturnNone() {
        this.canReturnNone = false;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LocalTime localTime = new LocalTime();

        if (TextUtils.isEmpty(topic)) {
            throw new RuntimeException("No topic specified!");
        }
        TimePickerDialog dialog = new TimePickerDialog(getActivity(),
                this,
                localTime.getHourOfDay(),
                localTime.getMinuteOfHour(),
                true);
        if (canReturnNone) {
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.set_no_date), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_NEGATIVE) {
                        dismiss();
                        ottoProvider.getSharedBus().post(new DatePickerFragment.DatePickedEvent(topic, null));
                    }
                }
            });
        }

        return dialog;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        DateTime dateTime = new DateTime();
        dateTime = dateTime.withHourOfDay(hourOfDay);
        dateTime = dateTime.withMinuteOfHour(minute);
        ottoProvider.getSharedBus().post(new DatePickerFragment.DatePickedEvent(topic, dateTime));
    }
}