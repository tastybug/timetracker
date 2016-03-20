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

import com.google.common.base.Optional;
import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.database.dao.ProjectDAO;
import com.tastybug.timetracker.gui.dialog.ConfirmDeleteProjectDialogFragment;
import com.tastybug.timetracker.gui.project.configuration.ProjectConfigurationActivity;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.tracking.CreatedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.ModifiedTrackingRecordEvent;

public class ProjectStatisticsFragment extends Fragment {

    private ProjectStatisticsUI ui;

    private Optional<Project> currentProjectOpt;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ui = new ProjectStatisticsUI(getActivity());
        return ui.inflateWidgets(inflater, container);
    }

    @Override
    public void onResume() {
        super.onResume();
        new OttoProvider().getSharedBus().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        new OttoProvider().getSharedBus().unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete_project:
                ConfirmDeleteProjectDialogFragment.aDialog()
                        .forProject(currentProjectOpt.get())
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
        intent.putExtra(ProjectConfigurationActivity.PROJECT_UUID, currentProjectOpt.get().getUuid());
        startActivity(intent);
    }

    public void showProjectDetailsFor(Project project) {
        this.currentProjectOpt = Optional.of(project);
        if(!project.hasContext()) {
            project.setContext(getActivity());
        }
        ui.renderProjectTimeFrame(Optional.of(project));
        ui.renderProjectDuration(Optional.of(project));
    }

    public void showNoProject() {
        this.currentProjectOpt = Optional.absent();
        ui.renderProjectTimeFrame(Optional.<Project>absent());
        ui.renderProjectDuration(Optional.<Project>absent());
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
