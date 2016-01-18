package com.tastybug.timetracker.gui.projectconfiguration;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.common.base.Optional;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.model.ProjectTimeConstraints;
import com.tastybug.timetracker.task.project.ConfigureProjectTask;

public class ProjectTimeConstraintsConfigurationFragment extends Fragment {

    private static final String HOUR_LIMIT = "HOUR_LIMIT";

    private EditText hourLimitEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_time_constraints_configuration, container);

        hourLimitEditText = (EditText) view.findViewById(R.id.project_hour_limit);

        if (savedInstanceState != null) {
            renderTimeConstraint(savedInstanceState.containsKey(HOUR_LIMIT) ? savedInstanceState.getInt(HOUR_LIMIT) : null);
        }

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
        renderTimeConstraint(projectTimeConstraints.getHourLimit().orNull());
    }

    private void renderTimeConstraint(Integer hourLimit) {
        hourLimitEditText.setText(hourLimit + "");
    }

    public void collectModifications(ConfigureProjectTask task) {
        Optional<Integer> newHourLimit = getHourLimitFromWidget();

        if (newHourLimit.isPresent()) {
            task.withHourLimit(newHourLimit.get());
        }
    }

    private Optional<Integer> getHourLimitFromWidget() {
        String text = hourLimitEditText.getText().toString();
        return Optional.fromNullable(!TextUtils.isEmpty(text) ? Integer.valueOf(text) : null);
    }
}
