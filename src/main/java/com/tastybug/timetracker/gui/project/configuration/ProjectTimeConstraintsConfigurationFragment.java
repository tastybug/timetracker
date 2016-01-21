package com.tastybug.timetracker.gui.project.configuration;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.common.base.Optional;
import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.ProjectTimeConstraints;
import com.tastybug.timetracker.model.TimeFrameRounding;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.project.ConfigureProjectTask;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ProjectTimeConstraintsConfigurationFragment extends Fragment {

    private static final String HOUR_LIMIT = "HOUR_LIMIT";
    private static final String START_DATE = "START_DATE";
    private static final String END_DATE = "END_DATE";
    private static final String ROUNDING_STRATEGY = "ROUNDING_STRATEGY";

    private EditText hourLimitEditText;
    private EditText startDateEditText;
    private EditText endDateEditText;
    private Spinner roundingStrategySpinner;

    @Override
    public void onDetach() {
        super.onDetach();
        new OttoProvider().getSharedBus().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_time_constraints_configuration, container);

        hourLimitEditText = (EditText) view.findViewById(R.id.hour_limit);
        startDateEditText = (EditText) view.findViewById(R.id.start_date);
        endDateEditText = (EditText) view.findViewById(R.id.end_date_inclusive);
        roundingStrategySpinner = (Spinner) view.findViewById(R.id.rounding_strategy_spinner);

        if (savedInstanceState != null) {
            renderHourLimit((Integer)savedInstanceState.getSerializable(HOUR_LIMIT));
            renderStartDate((Date) savedInstanceState.getSerializable(START_DATE));
            renderEndDate((Date) savedInstanceState.getSerializable(END_DATE));
            renderRoundingStrategy((TimeFrameRounding.Strategy) savedInstanceState.getSerializable(ROUNDING_STRATEGY));
        }

        startDateEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setTopic(START_DATE);
                newFragment.show(getFragmentManager(), DatePickerFragment.class.getSimpleName());
            }
        });

        endDateEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setTopic(END_DATE);
                newFragment.show(getFragmentManager(), DatePickerFragment.class.getSimpleName());
            }
        });
        new OttoProvider().getSharedBus().register(this);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(START_DATE, getStartDateFromWidget().orNull());
        outState.putSerializable(END_DATE, getLastEnddateInclusiveFromWidget().orNull());
        outState.putSerializable(HOUR_LIMIT, getHourLimitFromWidget().orNull());
        outState.putSerializable(ROUNDING_STRATEGY, getRoundingStrategyFromWidget());
    }

    public void showProjectTimeConstraints(ProjectTimeConstraints projectTimeConstraints) {
        renderHourLimit(projectTimeConstraints.getHourLimit().orNull());
        renderStartDate(projectTimeConstraints.getStart().orNull());
        renderEndDate(projectTimeConstraints.getEndDateAsInclusive().orNull());
        renderRoundingStrategy(projectTimeConstraints.getRoundingStrategy());
    }

    private void renderHourLimit(Integer hourLimit) {
        if(hourLimit != null) {
            hourLimitEditText.setText(hourLimit + "");
        } else {
            hourLimitEditText.setText("");
        }
    }

    private void renderStartDate(Date date) {
        if (date != null) {
            startDateEditText.setText(getString(R.string.project_starts_at_X, date.toString()));
            startDateEditText.setTag(date);
        } else {
            startDateEditText.setText("");
            startDateEditText.setTag(null);
        }
    }

    private void renderEndDate(Date date) {
        if (date != null) {
            endDateEditText.setText(getString(R.string.project_ends_at_X, date.toString()));
            endDateEditText.setTag(date);
        } else {
            endDateEditText.setText("");
            endDateEditText.setTag(null);
        }
    }

    private void renderRoundingStrategy(TimeFrameRounding.Strategy strategy) {
        List<String> strategyList = Arrays.asList(getResources().getStringArray(R.array.rouding_strategy_values));
        roundingStrategySpinner.setSelection(strategyList.indexOf(strategy.name()));
    }

    public boolean validateSettings() {
        Optional<Date> startDateOpt = getStartDateFromWidget();
        Optional<Date> newLastDateOpt = getLastEnddateInclusiveFromWidget();

        if(startDateOpt.isPresent()
            && newLastDateOpt.isPresent()
            && startDateOpt.get().after(newLastDateOpt.get())) {
            setProjectTitleErrorState(false);
            return false;
        } else {
            setProjectTitleErrorState(true);
        }
        return true;
    }

    public void collectModifications(ConfigureProjectTask task) {
        Optional<Integer> newHourLimitOpt = getHourLimitFromWidget();
        Optional<Date> startDateOpt = getStartDateFromWidget();
        Optional<Date> newLastDateOpt = getLastEnddateInclusiveFromWidget();
        TimeFrameRounding.Strategy strategy = getRoundingStrategyFromWidget();

        task.withHourLimit(newHourLimitOpt.orNull());
        task.withStartDate(startDateOpt.orNull());
        task.withInclusiveEndDate(newLastDateOpt.orNull());
        task.withRoundingStrategy(strategy);
    }

    private Optional<Integer> getHourLimitFromWidget() {
        String text = hourLimitEditText.getText().toString();
        return Optional.fromNullable(!TextUtils.isEmpty(text) ? Integer.valueOf(text) : null);
    }

    private Optional<Date> getStartDateFromWidget() {
        Date startDate = (Date) startDateEditText.getTag();
        return Optional.fromNullable(startDate);
    }

    private Optional<Date> getLastEnddateInclusiveFromWidget() {
        Date lastDay = (Date) endDateEditText.getTag();
        return Optional.fromNullable(lastDay);
    }

    private TimeFrameRounding.Strategy getRoundingStrategyFromWidget() {
        List<String> strategyList = Arrays.asList(getResources().getStringArray(R.array.rouding_strategy_values));
        String strategyName = strategyList.get(roundingStrategySpinner.getSelectedItemPosition());
        return TimeFrameRounding.Strategy.valueOf(strategyName);
    }

    private void setProjectTitleErrorState(boolean isValid) {
        if (!isValid) {
            endDateEditText.setError(getString(R.string.error_start_after_end_date));
        } else {
            endDateEditText.setError(null);
        }
    }

    @Subscribe public void handleDatePicked(DatePickerFragment.DatePickedEvent event) {
        if (START_DATE.equals(event.getTopic())) {
            renderStartDate(event.getDate().isPresent() ? event.getDate().get().toDate() : null);
        } else if (END_DATE.equals(event.getTopic())) {
            renderEndDate(event.getDate().isPresent() ? event.getDate().get().toDate() : null);
        } else {
            throw new RuntimeException("Unexpected topic received: " + event.getTopic());
        }
    }
}
