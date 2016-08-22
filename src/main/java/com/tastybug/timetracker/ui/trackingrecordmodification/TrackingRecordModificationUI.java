package com.tastybug.timetracker.ui.trackingrecordmodification;

import android.app.FragmentManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.common.base.Optional;
import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;
import com.tastybug.timetracker.ui.dialog.picker.DatePickerDialogFragment;
import com.tastybug.timetracker.ui.dialog.picker.TimePickerDialogFragment;
import com.tastybug.timetracker.util.Formatter;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.Date;

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
        new OttoProvider().getSharedBus().register(this);
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
                Optional<Date> date = getStartDateFromWidget(false);
                if (date.isPresent()) {
                    datePickerDialogFragment.setPresetDate(date.get());
                }
                datePickerDialogFragment.show(fragmentManager, DatePickerDialogFragment.class.getSimpleName());
            }
        });

        startTimeEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TimePickerDialogFragment timePickerDialogFragment = new TimePickerDialogFragment();
                timePickerDialogFragment.setTopic(START_TIME_TOPIC);
                timePickerDialogFragment.setCanNotReturnNone();
                Optional<Date> date = getStartTimeFromWidget(false);
                if (date.isPresent()) {
                    timePickerDialogFragment.setPresetDate(date.get());
                }
                timePickerDialogFragment.show(fragmentManager, DatePickerDialogFragment.class.getSimpleName());
            }
        });

        endDateEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
                datePickerDialogFragment.setTopic(END_DATE_TOPIC);
                datePickerDialogFragment.setCanNotReturnNone();
                Optional<Date> date = getEndDateFromWidget(false);
                if (date.isPresent()) {
                    datePickerDialogFragment.setPresetDate(date.get());
                }
                datePickerDialogFragment.show(fragmentManager, DatePickerDialogFragment.class.getSimpleName());
            }
        });

        endTimeEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TimePickerDialogFragment timePickerDialogFragment = new TimePickerDialogFragment();
                timePickerDialogFragment.setTopic(END_TIME_TOPIC);
                timePickerDialogFragment.setCanNotReturnNone();
                Optional<Date> date = getEndTimeFromWidget(false);
                if (date.isPresent()) {
                    timePickerDialogFragment.setPresetDate(date.get());
                }
                timePickerDialogFragment.show(fragmentManager, DatePickerDialogFragment.class.getSimpleName());
            }
        });

        return rootView;
    }

    public void destroy() {
        new OttoProvider().getSharedBus().unregister(this);
    }

    public void renderStartDate(Optional<Date> dateOptional) {
        if (dateOptional.isPresent()) {
            startDateEditText.setText(context.getString(R.string.from_X, Formatter.date().format(dateOptional.get())));
            startDateEditText.setTag(dateOptional.get());
        } else {
            startDateEditText.setText("");
            startDateEditText.setTag(null);
        }
    }

    public void renderStartTime(Optional<Date> dateOptional) {
        if (dateOptional.isPresent()) {
            startTimeEditText.setText(Formatter.time().format(dateOptional.get()));
            startTimeEditText.setTag(dateOptional.get());
        } else {
            startTimeEditText.setText("");
            startTimeEditText.setTag(null);
        }
    }

    public void renderEndDate(Optional<Date> dateOptional) {
        if (dateOptional.isPresent()) {
            endDateEditText.setText(context.getString(R.string.until_X, Formatter.date().format(dateOptional.get())));
            endDateEditText.setTag(dateOptional.get());
        } else {
            endDateEditText.setText("");
            endDateEditText.setTag(null);
        }
    }

    public void renderEndTime(Optional<Date> dateOptional) {
        if (dateOptional.isPresent()) {
            endTimeEditText.setText(Formatter.time().format(dateOptional.get()));
            endTimeEditText.setTag(dateOptional.get());
        } else {
            endTimeEditText.setText("");
            endTimeEditText.setTag(null);
        }
    }

    public void renderDescription(Optional<String> description) {
        descriptionEditText.setText(description.isPresent()
                ? description.get()
                : "");
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
        Optional<Date> dateOptional = Optional.fromNullable((Date) startTimeEditText.getTag());
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
        Optional<Date> dateOptional = Optional.fromNullable((Date) endTimeEditText.getTag());
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

    public Optional<Date> getAggregatedStartDate(boolean blame) {
        Optional<Date> startDateOpt = getStartDateFromWidget(blame);
        Optional<Date> startTimeOpt = getStartTimeFromWidget(blame);

        if (!startDateOpt.isPresent() || !startTimeOpt.isPresent()) {
            return Optional.absent();
        }

        return Optional.of(getAggregatedDate(startDateOpt, startTimeOpt).toDate());
    }

    public Optional<Date> getAggregatedEndDate(boolean blame) {
        Optional<Date> endDateOpt = getEndDateFromWidget(blame);
        Optional<Date> endTimeOpt = getEndTimeFromWidget(blame);

        if (!endDateOpt.isPresent() || !endTimeOpt.isPresent()) {
            return Optional.absent();
        }

        return Optional.of(getAggregatedDate(endDateOpt, endTimeOpt).toDate());
    }

    private LocalDateTime getAggregatedDate(Optional<Date> date, Optional<Date> time) {
        LocalDate localDate = new LocalDate(date.get());
        LocalTime localTime = new LocalTime(time.get());

        return new LocalDateTime(localDate.getYear(),
                localDate.getMonthOfYear(),
                localDate.getDayOfMonth(),
                localTime.getHourOfDay(),
                localTime.getMinuteOfHour(),
                localTime.getSecondOfMinute(),
                localTime.getMillisOfSecond());
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void handleDatePicked(DatePickerDialogFragment.DatePickedEvent event) {
        if (TrackingRecordModificationUI.START_DATE_TOPIC.equals(event.getTopic())) {
            renderStartDate(Optional.of(event.getDate().get().toDate()));
        } else if (TrackingRecordModificationUI.START_TIME_TOPIC.equals(event.getTopic())) {
            renderStartTime(Optional.of(event.getDate().get().toDate()));
        } else if (TrackingRecordModificationUI.END_DATE_TOPIC.equals(event.getTopic())) {
            renderEndDate(Optional.of(event.getDate().get().toDate()));
        } else if (TrackingRecordModificationUI.END_TIME_TOPIC.equals(event.getTopic())) {
            renderEndTime(Optional.of(event.getDate().get().toDate()));
        } else {
            throw new RuntimeException("Unexpected topic received: " + event.getTopic());
        }
    }
}
