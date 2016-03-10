package com.tastybug.timetracker.gui.trackingrecord.edit;

import android.app.FragmentManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.common.base.Optional;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.gui.dialog.DatePickerDialogFragment;
import com.tastybug.timetracker.gui.dialog.TimePickerDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TrackingRecordModificationUI {

    public static final String START_DATE_TOPIC = "START_DATE_TOPIC";
    public static final String START_TIME_TOPIC = "START_TIME_TOPIC";
    public static final String END_DATE_TOPIC = "END_DATE_TOPIC";
    public static final String END_TIME_TOPIC = "END_TIME_TOPIC";

    private EditText startDateEditText;
    private EditText startTimeEditText;
    private EditText endDateEditText;
    private EditText endTimeEditText;
    private EditText descriptionEditText;

    private Context context;

    public TrackingRecordModificationUI(Context context) {
        this.context = context;
    }

    public View inflateWidgets(LayoutInflater inflater, ViewGroup container, final FragmentManager fragmentManager) {
        View rootView = inflater.inflate(R.layout.fragment_tracking_record_editing, container);

        startDateEditText = (EditText) rootView.findViewById(R.id.start_date);
        startTimeEditText = (EditText) rootView.findViewById(R.id.start_time);
        endDateEditText = (EditText) rootView.findViewById(R.id.end_date);
        endTimeEditText = (EditText) rootView.findViewById(R.id.end_time);
        descriptionEditText = (EditText) rootView.findViewById(R.id.tracking_record_description);


        startDateEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
                datePickerDialogFragment.setTopic(START_DATE_TOPIC);
                datePickerDialogFragment.setCanNotReturnNone();
                datePickerDialogFragment.show(fragmentManager, DatePickerDialogFragment.class.getSimpleName());
            }
        });

        startTimeEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TimePickerDialogFragment timePickerDialogFragment = new TimePickerDialogFragment();
                timePickerDialogFragment.setTopic(START_TIME_TOPIC);
                timePickerDialogFragment.setCanNotReturnNone();
                timePickerDialogFragment.show(fragmentManager, DatePickerDialogFragment.class.getSimpleName());
            }
        });

        endDateEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
                datePickerDialogFragment.setTopic(END_DATE_TOPIC);
                datePickerDialogFragment.setCanNotReturnNone();
                datePickerDialogFragment.show(fragmentManager, DatePickerDialogFragment.class.getSimpleName());
            }
        });

        endTimeEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TimePickerDialogFragment timePickerDialogFragment = new TimePickerDialogFragment();
                timePickerDialogFragment.setTopic(END_TIME_TOPIC);
                timePickerDialogFragment.setCanNotReturnNone();
                timePickerDialogFragment.show(fragmentManager, DatePickerDialogFragment.class.getSimpleName());
            }
        });

        return rootView;
    }


    public void renderStartDate(Optional<Date> dateOptional) {
        if(dateOptional.isPresent()) {
            SimpleDateFormat f = new SimpleDateFormat("dd.MM.yy", Locale.US);
            startDateEditText.setText(context.getString(R.string.from_X, f.format(dateOptional.get())));
            startDateEditText.setTag(dateOptional.get());
        }
    }

    public void renderStartTime(Optional<Date> dateOptional) {
        if(dateOptional.isPresent()) {
            SimpleDateFormat f = new SimpleDateFormat("HH:mm", Locale.US);
            startTimeEditText.setText(f.format(dateOptional.get()));
            startTimeEditText.setTag(dateOptional.get());
        }
    }

    public void renderEndDate(Optional<Date> dateOptional) {
        if(dateOptional.isPresent()) {
            SimpleDateFormat f = new SimpleDateFormat("dd.MM.yy", Locale.US);
            endDateEditText.setText(context.getString(R.string.until_X, f.format(dateOptional.get())));
            endDateEditText.setTag(dateOptional.get());
        }
    }

    public void renderEndTime(Optional<Date> dateOptional) {
        if(dateOptional.isPresent()) {
            SimpleDateFormat f = new SimpleDateFormat("HH:mm", Locale.US);
            endTimeEditText.setText(f.format(dateOptional.get()));
            endTimeEditText.setTag(dateOptional.get());
        }
    }

    public void renderDescription(Optional<String> description) {
        descriptionEditText.setText(description.isPresent() ? description.get() : "");
    }

    public Optional<Date> getStartDateFromWidget(boolean blame) {
        Optional<Date> dateOptional = Optional.fromNullable((Date) startDateEditText.getTag());
        if (blame) {
            startDateEditText.setError(dateOptional.isPresent()
                    ? null :
                    context.getString(R.string.error_date_missing));
        }
        return dateOptional;
    }

    public Optional<Date> getStartTimeFromWidget(boolean blame) {
        Optional<Date> dateOptional =  Optional.fromNullable((Date) startTimeEditText.getTag());
        if (blame) {
            startTimeEditText.setError(dateOptional.isPresent()
                    ? null
                    : context.getString(R.string.error_time_missing));
        }
        return dateOptional;
    }

    public Optional<Date> getEndDateFromWidget(boolean blame) {
        Optional<Date> dateOptional = Optional.fromNullable((Date) endDateEditText.getTag());
        if (blame) {
            endDateEditText.setError(dateOptional.isPresent()
                    ? null
                    : context.getString(R.string.error_date_missing));
        }
        return dateOptional;
    }

    public Optional<Date> getEndTimeFromWidget(boolean blame) {
        Optional<Date> dateOptional =  Optional.fromNullable((Date) endTimeEditText.getTag());
        if (blame) {
            endTimeEditText.setError(dateOptional.isPresent()
                    ? null
                    : context.getString(R.string.error_time_missing));
        }
        return dateOptional;
    }

    public Optional<String> getDescriptionFromWidget() {
        return TextUtils.isEmpty(descriptionEditText.getText())
                ? Optional.<String>absent()
                : Optional.of(descriptionEditText.getText().toString());
    }

    public void blameEndDateBeforeStartDate(boolean isErroneous) {
        endDateEditText.setError(!isErroneous
                ? null
                : context.getString(R.string.error_end_before_start));
        endTimeEditText.setError(!isErroneous
                ? null
                : context.getString(R.string.error_end_before_start));

    }
}
