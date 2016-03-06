package com.tastybug.timetracker.gui.project.detail;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.database.dao.ProjectDAO;
import com.tastybug.timetracker.gui.project.configuration.ProjectConfigurationActivity;
import com.tastybug.timetracker.gui.shared.DialogConfirmDeleteProject;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingConfiguration;
import com.tastybug.timetracker.model.statistics.StatisticProjectDuration;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.tracking.CreatedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.ModifiedTrackingRecordEvent;

import org.joda.time.Duration;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProjectStatisticsFragment extends Fragment {

    private TextView projectTimeFrameTextView, projectDurationTextView;
    private Project currentProject;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_project_statistics, menu);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        new OttoProvider().getSharedBus().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_project_statistics, container);

        projectTimeFrameTextView = (TextView) rootview.findViewById(R.id.project_info_time_frame);
        projectDurationTextView = (TextView) rootview.findViewById(R.id.project_info_current_project_duration);

        new OttoProvider().getSharedBus().register(this);

        return rootview;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete_project:
                DialogConfirmDeleteProject.aDialog()
                        .forProject(currentProject)
                        .show(getFragmentManager(),getClass().getSimpleName());
                return true;
            case R.id.menu_configure_project:
                showProjectConfigurationActivity();
                return true;
            default:
                super.onOptionsItemSelected(item);
                return false;
        }
    }

    private void showProjectConfigurationActivity() {
        Intent intent = new Intent(getActivity(), ProjectConfigurationActivity.class);
        intent.putExtra(ProjectConfigurationActivity.PROJECT_UUID, currentProject.getUuid());
        startActivity(intent);
    }

    public void showProjectDetailsFor(Project project) {
        this.currentProject = project;
        if(!project.hasContext()) {
            project.setContext(getActivity());
        }
        renderProjektTimeframe(Optional.of(project));
        renderProjectDuration(Optional.of(project));
    }

    public void showNoProject() {
        this.currentProject = null;
        renderProjektTimeframe(Optional.<Project>absent());
        renderProjectDuration(Optional.<Project>absent());
    }

    public void renderProjektTimeframe(Optional<Project> project) {
        if (project.isPresent()) {
            TrackingConfiguration configuration = project.get().getTrackingConfiguration();
            if (configuration.getEnd().isPresent()) { // <- theres an end date that limits the time frame
                long remainingDays = getEffectiveRemainingProjectDays(configuration.getStart(), configuration.getEnd().get());
                if (remainingDays == 0) {
                    projectTimeFrameTextView.setText(R.string.project_ends_today);
                } else {
                    String endDateString = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT).format(configuration.getEndDateAsInclusive().get());
                    projectTimeFrameTextView.setText(getString(R.string.project_remaining_days_X_until_Y, remainingDays, endDateString));
                }
            } else {
                projectTimeFrameTextView.setText("");
            }
        } else {
            projectTimeFrameTextView.setText("");
        }
    }

    public void renderProjectDuration(Optional<Project> projectOpt) {
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            TrackingConfiguration configuration = project.getTrackingConfiguration();
            Duration duration = new StatisticProjectDuration(configuration, project.getTrackingRecords()).get();
            if (configuration.getHourLimit().isPresent()) {
                projectDurationTextView.setText(getString(R.string.X_recorded_hours_so_far_from_a_total_of_Y,
                        duration.getStandardHours(),
                        configuration.getHourLimit().get()));
            } else {
                projectDurationTextView.setText(getString(R.string.X_recorded_hours_so_far,
                        duration.getStandardHours()));
            }
        } else {
            projectDurationTextView.setText("");
        }
    }

    private long getEffectiveRemainingProjectDays(Optional<Date> startDateOpt, Date endDateExclusive) {
        // if the start date lies in the future, only count from that date onwards
        // otherwise count from NOW
        Date start = startDateOpt.isPresent() && startDateOpt.get().after(new Date()) ? startDateOpt.get() : new Date();
        Duration duration = new Duration(start.getTime(), endDateExclusive.getTime());
        return duration.getStandardDays();
    }

    @Subscribe public void handleTrackingStarted(CreatedTrackingRecordEvent event) {
        Project project = new ProjectDAO(getActivity()).get(event.getTrackingRecord().getProjectUuid()).get();
        project.setContext(getActivity());
        showProjectDetailsFor(project);
    }

    @Subscribe public void handleTrackingModified(ModifiedTrackingRecordEvent event) {
        Project project = new ProjectDAO(getActivity()).get(event.getTrackingRecord().getProjectUuid()).get();
        project.setContext(getActivity());
        showProjectDetailsFor(project);
    }
}
