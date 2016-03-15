package com.tastybug.timetracker.gui.project.detail;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.squareup.otto.Subscribe;
import com.tastybug.timetracker.R;
import com.tastybug.timetracker.database.dao.ProjectDAO;
import com.tastybug.timetracker.database.dao.TrackingRecordDAO;
import com.tastybug.timetracker.gui.delegate.TrackingDelegate;
import com.tastybug.timetracker.model.Project;
import com.tastybug.timetracker.model.TrackingRecord;
import com.tastybug.timetracker.task.OttoProvider;
import com.tastybug.timetracker.task.tracking.CreatedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.KickStartedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.KickStopTrackingRecordTask;
import com.tastybug.timetracker.task.tracking.KickStoppedTrackingRecordEvent;
import com.tastybug.timetracker.task.tracking.ModifiedTrackingRecordEvent;

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
        if(ongoingTracking.isPresent()) {
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
            KickStopTrackingRecordTask.aTask(getActivity()).withProjectUuid(projectUuid).execute();
        } else {
            TrackingDelegate.aDelegate(getActivity()).startTracking(currentProjectOpt.get());
        }
    }

    @Subscribe public void handleTrackingCreated(CreatedTrackingRecordEvent event) {
        if (currentProjectOpt.isPresent()
                && currentProjectOpt.get().getUuid().equals(event.getTrackingRecord().getProjectUuid())) {
            renderProject(new ProjectDAO(getActivity()).get(event.getTrackingRecord().getProjectUuid()).get());
        }
    }

    @Subscribe public void handleTrackingKickStarted(KickStartedTrackingRecordEvent event) {
        if (currentProjectOpt.isPresent()
                && currentProjectOpt.get().getUuid().equals(event.getTrackingRecord().getProjectUuid())) {
            ui.visualizeOngoingTracking(Optional.of(event.getTrackingRecord()));
        }
    }

    @Subscribe public void handleTrackingModified(ModifiedTrackingRecordEvent event) {
        if (currentProjectOpt.isPresent()
                && currentProjectOpt.get().getUuid().equals(event.getTrackingRecord().getProjectUuid())) {
            if (!new TrackingRecordDAO(getActivity()).getRunning(currentProjectOpt.get().getUuid()).isPresent()) {
                ui.visualizeNoOngoingTracking();
            }
        }
    }

    @Subscribe public void handleTrackingKickStopped(KickStoppedTrackingRecordEvent event) {
        if (currentProjectOpt.isPresent()
                && currentProjectOpt.get().getUuid().equals(event.getTrackingRecord().getProjectUuid())) {
            if (!new TrackingRecordDAO(getActivity()).getRunning(currentProjectOpt.get().getUuid()).isPresent()) {
                ui.visualizeNoOngoingTracking();
            }
        }
    }
}
