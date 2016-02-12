package com.tastybug.timetracker.gui.timeframe.edit;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.common.base.Optional;
import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.gui.shared.DatePickerFragment;
import com.tastybug.timetracker.gui.shared.TimePickerFragment;
import com.tastybug.timetracker.model.TimeFrame;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.tracking.ModifyTimeFrameTask;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeFrameEditingFragment extends Fragment {

    private static final String START_DATE = "START_DATE";
    private static final String START_TIME = "START_TIME";
    private static final String END_DATE = "END_DATE";
    private static final String END_TIME = "END_TIME";
    private static final String DESCRIPTION = "DESCRIPTION";

    private EditText startDateEditText;
    private EditText startTimeEditText;
    private EditText endDateEditText;
    private EditText endTimeEditText;
    private EditText descriptionEditText;

    private String timeFrameUuid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_time_frame_editing, container);

        startDateEditText = (EditText) rootview.findViewById(R.id.start_date);
        startTimeEditText = (EditText) rootview.findViewById(R.id.start_time);
        endDateEditText = (EditText) rootview.findViewById(R.id.end_date);
        endTimeEditText = (EditText) rootview.findViewById(R.id.end_time);
        descriptionEditText = (EditText) rootview.findViewById(R.id.time_frame_description);


        if (savedInstanceState != null) {
            renderStartDate(Optional.fromNullable((Date)savedInstanceState.getSerializable(START_DATE)));
            renderStartTime(Optional.fromNullable((Date)savedInstanceState.getSerializable(START_TIME)));
            renderEndDate(Optional.fromNullable((Date)savedInstanceState.getSerializable(END_DATE)));
            renderEndTime(Optional.fromNullable((Date)savedInstanceState.getSerializable(END_TIME)));
            renderDescription(Optional.fromNullable(savedInstanceState.getString(DESCRIPTION)));
        }

        startDateEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.setTopic(START_DATE);
                datePickerFragment.setCanNotReturnNone();
                datePickerFragment.show(getFragmentManager(), DatePickerFragment.class.getSimpleName());
            }
        });

        startTimeEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.setTopic(START_TIME);
                timePickerFragment.setCanNotReturnNone();
                timePickerFragment.show(getFragmentManager(), DatePickerFragment.class.getSimpleName());
            }
        });

        endDateEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.setTopic(END_DATE);
                datePickerFragment.setCanNotReturnNone();
                datePickerFragment.show(getFragmentManager(), DatePickerFragment.class.getSimpleName());
            }
        });

        endTimeEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.setTopic(END_TIME);
                timePickerFragment.setCanNotReturnNone();
                timePickerFragment.show(getFragmentManager(), DatePickerFragment.class.getSimpleName());
            }
        });

        new OttoProvider().getSharedBus().register(this);
        return rootview;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(START_DATE, getStartDateFromWidget().orNull());
        outState.putSerializable(START_TIME, getStartTimeFromWidget().orNull());
        outState.putSerializable(END_DATE, getEndDateFromWidget().orNull());
        outState.putSerializable(END_TIME, getEndTimeFromWidget().orNull());
        outState.putString(DESCRIPTION, getDescriptionFromWidget());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        new OttoProvider().getSharedBus().unregister(this);
    }

    public void showTimeFrameData(TimeFrame timeFrame) {
        this.timeFrameUuid = timeFrame.getUuid();

        renderStartDate(timeFrame.getStart());
        renderStartTime(timeFrame.getStart());
        renderEndDate(timeFrame.getEnd());
        renderEndTime(timeFrame.getEnd());
        renderDescription(timeFrame.getDescription());
    }

    private void renderStartDate(Optional<Date> dateOptional) {
        if(dateOptional.isPresent()) {
            SimpleDateFormat f = new SimpleDateFormat("dd.MM.yy", Locale.US);
            startDateEditText.setText(getString(R.string.from_X, f.format(dateOptional.get())));
            startDateEditText.setTag(dateOptional.get());
        }
    }

    private void renderStartTime(Optional<Date> dateOptional) {
        if(dateOptional.isPresent()) {
            SimpleDateFormat f = new SimpleDateFormat("HH:mm", Locale.US);
            startTimeEditText.setText(f.format(dateOptional.get()));
            startTimeEditText.setTag(dateOptional.get());
        }
    }

    private void renderEndDate(Optional<Date> dateOptional) {
        if(dateOptional.isPresent()) {
            SimpleDateFormat f = new SimpleDateFormat("dd.MM.yy", Locale.US);
            endDateEditText.setText(getString(R.string.until_X, f.format(dateOptional.get())));
            endDateEditText.setTag(dateOptional.get());
        }
    }

    private void renderEndTime(Optional<Date> dateOptional) {
        if(dateOptional.isPresent()) {
            SimpleDateFormat f = new SimpleDateFormat("HH:mm", Locale.US);
            endTimeEditText.setText(f.format(dateOptional.get()));
            endTimeEditText.setTag(dateOptional.get());
        }
    }

    private void renderDescription(Optional<String> description) {
        descriptionEditText.setText(description.isPresent() ? description.get() : "");
    }

    private Optional<Date> getStartDateFromWidget() {
        return Optional.fromNullable((Date) startDateEditText.getTag());
    }

    private Optional<Date> getStartTimeFromWidget() {
        return Optional.fromNullable((Date) startTimeEditText.getTag());
    }

    private Optional<Date> getEndDateFromWidget() {
        return Optional.fromNullable((Date) endDateEditText.getTag());
    }

    private Optional<Date> getEndTimeFromWidget() {
        return Optional.fromNullable((Date) endTimeEditText.getTag());
    }

    private String getDescriptionFromWidget() {
        return descriptionEditText.getText().toString();
    }

    private LocalDateTime getAggregatedDate(Optional<Date> date, Optional<Date> time) {
        LocalDate localDate = new LocalDate(date.get());
        LocalTime localTime = new LocalTime(time.get());

        return new LocalDateTime(localDate.getYear(),
                localDate.getMonthOfYear(),
                localDate.getDayOfMonth(),
                localTime.getHourOfDay(),
                localTime.getMinuteOfHour());
    }

    public boolean validateData() {
        Optional<Date> startDateOpt = getStartDateFromWidget();
        Optional<Date> startTimeOpt = getStartTimeFromWidget();
        Optional<Date> endDateOpt = getEndDateFromWidget();
        Optional<Date> endTimeOpt = getEndTimeFromWidget();

        startDateEditText.setError(startDateOpt.isPresent() ? null : getString(R.string.error_date_missing));
        startTimeEditText.setError(startTimeOpt.isPresent() ? null : getString(R.string.error_time_missing));
        endDateEditText.setError(endDateOpt.isPresent() ? null : getString(R.string.error_date_missing));
        endTimeEditText.setError(endTimeOpt.isPresent() ? null : getString(R.string.error_time_missing));
        if (startDateEditText.getError() != null
                || startTimeEditText.getError() != null
                || endDateEditText.getError() != null
                || endTimeEditText.getError() != null) {
            return false;
        }

        LocalDateTime startDateTime = getAggregatedDate(startDateOpt, startTimeOpt);
        LocalDateTime endDateTime = getAggregatedDate(endDateOpt, endTimeOpt);
        return ensureEndDateIsAfterStartDate(startDateTime, endDateTime);
    }

    private boolean ensureEndDateIsAfterStartDate(LocalDateTime startDate, LocalDateTime endDate) {
        endDateEditText.setError(endDate.isAfter(startDate) ? null : getString(R.string.error_end_before_start));
        endTimeEditText.setError(endDate.isAfter(startDate) ? null : getString(R.string.error_end_before_start));
        return endDateEditText.getError() == null;
    }

    public void collectModifications(ModifyTimeFrameTask task) {
        Optional<Date> startDateOpt = getStartDateFromWidget();
        Optional<Date> startTimeOpt = getStartTimeFromWidget();
        Optional<Date> endDateOpt = getEndDateFromWidget();
        Optional<Date> endTimeOpt = getEndTimeFromWidget();

        task.withTimeFrameUuid(timeFrameUuid)
                .withStartDate(getAggregatedDate(startDateOpt, startTimeOpt).toDate())
                .withEndDate(getAggregatedDate(endDateOpt, endTimeOpt).toDate())
                .withDescription(getDescriptionFromWidget());
    }

    @Subscribe
    public void handleDatePicked(DatePickerFragment.DatePickedEvent event) {
        if (START_DATE.equals(event.getTopic())) {
            renderStartDate(Optional.of(event.getDate().get().toDate()));
        } else if (START_TIME.equals(event.getTopic())) {
            renderStartTime(Optional.of(event.getDate().get().toDate()));
        } else if (END_DATE.equals(event.getTopic())) {
            renderEndDate(Optional.of(event.getDate().get().toDate()));
        } else if (END_TIME.equals(event.getTopic())) {
            renderEndTime(Optional.of(event.getDate().get().toDate()));
        } else {
            throw new RuntimeException("Unexpected topic received: " + event.getTopic());
        }
    }
}
