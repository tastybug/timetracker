package com.tastybug.timetracker.gui.project.configuration;

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

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private OttoProvider ottoProvider = new OttoProvider();
    private String topic;

    public DatePickerFragment() {}

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LocalDate localDate = new LocalDate();

        if (TextUtils.isEmpty(topic)) {
            throw new RuntimeException("No topic specified!");
        }
        DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                this,
                localDate.getYear(),
                localDate.getMonthOfYear(),
                localDate.getDayOfMonth());

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.set_no_date), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    dismiss();
                    ottoProvider.getSharedBus().post(new DatePickedEvent(topic, null));
                }
            }
        });

        return dialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        ottoProvider.getSharedBus().post(new DatePickedEvent(topic, new DateTime(year, month, day, 0, 0)));
    }

    static class DatePickedEvent implements OttoEvent {

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