package com.tastybug.timetracker.core.ui.projectdetails;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.core.model.Project;
import com.tastybug.timetracker.core.model.TrackingRecord;
import com.tastybug.timetracker.core.model.dao.ProjectDAO;
import com.tastybug.timetracker.core.model.dao.TrackingRecordDAO;
import com.tastybug.timetracker.core.task.tracking.checkin.CheckInEvent;
import com.tastybug.timetracker.core.task.tracking.checkout.CheckOutEvent;
import com.tastybug.timetracker.core.task.tracking.checkout.CheckOutTask;
import com.tastybug.timetracker.core.task.tracking.create.CreatedTrackingRecordEvent;
import com.tastybug.timetracker.core.task.tracking.update.UpdateTrackingRecordEvent;
import com.tastybug.timetracker.core.ui.delegate.CheckInPreconditionCheckDelegate;
import com.tastybug.timetracker.infrastructure.otto.OttoProvider;

public class TrackingControlPanelFragment extends Fragment implements View.OnClickListener {

    private TrackingControlPanelUI ui;
    private Optional<Project> currentProjectOpt = Optional.absent();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ui = new TrackingControlPanelUI(getActivity());
        return ui.inflateWidgets(inflater, container, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        new OttoProvider().getSharedBus().register(this);
        ui.startUiUpdater();
    }

    @Override
    public void onPause() {
        super.onPause();
        new OttoProvider().getSharedBus().unregister(this);
        ui.stopUiUpdater();
    }

    public void renderProject(Project project) {
        currentProjectOpt = Optional.of(project);
        Optional<TrackingRecord> ongoingTracking = new TrackingRecordDAO(getActivity()).getRunning(project.getUuid());
        if (project.isClosed()) {
            ui.visualizeProjectClosed();
        } else if (ongoingTracking.isPresent()) {
            ui.visualizeOngoingTracking(ongoingTracking);
        } else {
            ui.visualizeNoOngoingTracking();
        }
    }

    public void onClick(View v) {
        if (!currentProjectOpt.isPresent()) {
            Toast.makeText(getActivity(), R.string.message_no_project_selected, Toast.LENGTH_SHORT).show();
            return;
        }
        String projectUuid = currentProjectOpt.get().getUuid();
        Optional<TrackingRecord> ongoing = new TrackingRecordDAO(getActivity()).getRunning(projectUuid);
        if (ongoing.isPresent()) {
            new CheckOutTask(getActivity()).withTrackingRecordUuid(ongoing.get().getUuid()).run();
        } else {
            CheckInPreconditionCheckDelegate.aDelegate(getActivity()).startTracking(currentProjectOpt.get());
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleTrackingCreated(CreatedTrackingRecordEvent event) {
        if (currentProjectOpt.isPresent()
                && currentProjectOpt.get().getUuid().equals(event.getTrackingRecord().getProjectUuid())) {
            renderProject(new ProjectDAO(getActivity()).get(event.getTrackingRecord().getProjectUuid()).get());
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleCheckIn(CheckInEvent event) {
        if (currentProjectOpt.isPresent()
                && currentProjectOpt.get().getUuid().equals(event.getTrackingRecord().getProjectUuid())) {
            ui.visualizeOngoingTracking(Optional.of(event.getTrackingRecord()));
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleTrackingRecordUpdate(UpdateTrackingRecordEvent event) {
        if (currentProjectOpt.isPresent()
                && currentProjectOpt.get().getUuid().equals(event.getTrackingRecord().getProjectUuid())) {
            if (!new TrackingRecordDAO(getActivity()).getRunning(currentProjectOpt.get().getUuid()).isPresent()) {
                ui.visualizeNoOngoingTracking();
            }
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void handleCheckOut(CheckOutEvent event) {
        if (currentProjectOpt.isPresent()
                && currentProjectOpt.get().getUuid().equals(event.getTrackingRecord().getProjectUuid())) {
            if (!new TrackingRecordDAO(getActivity()).getRunning(currentProjectOpt.get().getUuid()).isPresent()) {
                ui.visualizeNoOngoingTracking();
            }
        }
    }
}
