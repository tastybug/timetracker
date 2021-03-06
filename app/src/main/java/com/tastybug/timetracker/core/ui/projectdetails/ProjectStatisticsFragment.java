package com.tastybug.timetracker.core.ui.projectdetails;

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
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingConfiguration;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.ProjectDAO;
import com.tastybug.timetracker.core.model.dao.TrackingConfigurationDAO;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.core.task.tracking.checkin.CheckInEvent;
import com.tastybug.timetracker.core.task.tracking.checkout.CheckOutEvent;
import com.tastybug.timetracker.core.task.tracking.checkout.CheckOutTask;
import com.tastybug.timetracker.core.task.tracking.create.CreatedTrackingRecordEvent;
import com.tastybug.timetracker.core.task.tracking.update.UpdateTrackingRecordEvent;
import com.tastybug.timetracker.core.ui.delegate.CheckInPreconditionCheckDelegate;
import com.tastybug.timetracker.core.ui.dialog.project.ConfirmDeleteProjectDialogFragment;
import com.tastybug.timetracker.core.ui.projectconfiguration.ProjectConfigurationActivity;
import com.tastybug.timetracker.extension.reporting.ui.CreateReportDialogFragment;
import com.tastybug.timetracker.extension.wifitracking.ui.ManageWifiTrackingDialogFragment;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;

public class ProjectStatisticsFragment extends Fragment implements View.OnClickListener {

    private ProjectStatisticsUI ui;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ui = new ProjectStatisticsUI(getActivity());
        return ui.inflateWidgets(inflater, container, this);
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
                        .forProject(currentProject)
                        .show(getFragmentManager(), getClass().getSimpleName());
                return true;
            case R.id.menu_configure_project:
                showProjectConfigurationActivity();
                return true;
            case R.id.menu_item_generate_report:
                showReportDialog();
                return true;
            case R.id.menu_wifi_tracking_configuration:
                showManageWifiTrackingSetupDialog();
                return true;
            default:
                super.onOptionsItemSelected(item);
                return false;
        }
    }

    public void showProjectDetailsFor(Project projectToShow) {
        this.currentProject = projectToShow;
        ui.renderExpiration(projectToShow);
        ui.renderProjectDuration(projectToShow);
        Optional<TrackingRecord> ongoingTracking = new TrackingRecordDAO(getActivity()).getRunning(currentProject.getUuid());
        if (currentProject.isClosed()) {
            ui.visualizeProjectClosed();
        } else if (ongoingTracking.isPresent()) {
            ui.visualizeOngoingTracking();
        } else {
            ui.visualizeNoOngoingTracking();
        }
    }

    private void showManageWifiTrackingSetupDialog() {
        ManageWifiTrackingDialogFragment.aDialog(currentProject.getUuid())
                .show(getFragmentManager(), ManageWifiTrackingDialogFragment.class.getSimpleName());
    }

    private void showReportDialog() {
        CreateReportDialogFragment
                .aDialog(getTrackingConfigurationForCurrentProject())
                .show(getFragmentManager(), getClass().getSimpleName());
    }

    private TrackingConfiguration getTrackingConfigurationForCurrentProject() {
        return new TrackingConfigurationDAO(getActivity()).getByProjectUuid(currentProject.getUuid()).get();
    }

    private void showProjectConfigurationActivity() {
        Intent intent = new Intent(getActivity(), ProjectConfigurationActivity.class);
        intent.putExtra(ProjectConfigurationActivity.PROJECT_UUID, currentProject.getUuid());
        startActivity(intent);
    }

    public void onClick(View v) {
        String projectUuid = currentProject.getUuid();
        Optional<TrackingRecord> ongoing = new TrackingRecordDAO(getActivity()).getRunning(projectUuid);
        if (ongoing.isPresent()) {
            new CheckOutTask(getActivity()).withTrackingRecordUuid(ongoing.get().getUuid()).run();
        } else {
            CheckInPreconditionCheckDelegate.aDelegate(getActivity()).startTracking(currentProject);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleTrackingStarted(CreatedTrackingRecordEvent event) {
        Project project = new ProjectDAO(getActivity()).get(event.getTrackingRecord().getProjectUuid()).get();
        showProjectDetailsFor(project);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleCheckIn(CheckInEvent event) {
        if (currentProject.getUuid().equals(event.getTrackingRecord().getProjectUuid())) {
            ui.visualizeOngoingTracking();
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleTrackingRecordUpdate(UpdateTrackingRecordEvent event) {
        Project project = new ProjectDAO(getActivity()).get(event.getTrackingRecord().getProjectUuid()).get();
        showProjectDetailsFor(project);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleCheckOut(CheckOutEvent event) {
        if (currentProject.getUuid().equals(event.getTrackingRecord().getProjectUuid())) {
            if (!new TrackingRecordDAO(getActivity()).getRunning(currentProject.getUuid()).isPresent()) {
                ui.visualizeNoOngoingTracking();
            }
        }
    }
}
