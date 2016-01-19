package com.tastybug.timetracker.gui.projectconfiguration;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.common.base.Optional;
import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.ProjectTimeConstraints;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.project.ConfigureProjectTask;

import java.util.Date;

public class ProjectTimeConstraintsConfigurationFragment extends Fragment {

    private static final String HOUR_LIMIT = "HOUR_LIMIT";
    private static final String END_DATE = "END_DATE";

    private EditText hourLimitEditText;
    private EditText endDateEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_time_constraints_configuration, container);

        hourLimitEditText = (EditText) view.findViewById(R.id.project_hour_limit);
        endDateEditText = (EditText) view.findViewById(R.id.project_end_date);

        if (savedInstanceState != null) {
            renderHourLimit(savedInstanceState.containsKey(HOUR_LIMIT) ? savedInstanceState.getInt(HOUR_LIMIT) : null);
            renderEndDate((Date) savedInstanceState.getSerializable(END_DATE));
        }

        endDateEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setTopic("endDate");
                newFragment.show(getFragmentManager(), DatePickerFragment.class.getSimpleName());
            }
        });
        new OttoProvider().getSharedBus().register(this);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Optional<Integer> hourLimitOpt = getHourLimitFromWidget();
        if(hourLimitOpt.isPresent()) {
            outState.putInt(HOUR_LIMIT, hourLimitOpt.get());
        }
    }

    public void showProjectTimeConstraints(ProjectTimeConstraints projectTimeConstraints) {
        renderHourLimit(projectTimeConstraints.getHourLimit().orNull());
        renderEndDate(projectTimeConstraints.getEndDateAsInclusive().orNull());
    }

    private void renderHourLimit(Integer hourLimit) {
        hourLimitEditText.setText(hourLimit != null ? hourLimit + "" : "");
    }

    private void renderEndDate(Date date) {
        if (date != null) {
            endDateEditText.setText("Endet am " + date.toString());
            endDateEditText.setTag(date);
        } else {
            endDateEditText.setText("");
            endDateEditText.setTag(null);
        }
    }

    public void collectModifications(ConfigureProjectTask task) {
        Optional<Integer> newHourLimitOpt = getHourLimitFromWidget();
        Optional<Date> newLastDateOpt = getLastEnddateInclusiveFromWidget();

        task.withHourLimit(newHourLimitOpt.orNull());
        task.withInclusiveEndDate(newLastDateOpt.orNull());
    }

    private Optional<Integer> getHourLimitFromWidget() {
        String text = hourLimitEditText.getText().toString();
        return Optional.fromNullable(!TextUtils.isEmpty(text) ? Integer.valueOf(text) : null);
    }

    private Optional<Date> getLastEnddateInclusiveFromWidget() {
        Date lastDay = (Date) endDateEditText.getTag();
        return Optional.fromNullable(lastDay);
    }

    @Subscribe
    public void handleDatePicked(DatePickerFragment.DatePickedEvent event) {
        renderEndDate(event.getDate().isPresent() ? event.getDate().get().toDate() : null);
    }
}
