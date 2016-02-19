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

import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.database.dao.ProjectDAO;
import com.tastybug.timetracker.gui.project.configuration.ProjectConfigurationActivity;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.statistics.StatisticProjectDuration;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.project.DeleteProjectTask;
import com.tastybug.timetracker.task.tracking.TrackingRecordCreatedEvent;
import com.tastybug.timetracker.task.tracking.TrackingRecordModifiedEvent;

public class ProjectStatisticsFragment extends Fragment {

    private TextView someTextView;
    private String currentProjectUuid;

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

        someTextView = (TextView) rootview.findViewById(R.id.lineOne);

        new OttoProvider().getSharedBus().register(this);

        return rootview;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete_project:
                DeleteProjectTask.aTask(getActivity()).withProjectUuid(currentProjectUuid).execute();
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
        intent.putExtra(ProjectConfigurationActivity.PROJECT_UUID, currentProjectUuid);
        startActivity(intent);
    }

    public void showProjectDetailsFor(Project project) {
        this.currentProjectUuid = project.getUuid();
        if(!project.hasContext()) {
            project.setContext(getActivity());
        }
        someTextView.setText(new StatisticProjectDuration(project.getTrackingConfiguration(), project.getTrackingRecords()).get().getStandardSeconds() + " Seconds");
    }

    public void showNoProject() {
        this.currentProjectUuid = null;
        someTextView.setText("//Nothing selected for project statistics");
    }

    @Subscribe public void handleTrackingStarted(TrackingRecordCreatedEvent event) {
        Project project = new ProjectDAO(getActivity()).get(event.getTrackingRecord().getProjectUuid()).get();
        project.setContext(getActivity());
        showProjectDetailsFor(project);
    }

    @Subscribe public void handleTrackingModified(TrackingRecordModifiedEvent event) {
        Project project = new ProjectDAO(getActivity()).get(event.getTrackingRecord().getProjectUuid()).get();
        project.setContext(getActivity());
        showProjectDetailsFor(project);
    }
}
